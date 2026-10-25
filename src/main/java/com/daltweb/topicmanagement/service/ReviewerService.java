package com.daltweb.topicmanagement.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import com.daltweb.topicmanagement.constant.GraderRole;
import com.daltweb.topicmanagement.constant.RoleType;
import com.daltweb.topicmanagement.constant.PeriodStatus;
import com.daltweb.topicmanagement.dto.reviewer.AssignReviewerRequest;
import com.daltweb.topicmanagement.dto.reviewer.ReviewerAssignmentDto;
import com.daltweb.topicmanagement.dto.reviewer.ReviewerGradeRequest;
import com.daltweb.topicmanagement.entity.*;
import com.daltweb.topicmanagement.exception.BadRequestException;
import com.daltweb.topicmanagement.exception.ForbiddenException;
import com.daltweb.topicmanagement.exception.ResourceNotFoundException;
import com.daltweb.topicmanagement.repository.*;
import com.daltweb.topicmanagement.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ReviewerService {

    private final ReviewerAssignmentRepository reviewerAssignmentRepository;
    private final RegistrationPeriodRepository periodRepository;
    private final TopicRepository topicRepository;
    private final StudentGroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GradeRepository gradeRepository;

    public ReviewerService(ReviewerAssignmentRepository reviewerAssignmentRepository, RegistrationPeriodRepository periodRepository, TopicRepository topicRepository, StudentGroupRepository groupRepository, UserRepository userRepository, GradeRepository gradeRepository) {
        this.reviewerAssignmentRepository = reviewerAssignmentRepository;
        this.periodRepository = periodRepository;
        this.topicRepository = topicRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.gradeRepository = gradeRepository;
    }

    @Transactional(readOnly = true)
    public Page<ReviewerAssignmentDto> getAssignmentsByPeriod(Long periodId, List<Long> topicIds, Pageable pageable) {
        if (SecurityUtils.hasRole(RoleType.ROLE_STUDENT)) {
            throw new ForbiddenException("Sinh viên tra cứu điểm qua chức năng kết quả của mình!");
        }
        if (topicIds != null && topicIds.isEmpty()) return Page.empty(pageable);
        return (topicIds == null ? reviewerAssignmentRepository.findByPeriodId(periodId, pageable)
                : reviewerAssignmentRepository.findByPeriodIdAndTopicIdIn(periodId, topicIds, pageable))
                .map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public Page<ReviewerAssignmentDto> getMyReviewTasks(Pageable pageable) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return reviewerAssignmentRepository.findByReviewerId(currentUserId, pageable)
                .map(this::mapToDto);
    }

    @Transactional
    public ReviewerAssignmentDto assignReviewer(AssignReviewerRequest request) {
        RegistrationPeriod period = periodRepository.findById(request.getPeriodId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đợt đăng ký"));

        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đề tài"));

        if (topic.getApprovalStatus() != com.daltweb.topicmanagement.constant.TopicApprovalStatus.APPROVED) {
            throw new BadRequestException("Đề tài chưa được phê duyệt, không thể phân công giảng viên phản biện!");
        }

        User reviewer = userRepository.findById(request.getReviewerId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên phản biện"));

        if (Objects.equals(topic.getPrimaryAdvisor().getId(), reviewer.getId()) ||
            (topic.getCoAdvisor() != null && Objects.equals(topic.getCoAdvisor().getId(), reviewer.getId()))) {
            throw new BadRequestException("Giảng viên hướng dẫn không được phân công làm giảng viên phản biện cho chính đề tài của mình!");
        }

        StudentGroup group = groupRepository.findByTopicId(topic.getId()).orElse(null);

        ReviewerAssignment assignment = reviewerAssignmentRepository.findByPeriodIdAndTopicId(period.getId(), topic.getId())
                .orElse(ReviewerAssignment.builder()
                        .period(period)
                        .topic(topic)
                        .studentGroup(group)
                        .build());

        assignment.setReviewer(reviewer);
        assignment.setStudentGroup(group);

        ReviewerAssignment saved = reviewerAssignmentRepository.save(assignment);
        return mapToDto(saved);
    }

    @Transactional
    public ReviewerAssignmentDto gradeReview(ReviewerGradeRequest request) {
        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đề tài"));

        if (topic.getPeriod().getStatus() != PeriodStatus.REVIEWING) {
            throw new BadRequestException("Chỉ được nhập điểm phản biện trong giai đoạn phản biện!");
        }

        Long currentUserId = SecurityUtils.getCurrentUserId();

        ReviewerAssignment assignment = reviewerAssignmentRepository.findByTopicId(topic.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Đề tài này chưa được phân công GVPB!"));

        boolean isAdmin = SecurityUtils.hasRole(RoleType.ROLE_ADMIN) || SecurityUtils.hasRole(RoleType.ROLE_DEAN);
        if (!Objects.equals(assignment.getReviewer().getId(), currentUserId) && !isAdmin) {
            throw new ForbiddenException("Bạn không phải là GVPB được phân công cho đề tài này!");
        }

        if (!isAdmin && assignment.getPeriod().getReviewerDeadline() != null &&
            LocalDate.now().isAfter(assignment.getPeriod().getReviewerDeadline())) {
            throw new BadRequestException("Đã quá hạn chót nộp điểm phản biện về Khoa (" + assignment.getPeriod().getReviewerDeadline() + ")!");
        }

        assignment.setScore(request.getScore());
        assignment.setFeedback(request.getFeedback());
        assignment.setSubmittedAt(LocalDateTime.now());
        ReviewerAssignment saved = reviewerAssignmentRepository.save(assignment);

        StudentGroup group = groupRepository.findByTopicId(topic.getId()).orElse(null);
        if (group != null) {
            Grade grade = gradeRepository.findByTopicIdAndGraderIdAndGraderRole(topic.getId(), assignment.getReviewer().getId(), GraderRole.REVIEWER)
                    .orElse(Grade.builder()
                            .period(topic.getPeriod())
                            .topic(topic)
                            .studentGroup(group)
                            .grader(assignment.getReviewer())
                            .graderRole(GraderRole.REVIEWER)
                            .build());

            grade.setScore(request.getScore());
            grade.setFeedback(request.getFeedback());
            grade.setGradedAt(LocalDateTime.now());
            gradeRepository.save(grade);
        }

        return mapToDto(saved);
    }

    public ReviewerAssignmentDto mapToDto(ReviewerAssignment a) {
        return ReviewerAssignmentDto.builder()
                .id(a.getId())
                .periodId(a.getPeriod() != null ? a.getPeriod().getId() : null)
                .periodName(a.getPeriod() != null ? a.getPeriod().getName() : null)
                .topicId(a.getTopic() != null ? a.getTopic().getId() : null)
                .topicCode(a.getTopic() != null ? a.getTopic().getCode() : null)
                .topicTitleVi(a.getTopic() != null ? a.getTopic().getTitleVi() : null)
                .studentGroupId(a.getStudentGroup() != null ? a.getStudentGroup().getId() : null)
                .studentGroupName(a.getStudentGroup() != null ? a.getStudentGroup().getName() : null)
                .reviewerId(a.getReviewer() != null ? a.getReviewer().getId() : null)
                .reviewerName(a.getReviewer() != null ? a.getReviewer().getFullName() : null)
                .reviewerCode(a.getReviewer() != null ? a.getReviewer().getCode() : null)
                .reviewerEmail(a.getReviewer() != null ? a.getReviewer().getEmail() : null)
                .score(a.getScore())
                .feedback(a.getFeedback())
                .submittedAt(a.getSubmittedAt())
                .assignedAt(a.getAssignedAt())
                .build();
    }
}
