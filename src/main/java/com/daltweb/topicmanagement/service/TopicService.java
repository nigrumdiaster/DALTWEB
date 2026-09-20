package com.daltweb.topicmanagement.service;

import com.daltweb.topicmanagement.constant.PeriodStatus;
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
import com.daltweb.topicmanagement.exception.BadRequestException;
import com.daltweb.topicmanagement.exception.ForbiddenException;
import com.daltweb.topicmanagement.exception.ResourceNotFoundException;
import com.daltweb.topicmanagement.repository.DepartmentRepository;
import com.daltweb.topicmanagement.repository.RegistrationPeriodRepository;
import com.daltweb.topicmanagement.repository.StudentGroupRepository;
import com.daltweb.topicmanagement.repository.TopicRepository;
import com.daltweb.topicmanagement.repository.UserRepository;
import com.daltweb.topicmanagement.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final DepartmentRepository departmentRepository;
    private final RegistrationPeriodRepository periodRepository;
    private final UserRepository userRepository;
    private final StudentGroupRepository groupRepository;

    public TopicService(TopicRepository topicRepository, DepartmentRepository departmentRepository, RegistrationPeriodRepository periodRepository, UserRepository userRepository, StudentGroupRepository groupRepository) {
        this.topicRepository = topicRepository;
        this.departmentRepository = departmentRepository;
        this.periodRepository = periodRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    @Transactional(readOnly = true)
    public List<TopicDto> getTopics(Long periodId, Long departmentId, TopicApprovalStatus status, Boolean isAssigned, String keyword) {
        return topicRepository.filterTopics(periodId, departmentId, status, isAssigned, keyword)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TopicDto> getApprovedPublishedTopics(Long periodId, Long departmentId) {
        return topicRepository.filterTopics(periodId, departmentId, TopicApprovalStatus.APPROVED, null, null)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
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
        boolean isAdmin = SecurityUtils.hasRole(RoleType.ROLE_ADMIN) || SecurityUtils.hasRole(RoleType.ROLE_DEAN);
        if (!isAdmin) {
            if (period.getStatus() != PeriodStatus.PHASE1_LECTURER_REGISTRATION &&
                (now.isBefore(period.getLecturerRegStart()) || now.isAfter(period.getLecturerRegEnd()))) {
                throw new BadRequestException("Hiện tại không nằm trong thời gian cho phép giảng viên đăng ký đề tài!");
            }
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

        if (topic.getIsAssigned()) {
            throw new BadRequestException("Đề tài đã có nhóm sinh viên đăng ký, không thể chỉnh sửa!");
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

        Topic updated = topicRepository.save(topic);
        return mapToDto(updated);
    }

    @Transactional
    public TopicDto approveTopic(Long id, TopicApprovalRequest request) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đề tài với ID: " + id));

        Long currentUserId = SecurityUtils.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin người dùng"));

        boolean isDeptHead = topic.getDepartment().getHeadLecturer() != null &&
                Objects.equals(topic.getDepartment().getHeadLecturer().getId(), currentUserId);
        boolean isAdmin = SecurityUtils.hasRole(RoleType.ROLE_ADMIN) || SecurityUtils.hasRole(RoleType.ROLE_DEAN);

        if (!isDeptHead && !isAdmin) {
            throw new ForbiddenException("Chỉ Trưởng bộ môn phụ trách hoặc Ban chủ nhiệm Khoa mới có quyền phê duyệt đề tài này!");
        }

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

        if (topic.getIsAssigned()) {
            throw new BadRequestException("Đề tài đã có nhóm sinh viên đăng ký, không thể xóa!");
        }

        topicRepository.delete(topic);
    }

    public TopicDto mapToDto(Topic topic) {
        var groupOpt = groupRepository.findByTopicId(topic.getId());

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
                .assignedGroupId(groupOpt.map(g -> g.getId()).orElse(null))
                .assignedGroupName(groupOpt.map(g -> g.getName()).orElse(null))
                .createdAt(topic.getCreatedAt())
                .build();
    }
}
