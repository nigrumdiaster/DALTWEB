package com.daltweb.topicmanagement.service;

import com.daltweb.topicmanagement.constant.CouncilRole;
import com.daltweb.topicmanagement.constant.GraderRole;
import com.daltweb.topicmanagement.dto.council.*;
import com.daltweb.topicmanagement.entity.*;
import com.daltweb.topicmanagement.exception.BadRequestException;
import com.daltweb.topicmanagement.exception.ForbiddenException;
import com.daltweb.topicmanagement.exception.ResourceNotFoundException;
import com.daltweb.topicmanagement.repository.*;
import com.daltweb.topicmanagement.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DefenseCouncilService {

    private final DefenseCouncilRepository councilRepository;
    private final CouncilMemberRepository councilMemberRepository;
    private final CouncilTopicAssignmentRepository assignmentRepository;
    private final RegistrationPeriodRepository periodRepository;
    private final TopicRepository topicRepository;
    private final StudentGroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GradeRepository gradeRepository;

    public DefenseCouncilService(DefenseCouncilRepository councilRepository, CouncilMemberRepository councilMemberRepository, CouncilTopicAssignmentRepository assignmentRepository, RegistrationPeriodRepository periodRepository, TopicRepository topicRepository, StudentGroupRepository groupRepository, UserRepository userRepository, GradeRepository gradeRepository) {
        this.councilRepository = councilRepository;
        this.councilMemberRepository = councilMemberRepository;
        this.assignmentRepository = assignmentRepository;
        this.periodRepository = periodRepository;
        this.topicRepository = topicRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.gradeRepository = gradeRepository;
    }

    @Transactional(readOnly = true)
    public List<DefenseCouncilDto> getCouncilsByPeriod(Long periodId) {
        return councilRepository.findByPeriodId(periodId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DefenseCouncilDto getCouncilById(Long id) {
        DefenseCouncil council = councilRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hội đồng với ID: " + id));
        return mapToDto(council);
    }

    @Transactional
    public DefenseCouncilDto createCouncil(CouncilCreateRequest request) {
        RegistrationPeriod period = periodRepository.findById(request.getPeriodId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đợt đăng ký"));

        if (request.getMembers() == null || request.getMembers().size() < 3 || request.getMembers().size() > 5) {
            throw new BadRequestException("Hội đồng phản biện/bảo vệ phải có từ 3 đến 5 giảng viên!");
        }

        long chairmanCount = request.getMembers().stream().filter(m -> m.getCouncilRole() == CouncilRole.CHAIRMAN).count();
        long secretaryCount = request.getMembers().stream().filter(m -> m.getCouncilRole() == CouncilRole.SECRETARY).count();

        if (chairmanCount != 1) {
            throw new BadRequestException("Hội đồng bắt buộc phải có đúng 01 Chủ tịch hội đồng!");
        }
        if (secretaryCount != 1) {
            throw new BadRequestException("Hội đồng bắt buộc phải có đúng 01 Thư ký hội đồng!");
        }

        Set<Long> lecturerIds = new HashSet<>();
        for (CouncilMemberInput memberInput : request.getMembers()) {
            if (!lecturerIds.add(memberInput.getLecturerId())) {
                throw new BadRequestException("Một giảng viên không thể xuất hiện 2 lần trong cùng một hội đồng!");
            }
        }

        long count = councilRepository.count() + 1;
        String councilCode = String.format("HD-%d-%02d", period.getId(), count);

        DefenseCouncil council = DefenseCouncil.builder()
                .code(councilCode)
                .name(request.getName())
                .period(period)
                .defenseDate(request.getDefenseDate())
                .location(request.getLocation())
                .notes(request.getNotes())
                .members(new ArrayList<>())
                .assignedTopics(new ArrayList<>())
                .build();

        DefenseCouncil savedCouncil = councilRepository.save(council);

        for (CouncilMemberInput memberInput : request.getMembers()) {
            User lecturer = userRepository.findById(memberInput.getLecturerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên với ID: " + memberInput.getLecturerId()));

            CouncilMember councilMember = CouncilMember.builder()
                    .council(savedCouncil)
                    .lecturer(lecturer)
                    .councilRole(memberInput.getCouncilRole())
                    .build();

            councilMemberRepository.save(councilMember);
            savedCouncil.getMembers().add(councilMember);
        }

        return mapToDto(savedCouncil);
    }

    @Transactional
    public DefenseCouncilDto assignTopicsToCouncil(Long councilId, AssignTopicsToCouncilRequest request) {
        DefenseCouncil council = councilRepository.findById(councilId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hội đồng với ID: " + councilId));

        Set<Long> councilLecturerIds = council.getMembers().stream()
                .map(m -> m.getLecturer().getId())
                .collect(Collectors.toSet());

        for (Long topicId : request.getTopicIds()) {
            Topic topic = topicRepository.findById(topicId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đề tài với ID: " + topicId));

            if (councilLecturerIds.contains(topic.getPrimaryAdvisor().getId())) {
                throw new BadRequestException(String.format("Không thể gán đề tài '%s' cho hội đồng vì GVHD chính (%s) là thành viên của hội đồng này!",
                        topic.getTitleVi(), topic.getPrimaryAdvisor().getFullName()));
            }

            if (topic.getCoAdvisor() != null && councilLecturerIds.contains(topic.getCoAdvisor().getId())) {
                throw new BadRequestException(String.format("Không thể gán đề tài '%s' cho hội đồng vì GV đồng hướng dẫn (%s) là thành viên của hội đồng này!",
                        topic.getTitleVi(), topic.getCoAdvisor().getFullName()));
            }

            StudentGroup group = groupRepository.findByTopicId(topic.getId()).orElse(null);

            if (!assignmentRepository.existsByCouncilIdAndTopicId(councilId, topicId)) {
                CouncilTopicAssignment assignment = CouncilTopicAssignment.builder()
                        .council(council)
                        .topic(topic)
                        .studentGroup(group)
                        .defenseTime(request.getDefenseTime())
                        .notes(request.getNotes())
                        .build();

                assignmentRepository.save(assignment);
                council.getAssignedTopics().add(assignment);
            }
        }

        return mapToDto(council);
    }

    @Transactional
    public void gradeTopicInCouncil(Long councilId, CouncilGradingRequest request) {
        DefenseCouncil council = councilRepository.findById(councilId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hội đồng"));

        Long currentUserId = SecurityUtils.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        CouncilMember member = council.getMembers().stream()
                .filter(m -> Objects.equals(m.getLecturer().getId(), currentUserId))
                .findFirst()
                .orElseThrow(() -> new ForbiddenException("Bạn không phải là thành viên của hội đồng này!"));

        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đề tài"));

        if (Objects.equals(topic.getPrimaryAdvisor().getId(), currentUserId) ||
            (topic.getCoAdvisor() != null && Objects.equals(topic.getCoAdvisor().getId(), currentUserId))) {
            throw new BadRequestException("Bạn là GVHD của đề tài này nên không thể chấm điểm!");
        }

        StudentGroup group = groupRepository.findByTopicId(topic.getId()).orElse(null);
        if (group == null) {
            throw new BadRequestException("Đề tài chưa có nhóm sinh viên thực hiện!");
        }

        GraderRole graderRole;
        if (member.getCouncilRole() == CouncilRole.CHAIRMAN) {
            graderRole = GraderRole.COUNCIL_CHAIRMAN;
        } else if (member.getCouncilRole() == CouncilRole.SECRETARY) {
            graderRole = GraderRole.COUNCIL_SECRETARY;
        } else {
            graderRole = GraderRole.COUNCIL_MEMBER;
        }

        Grade grade = gradeRepository.findByTopicIdAndGraderIdAndGraderRole(topic.getId(), currentUserId, graderRole)
                .orElse(Grade.builder()
                        .period(topic.getPeriod())
                        .topic(topic)
                        .studentGroup(group)
                        .grader(currentUser)
                        .graderRole(graderRole)
                        .build());

        grade.setScore(request.getScore());
        grade.setFeedback(request.getFeedback());
        grade.setGradedAt(LocalDateTime.now());
        gradeRepository.save(grade);
    }

    public DefenseCouncilDto mapToDto(DefenseCouncil council) {
        List<CouncilMemberDto> memberDtos = council.getMembers().stream()
                .map(m -> CouncilMemberDto.builder()
                        .id(m.getId())
                        .lecturerId(m.getLecturer().getId())
                        .lecturerName(m.getLecturer().getFullName())
                        .lecturerCode(m.getLecturer().getCode())
                        .lecturerEmail(m.getLecturer().getEmail())
                        .departmentId(m.getLecturer().getDepartment() != null ? m.getLecturer().getDepartment().getId() : null)
                        .departmentName(m.getLecturer().getDepartment() != null ? m.getLecturer().getDepartment().getName() : null)
                        .councilRole(m.getCouncilRole())
                        .build())
                .collect(Collectors.toList());

        List<CouncilTopicAssignmentDto> topicDtos = council.getAssignedTopics().stream()
                .map(a -> CouncilTopicAssignmentDto.builder()
                        .id(a.getId())
                        .topicId(a.getTopic().getId())
                        .topicCode(a.getTopic().getCode())
                        .topicTitleVi(a.getTopic().getTitleVi())
                        .primaryAdvisorId(a.getTopic().getPrimaryAdvisor().getId())
                        .primaryAdvisorName(a.getTopic().getPrimaryAdvisor().getFullName())
                        .coAdvisorId(a.getTopic().getCoAdvisor() != null ? a.getTopic().getCoAdvisor().getId() : null)
                        .coAdvisorName(a.getTopic().getCoAdvisor() != null ? a.getTopic().getCoAdvisor().getFullName() : null)
                        .studentGroupId(a.getStudentGroup() != null ? a.getStudentGroup().getId() : null)
                        .studentGroupName(a.getStudentGroup() != null ? a.getStudentGroup().getName() : null)
                        .defenseTime(a.getDefenseTime())
                        .notes(a.getNotes())
                        .build())
                .collect(Collectors.toList());

        return DefenseCouncilDto.builder()
                .id(council.getId())
                .code(council.getCode())
                .name(council.getName())
                .periodId(council.getPeriod() != null ? council.getPeriod().getId() : null)
                .periodName(council.getPeriod() != null ? council.getPeriod().getName() : null)
                .defenseDate(council.getDefenseDate())
                .location(council.getLocation())
                .notes(council.getNotes())
                .members(memberDtos)
                .assignedTopics(topicDtos)
                .createdAt(council.getCreatedAt())
                .build();
    }
}
