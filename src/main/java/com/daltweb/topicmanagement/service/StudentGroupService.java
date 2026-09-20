package com.daltweb.topicmanagement.service;

import com.daltweb.topicmanagement.constant.GroupRole;
import com.daltweb.topicmanagement.constant.GroupStatus;
import com.daltweb.topicmanagement.constant.PeriodStatus;
import com.daltweb.topicmanagement.constant.TopicApprovalStatus;
import com.daltweb.topicmanagement.dto.group.*;
import com.daltweb.topicmanagement.entity.*;
import com.daltweb.topicmanagement.exception.BadRequestException;
import com.daltweb.topicmanagement.exception.ConflictException;
import com.daltweb.topicmanagement.exception.ForbiddenException;
import com.daltweb.topicmanagement.exception.ResourceNotFoundException;
import com.daltweb.topicmanagement.repository.*;
import com.daltweb.topicmanagement.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class StudentGroupService {

    private final StudentGroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;
    private final RegistrationPeriodRepository periodRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final TopicService topicService;

    public StudentGroupService(StudentGroupRepository groupRepository, GroupMemberRepository memberRepository, RegistrationPeriodRepository periodRepository, TopicRepository topicRepository, UserRepository userRepository, TopicService topicService) {
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
        this.periodRepository = periodRepository;
        this.topicRepository = topicRepository;
        this.userRepository = userRepository;
        this.topicService = topicService;
    }

    @Transactional(readOnly = true)
    public List<StudentGroupDto> getGroups(Long periodId, GroupStatus status) {
        return groupRepository.filterGroups(periodId, status).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StudentGroupDto getGroupById(Long id) {
        StudentGroup group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhóm với ID: " + id));
        return mapToDto(group);
    }

    @Transactional(readOnly = true)
    public StudentGroupDto getMyGroupInPeriod(Long periodId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        GroupMember membership = memberRepository.findByStudentIdAndPeriodId(currentUserId, periodId)
                .orElseThrow(() -> new ResourceNotFoundException("Bạn chưa tham gia nhóm nào trong đợt đăng ký này!"));
        return mapToDto(membership.getGroup());
    }

    @Transactional
    public StudentGroupDto createGroup(GroupCreateRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin sinh viên"));

        RegistrationPeriod period = periodRepository.findById(request.getPeriodId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đợt đăng ký"));

        if (memberRepository.existsByStudentIdAndPeriodId(currentUserId, period.getId())) {
            throw new ConflictException("Bạn đã tham gia một nhóm khác trong đợt đăng ký này!");
        }

        long groupCount = groupRepository.count() + 1;
        String groupCode = String.format("GRP-%d-%03d", period.getId(), groupCount);

        StudentGroup group = StudentGroup.builder()
                .code(groupCode)
                .name(request.getName())
                .period(period)
                .status(GroupStatus.FORMING)
                .members(new ArrayList<>())
                .build();

        StudentGroup savedGroup = groupRepository.save(group);

        GroupMember leader = GroupMember.builder()
                .group(savedGroup)
                .student(currentUser)
                .groupRole(GroupRole.LEADER)
                .build();
        memberRepository.save(leader);
        savedGroup.getMembers().add(leader);

        if (request.getMemberStudentIds() != null && !request.getMemberStudentIds().isEmpty()) {
            if (request.getMemberStudentIds().size() > 2) {
                throw new BadRequestException("Nhóm chỉ có tối đa 3 thành viên (1 nhóm trưởng + tối đa 2 thành viên)!");
            }
            for (Long memberId : request.getMemberStudentIds()) {
                if (Objects.equals(memberId, currentUserId)) continue;
                addMemberInternal(savedGroup, memberId);
            }
        }

        return mapToDto(savedGroup);
    }

    @Transactional
    public StudentGroupDto addMember(Long groupId, AddMemberRequest request) {
        StudentGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhóm với ID: " + groupId));

        validateLeaderPermission(group, "Chỉ nhóm trưởng mới có quyền thêm thành viên vào nhóm!");

        if (group.getMembers().size() >= 3) {
            throw new BadRequestException("Nhóm đã đủ số lượng tối đa (3 sinh viên)!");
        }

        addMemberInternal(group, request.getStudentId());
        return mapToDto(group);
    }

    private void addMemberInternal(StudentGroup group, Long studentId) {
        if (memberRepository.existsByStudentIdAndPeriodId(studentId, group.getPeriod().getId())) {
            throw new ConflictException("Sinh viên (ID: " + studentId + ") đã tham gia một nhóm khác trong đợt này!");
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sinh viên với ID: " + studentId));

        GroupMember member = GroupMember.builder()
                .group(group)
                .student(student)
                .groupRole(GroupRole.MEMBER)
                .build();

        memberRepository.save(member);
        group.getMembers().add(member);
    }

    @Transactional
    public void removeMember(Long groupId, Long studentId) {
        StudentGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhóm với ID: " + groupId));

        validateLeaderPermission(group, "Chỉ nhóm trưởng mới có quyền xóa thành viên!");

        GroupMember member = memberRepository.findByGroupIdAndStudentId(groupId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên không thuộc nhóm này"));

        if (member.getGroupRole() == GroupRole.LEADER) {
            throw new BadRequestException("Không thể xóa nhóm trưởng khỏi nhóm!");
        }

        group.getMembers().remove(member);
        memberRepository.delete(member);
    }

    @Transactional
    public StudentGroupDto registerTopic(Long groupId, RegisterTopicRequest request) {
        StudentGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhóm"));

        validateLeaderPermission(group, "Chỉ nhóm trưởng mới có quyền đăng ký đề tài cho nhóm!");

        RegistrationPeriod period = group.getPeriod();
        LocalDateTime now = LocalDateTime.now();

        if (period.getStatus() != PeriodStatus.PHASE2_STUDENT_REGISTRATION &&
            (now.isBefore(period.getStudentRegStart()) || now.isAfter(period.getStudentRegEnd()))) {
            throw new BadRequestException("Hiện tại không nằm trong thời gian cho phép sinh viên đăng ký đề tài!");
        }

        if (group.getTopic() != null) {
            throw new ConflictException("Nhóm đã đăng ký đề tài trước đó! Vui lòng hủy đăng ký đề tài cũ trước.");
        }

        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đề tài"));

        if (!Objects.equals(topic.getPeriod().getId(), period.getId())) {
            throw new BadRequestException("Đề tài không thuộc đợt đăng ký này!");
        }

        if (topic.getApprovalStatus() != TopicApprovalStatus.APPROVED) {
            throw new BadRequestException("Đề tài này chưa được Trưởng bộ môn phê duyệt!");
        }

        if (topic.getIsAssigned()) {
            throw new ConflictException("Đề tài này đã được một nhóm sinh viên khác đăng ký!");
        }

        topic.setIsAssigned(true);
        topicRepository.save(topic);

        group.setTopic(topic);
        group.setStatus(GroupStatus.REGISTERED);
        StudentGroup saved = groupRepository.save(group);

        return mapToDto(saved);
    }

    @Transactional
    public StudentGroupDto cancelTopicRegistration(Long groupId) {
        StudentGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhóm"));

        validateLeaderPermission(group, "Chỉ nhóm trưởng mới có quyền hủy đăng ký đề tài!");

        if (group.getTopic() == null) {
            throw new BadRequestException("Nhóm chưa đăng ký đề tài nào!");
        }

        RegistrationPeriod period = group.getPeriod();
        LocalDateTime now = LocalDateTime.now();
        if (period.getStatus() != PeriodStatus.PHASE2_STUDENT_REGISTRATION &&
            (now.isBefore(period.getStudentRegStart()) || now.isAfter(period.getStudentRegEnd()))) {
            throw new BadRequestException("Đã hết thời hạn đăng ký/hủy đề tài của sinh viên!");
        }

        Topic topic = group.getTopic();
        topic.setIsAssigned(false);
        topicRepository.save(topic);

        group.setTopic(null);
        group.setStatus(GroupStatus.FORMING);
        StudentGroup saved = groupRepository.save(group);

        return mapToDto(saved);
    }

    @Transactional
    public StudentGroupDto submitReport(Long groupId, SubmitReportRequest request) {
        StudentGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhóm"));

        validateLeaderPermission(group, "Việc nộp báo cáo đề tài chỉ được thực hiện bởi nhóm trưởng!");

        if (group.getTopic() == null) {
            throw new BadRequestException("Nhóm chưa đăng ký đề tài, không thể nộp báo cáo!");
        }

        group.setReportUrl(request.getReportUrl());
        group.setReportNote(request.getReportNote());
        group.setSubmittedAt(LocalDateTime.now());
        group.setStatus(GroupStatus.SUBMITTED);

        StudentGroup saved = groupRepository.save(group);
        return mapToDto(saved);
    }

    private void validateLeaderPermission(StudentGroup group, String errorMessage) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean isLeader = group.getMembers().stream()
                .anyMatch(m -> Objects.equals(m.getStudent().getId(), currentUserId) && m.getGroupRole() == GroupRole.LEADER);

        if (!isLeader) {
            throw new ForbiddenException(errorMessage);
        }
    }

    public StudentGroupDto mapToDto(StudentGroup group) {
        List<GroupMemberDto> memberDtos = group.getMembers().stream()
                .map(m -> GroupMemberDto.builder()
                        .id(m.getId())
                        .studentId(m.getStudent().getId())
                        .studentName(m.getStudent().getFullName())
                        .studentCode(m.getStudent().getCode())
                        .studentEmail(m.getStudent().getEmail())
                        .studentPhone(m.getStudent().getPhone())
                        .studentClass(m.getStudent().getStudentClass())
                        .groupRole(m.getGroupRole())
                        .joinedAt(m.getJoinedAt())
                        .build())
                .collect(Collectors.toList());

        GroupMember leader = group.getMembers().stream()
                .filter(m -> m.getGroupRole() == GroupRole.LEADER)
                .findFirst().orElse(null);

        return StudentGroupDto.builder()
                .id(group.getId())
                .code(group.getCode())
                .name(group.getName())
                .periodId(group.getPeriod() != null ? group.getPeriod().getId() : null)
                .periodName(group.getPeriod() != null ? group.getPeriod().getName() : null)
                .topicId(group.getTopic() != null ? group.getTopic().getId() : null)
                .topicTitleVi(group.getTopic() != null ? group.getTopic().getTitleVi() : null)
                .topic(group.getTopic() != null ? topicService.mapToDto(group.getTopic()) : null)
                .status(group.getStatus())
                .reportUrl(group.getReportUrl())
                .reportNote(group.getReportNote())
                .submittedAt(group.getSubmittedAt())
                .leaderId(leader != null ? leader.getStudent().getId() : null)
                .leaderName(leader != null ? leader.getStudent().getFullName() : null)
                .members(memberDtos)
                .createdAt(group.getCreatedAt())
                .build();
    }
}
