package com.daltweb.topicmanagement.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import com.daltweb.topicmanagement.constant.PeriodStatus;
import com.daltweb.topicmanagement.constant.GroupStatus;
import com.daltweb.topicmanagement.constant.RoleType;
import com.daltweb.topicmanagement.constant.TopicApprovalStatus;
import com.daltweb.topicmanagement.dto.topic.TopicApprovalRequest;
import com.daltweb.topicmanagement.dto.topic.TopicCreateRequest;
import com.daltweb.topicmanagement.dto.topic.TopicDto;
import com.daltweb.topicmanagement.dto.topic.TopicUpdateRequest;
import com.daltweb.topicmanagement.entity.Department;
import com.daltweb.topicmanagement.entity.RegistrationPeriod;
import com.daltweb.topicmanagement.entity.Topic;
import com.daltweb.topicmanagement.entity.User;
import com.daltweb.topicmanagement.entity.StudentGroup;
import com.daltweb.topicmanagement.exception.BadRequestException;
import com.daltweb.topicmanagement.exception.ForbiddenException;
import com.daltweb.topicmanagement.exception.ResourceNotFoundException;
import com.daltweb.topicmanagement.repository.DepartmentRepository;
import com.daltweb.topicmanagement.repository.RegistrationPeriodRepository;
import com.daltweb.topicmanagement.repository.StudentGroupRepository;
import com.daltweb.topicmanagement.repository.TopicRepository;
import com.daltweb.topicmanagement.repository.UserRepository;
import com.daltweb.topicmanagement.repository.GradeRepository;
import com.daltweb.topicmanagement.repository.FinalResultRepository;
import com.daltweb.topicmanagement.repository.ReviewerAssignmentRepository;
import com.daltweb.topicmanagement.repository.CouncilTopicAssignmentRepository;
import com.daltweb.topicmanagement.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final DepartmentRepository departmentRepository;
    private final RegistrationPeriodRepository periodRepository;
    private final UserRepository userRepository;
    private final StudentGroupRepository groupRepository;
    private final GradeRepository gradeRepository;
    private final FinalResultRepository finalResultRepository;
    private final ReviewerAssignmentRepository reviewerRepository;
    private final CouncilTopicAssignmentRepository councilTopicRepository;

    public TopicService(TopicRepository topicRepository, DepartmentRepository departmentRepository, RegistrationPeriodRepository periodRepository, UserRepository userRepository, StudentGroupRepository groupRepository,
                        GradeRepository gradeRepository, FinalResultRepository finalResultRepository,
                        ReviewerAssignmentRepository reviewerRepository, CouncilTopicAssignmentRepository councilTopicRepository) {
        this.topicRepository = topicRepository;
        this.departmentRepository = departmentRepository;
        this.periodRepository = periodRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.gradeRepository = gradeRepository;
        this.finalResultRepository = finalResultRepository;
        this.reviewerRepository = reviewerRepository;
        this.councilTopicRepository = councilTopicRepository;
    }

    @Transactional(readOnly = true)
    public Page<TopicDto> getTopics(Long periodId, Long departmentId, TopicApprovalStatus status, Boolean isAssigned, String keyword, Long advisorId, Pageable pageable) {
        return mapPage(topicRepository.filterTopics(periodId, departmentId, status, isAssigned, keyword, advisorId, pageable));
    }

    @Transactional(readOnly = true)
    public Page<TopicDto> getApprovedPublishedTopics(Long periodId, Long departmentId, Pageable pageable) {
        return mapPage(topicRepository.filterTopics(periodId, departmentId, TopicApprovalStatus.APPROVED, null, null, null, pageable));
    }

    @Transactional(readOnly = true)
    public TopicDto getTopicById(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đề tài với ID: " + id));
        return mapToDto(topic);
    }

    @Transactional
    public TopicDto createTopic(TopicCreateRequest request) {
        RegistrationPeriod period = periodRepository.findById(request.getPeriodId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đợt đăng ký"));

        LocalDateTime now = LocalDateTime.now();
        if (period.getStatus() != PeriodStatus.PHASE1_LECTURER_REGISTRATION) {
            throw new BadRequestException("Chỉ được đề xuất đề tài trong Giai đoạn 1 đăng ký đề tài!");
        }
        if (now.isBefore(period.getLecturerRegStart()) || now.isAfter(period.getLecturerRegEnd())) {
            throw new BadRequestException("Hiện tại không nằm trong thời gian cho phép giảng viên đăng ký đề tài!");
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bộ môn"));

        Long currentUserId = SecurityUtils.getCurrentUserId();
        User primaryAdvisor = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin giảng viên hướng dẫn"));

        User coAdvisor = null;
        if (request.getCoAdvisorId() != null) {
            if (Objects.equals(request.getCoAdvisorId(), currentUserId)) {
                throw new BadRequestException("GVHD phụ không thể trùng với GVHD chính!");
            }
            coAdvisor = userRepository.findById(request.getCoAdvisorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên đồng hướng dẫn"));
        }

        long count = topicRepository.count() + 1;
        String topicCode = String.format("DT-%s-%03d", department.getCode(), count);

        Topic topic = Topic.builder()
                .code(topicCode)
                .titleVi(request.getTitleVi())
                .titleEn(request.getTitleEn())
                .description(request.getDescription())
                .requirements(request.getRequirements())
                .maxStudents(request.getMaxStudents() != null ? request.getMaxStudents() : 3)
                .department(department)
                .period(period)
                .primaryAdvisor(primaryAdvisor)
                .coAdvisor(coAdvisor)
                .approvalStatus(TopicApprovalStatus.PENDING)
                .isAssigned(false)
                .build();

        Topic saved = topicRepository.save(topic);
        return mapToDto(saved);
    }

    @Transactional
    public TopicDto updateTopic(Long id, TopicUpdateRequest request) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đề tài với ID: " + id));

        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean isOwner = Objects.equals(topic.getPrimaryAdvisor().getId(), currentUserId);
        boolean isAdmin = SecurityUtils.hasRole(RoleType.ROLE_ADMIN) || SecurityUtils.hasRole(RoleType.ROLE_DEAN);

        if (!isOwner && !isAdmin) {
            throw new ForbiddenException("Bạn không có quyền chỉnh sửa đề tài này!");
        }

        if (Boolean.TRUE.equals(topic.getIsAssigned()) && !isAdmin) {
            throw new BadRequestException("Đề tài đã có nhóm sinh viên đăng ký, không thể chỉnh sửa!");
        }

        if (isAdmin && !Objects.equals(request.getMaxStudents(), topic.getMaxStudents())) {
            groupRepository.findByTopicId(id).ifPresent(group -> {
                if (request.getMaxStudents() != null && request.getMaxStudents() < group.getMembers().size())
                    throw new BadRequestException("Số sinh viên tối đa không được nhỏ hơn số thành viên của nhóm đã đăng ký!");
            });
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bộ môn"));

        User coAdvisor = null;
        if (request.getCoAdvisorId() != null) {
            if (Objects.equals(request.getCoAdvisorId(), topic.getPrimaryAdvisor().getId())) {
                throw new BadRequestException("GVHD phụ không thể trùng với GVHD chính!");
            }
            coAdvisor = userRepository.findById(request.getCoAdvisorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên đồng hướng dẫn"));
        }

        topic.setTitleVi(request.getTitleVi());
        topic.setTitleEn(request.getTitleEn());
        topic.setDescription(request.getDescription());
        topic.setRequirements(request.getRequirements());
        topic.setMaxStudents(request.getMaxStudents());
        topic.setDepartment(department);
        topic.setCoAdvisor(coAdvisor);

        if (topic.getApprovalStatus() == TopicApprovalStatus.APPROVED && !isAdmin) {
            topic.setApprovalStatus(TopicApprovalStatus.PENDING);
            topic.setApprovedBy(null);
            topic.setApprovedAt(null);
        }

        Topic updated = topicRepository.save(topic);
        return mapToDto(updated);
    }

    @Transactional
    public TopicDto approveTopic(Long id, TopicApprovalRequest request) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đề tài với ID: " + id));

        boolean isAdmin = SecurityUtils.hasRole(RoleType.ROLE_ADMIN) || SecurityUtils.hasRole(RoleType.ROLE_DEAN);
        if (!isAdmin) throw new ForbiddenException("Chỉ Admin/Trưởng khoa được phê duyệt cuối cùng!");
        if (Boolean.TRUE.equals(topic.getIsAssigned()) && request.getApprovalStatus() != TopicApprovalStatus.APPROVED)
            throw new BadRequestException("Đề tài đã có nhóm đăng ký, không thể thu hồi phê duyệt hoặc từ chối!");

        Long currentUserId = SecurityUtils.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin người dùng"));

        topic.setApprovalStatus(request.getApprovalStatus());
        topic.setApprovedBy(currentUser);
        topic.setApprovedAt(LocalDateTime.now());
        if (request.getApprovalStatus() == TopicApprovalStatus.REJECTED) {
            topic.setRejectionReason(request.getRejectionReason());
        } else {
            topic.setRejectionReason(null);
        }

        Topic saved = topicRepository.save(topic);
        return mapToDto(saved);
    }

    @Transactional
    public void deleteTopic(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đề tài với ID: " + id));

        boolean isAdmin = SecurityUtils.hasRole(RoleType.ROLE_ADMIN) || SecurityUtils.hasRole(RoleType.ROLE_DEAN);
        if (!isAdmin && !Objects.equals(topic.getPrimaryAdvisor().getId(), SecurityUtils.getCurrentUserId()))
            throw new ForbiddenException("Bạn không có quyền xóa đề tài này!");
        if (!isAdmin && topic.getApprovalStatus() == TopicApprovalStatus.APPROVED) {
            throw new BadRequestException("Đề tài đã được phê duyệt, giảng viên không thể tự ý xóa!");
        }

        List<StudentGroup> registeredGroups = groupRepository.findByTopicIdIn(List.of(id));
        if (!isAdmin && (Boolean.TRUE.equals(topic.getIsAssigned()) || !registeredGroups.isEmpty()))
            throw new BadRequestException("Đề tài đã có nhóm sinh viên đăng ký, chỉ Admin/Trưởng khoa được xóa!");
        if (isAdmin) {
            finalResultRepository.deleteByTopicId(id);
            gradeRepository.deleteByTopicId(id);
            reviewerRepository.deleteByTopicId(id);
            councilTopicRepository.deleteByTopicId(id);
            for (StudentGroup group : registeredGroups) {
                group.setTopic(null);
                group.setStatus(GroupStatus.FORMING);
                group.setReportUrl(null);
                group.setReportNote(null);
                group.setSubmittedAt(null);
            }
            groupRepository.saveAll(registeredGroups);
            topicRepository.flush();
        }
        topicRepository.delete(topic);
    }

    public TopicDto mapToDto(Topic topic) {
        return mapToDto(topic, groupRepository.findByTopicId(topic.getId()).orElse(null));
    }

    private Page<TopicDto> mapPage(Page<Topic> topics) {
        if (topics.isEmpty()) return topics.map(t -> mapToDto(t, null));
        Map<Long, StudentGroup> groups = groupRepository.findByTopicIdIn(topics.map(Topic::getId).getContent()).stream()
                .collect(Collectors.toMap(g -> g.getTopic().getId(), g -> g));
        return topics.map(topic -> mapToDto(topic, groups.get(topic.getId())));
    }

    public TopicDto mapToDto(Topic topic, StudentGroup group) {

        return TopicDto.builder()
                .id(topic.getId())
                .code(topic.getCode())
                .titleVi(topic.getTitleVi())
                .titleEn(topic.getTitleEn())
                .description(topic.getDescription())
                .requirements(topic.getRequirements())
                .maxStudents(topic.getMaxStudents())
                .departmentId(topic.getDepartment() != null ? topic.getDepartment().getId() : null)
                .departmentName(topic.getDepartment() != null ? topic.getDepartment().getName() : null)
                .periodId(topic.getPeriod() != null ? topic.getPeriod().getId() : null)
                .periodName(topic.getPeriod() != null ? topic.getPeriod().getName() : null)
                .primaryAdvisorId(topic.getPrimaryAdvisor() != null ? topic.getPrimaryAdvisor().getId() : null)
                .primaryAdvisorName(topic.getPrimaryAdvisor() != null ? topic.getPrimaryAdvisor().getFullName() : null)
                .primaryAdvisorEmail(topic.getPrimaryAdvisor() != null ? topic.getPrimaryAdvisor().getEmail() : null)
                .coAdvisorId(topic.getCoAdvisor() != null ? topic.getCoAdvisor().getId() : null)
                .coAdvisorName(topic.getCoAdvisor() != null ? topic.getCoAdvisor().getFullName() : null)
                .coAdvisorEmail(topic.getCoAdvisor() != null ? topic.getCoAdvisor().getEmail() : null)
                .approvalStatus(topic.getApprovalStatus())
                .rejectionReason(topic.getRejectionReason())
                .approvedById(topic.getApprovedBy() != null ? topic.getApprovedBy().getId() : null)
                .approvedByName(topic.getApprovedBy() != null ? topic.getApprovedBy().getFullName() : null)
                .approvedAt(topic.getApprovedAt())
                .isAssigned(topic.getIsAssigned())
                .assignedGroupId(group != null ? group.getId() : null)
                .assignedGroupName(group != null ? group.getName() : null)
                .createdAt(topic.getCreatedAt())
                .build();
    }
}
