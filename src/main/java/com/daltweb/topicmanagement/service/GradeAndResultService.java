package com.daltweb.topicmanagement.service;

import com.daltweb.topicmanagement.constant.CouncilRole;
import com.daltweb.topicmanagement.constant.GraderRole;
import com.daltweb.topicmanagement.constant.RoleType;
import com.daltweb.topicmanagement.constant.PeriodStatus;
import com.daltweb.topicmanagement.constant.PeriodType;
import com.daltweb.topicmanagement.dto.period.RegistrationPeriodDto;
import com.daltweb.topicmanagement.event.ResultPublishedEvent;
import com.daltweb.topicmanagement.dto.council.FinalizeCouncilResultRequest;
import com.daltweb.topicmanagement.dto.grade.AdvisorGradeRequest;
import com.daltweb.topicmanagement.dto.grade.FinalResultDto;
import com.daltweb.topicmanagement.dto.grade.GradeDto;
import com.daltweb.topicmanagement.dto.grade.PublishResultRequest;
import com.daltweb.topicmanagement.entity.*;
import com.daltweb.topicmanagement.exception.BadRequestException;
import com.daltweb.topicmanagement.exception.ForbiddenException;
import com.daltweb.topicmanagement.exception.ResourceNotFoundException;
import com.daltweb.topicmanagement.repository.*;
import com.daltweb.topicmanagement.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GradeAndResultService {

    private final GradeRepository gradeRepository;
    private final FinalResultRepository finalResultRepository;
    private final TopicRepository topicRepository;
    private final StudentGroupRepository groupRepository;
    private final RegistrationPeriodRepository periodRepository;
    private final ReviewerAssignmentRepository reviewerAssignmentRepository;
    private final CouncilMemberRepository councilMemberRepository;
    private final CouncilTopicAssignmentRepository councilTopicAssignmentRepository;
    private final UserRepository userRepository;
    private final TopicService topicService;
    private final StudentGroupService groupService;
    private final GroupMemberRepository memberRepository;
    private final RegistrationPeriodService periodService;
    private final ResultValidationService resultValidation;
    private final ApplicationEventPublisher events;

    public GradeAndResultService(GradeRepository gradeRepository, FinalResultRepository finalResultRepository, TopicRepository topicRepository, StudentGroupRepository groupRepository, RegistrationPeriodRepository periodRepository, ReviewerAssignmentRepository reviewerAssignmentRepository, CouncilMemberRepository councilMemberRepository, CouncilTopicAssignmentRepository councilTopicAssignmentRepository, UserRepository userRepository, TopicService topicService, StudentGroupService groupService,
            GroupMemberRepository memberRepository, RegistrationPeriodService periodService,
            ResultValidationService resultValidation, ApplicationEventPublisher events) {
        this.gradeRepository = gradeRepository;
        this.finalResultRepository = finalResultRepository;
        this.topicRepository = topicRepository;
        this.groupRepository = groupRepository;
        this.periodRepository = periodRepository;
        this.reviewerAssignmentRepository = reviewerAssignmentRepository;
        this.councilMemberRepository = councilMemberRepository;
        this.councilTopicAssignmentRepository = councilTopicAssignmentRepository;
        this.userRepository = userRepository;
        this.topicService = topicService;
        this.groupService = groupService;
        this.memberRepository = memberRepository;
        this.periodService = periodService;
        this.resultValidation = resultValidation;
        this.events = events;
    }

    @Transactional
    public GradeDto gradeAsAdvisor(AdvisorGradeRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đề tài với ID: " + request.getTopicId()));

        if (!Set.of(PeriodStatus.IN_PROGRESS, PeriodStatus.REVIEWING, PeriodStatus.DEFENSE).contains(topic.getPeriod().getStatus())) {
            throw new BadRequestException("Chỉ được nhập điểm GVHD trong giai đoạn thực hiện hoặc đánh giá đề tài!");
        }

        boolean isPrimary = Objects.equals(topic.getPrimaryAdvisor().getId(), currentUserId);
        boolean isCo = topic.getCoAdvisor() != null && Objects.equals(topic.getCoAdvisor().getId(), currentUserId);
        boolean isAdmin = SecurityUtils.hasRole(RoleType.ROLE_ADMIN) || SecurityUtils.hasRole(RoleType.ROLE_DEAN);

        if (!isPrimary && !isCo && !isAdmin) {
            throw new ForbiddenException("Bạn không phải là giảng viên hướng dẫn của đề tài này!");
        }

        StudentGroup group = groupRepository.findByTopicId(topic.getId())
                .orElseThrow(() -> new BadRequestException("Đề tài chưa có nhóm sinh viên thực hiện!"));

        GraderRole role = isPrimary ? GraderRole.ADVISOR : GraderRole.CO_ADVISOR;

        Grade grade = gradeRepository.findByTopicIdAndGraderIdAndGraderRole(topic.getId(), currentUserId, role)
                .orElse(Grade.builder()
                        .period(topic.getPeriod())
                        .topic(topic)
                        .studentGroup(group)
                        .grader(currentUser)
                        .graderRole(role)
                        .build());

        grade.setScore(request.getScore());
        grade.setFeedback(request.getFeedback());
        grade.setGradedAt(LocalDateTime.now());
        Grade saved = gradeRepository.save(grade);

        return mapToGradeDto(saved);
    }

    @Transactional
    public FinalResultDto finalizeTopicResult(FinalizeCouncilResultRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đề tài"));

        StudentGroup group = groupRepository.findByTopicId(topic.getId())
                .orElseThrow(() -> new BadRequestException("Đề tài chưa có nhóm sinh viên thực hiện!"));

        boolean isAdmin = SecurityUtils.hasRole(RoleType.ROLE_ADMIN) || SecurityUtils.hasRole(RoleType.ROLE_DEAN);
        boolean isChairman = false;

        var assignmentOpt = councilTopicAssignmentRepository.findByTopicId(topic.getId());
        if (assignmentOpt.isPresent()) {
            DefenseCouncil council = assignmentOpt.get().getCouncil();
            var chairmanOpt = councilMemberRepository.findByCouncilIdAndCouncilRole(council.getId(), CouncilRole.CHAIRMAN);
            if (chairmanOpt.isPresent() && Objects.equals(chairmanOpt.get().getLecturer().getId(), currentUserId)) {
                isChairman = true;
            }
        }

        if (!isChairman && !isAdmin) {
            throw new ForbiddenException("Chỉ Chủ tịch hội đồng chấm điểm hoặc Ban chủ nhiệm Khoa mới có quyền tổng hợp và chốt kết quả đề tài!");
        }

        PeriodStatus requiredStatus = topic.getPeriod().getType() == PeriodType.GRADUATION_THESIS
                ? PeriodStatus.DEFENSE : PeriodStatus.REVIEWING;
        if (topic.getPeriod().getStatus() != requiredStatus) {
            throw new BadRequestException("Chỉ được chốt điểm trong giai đoạn " + requiredStatus + "!");
        }
        ResultValidationService.Scores scores = resultValidation.calculate(topic);
        Double advisorAvg = scores.advisor();
        Double reviewerScore = scores.reviewer();
        Double councilAvg = scores.council();
        double finalScore = scores.total();

        String letterGrade = convertToLetterGrade(finalScore);
        boolean passed = finalScore >= 5.0;

        FinalResult result = finalResultRepository.findByTopicId(topic.getId())
                .orElse(FinalResult.builder()
                        .period(topic.getPeriod())
                        .topic(topic)
                        .studentGroup(group)
                        .build());

        result.setAdvisorAvgScore(advisorAvg != null ? Math.round(advisorAvg * 100.0) / 100.0 : null);
        result.setReviewerScore(reviewerScore != null ? Math.round(reviewerScore * 100.0) / 100.0 : null);
        result.setCouncilAvgScore(councilAvg != null ? Math.round(councilAvg * 100.0) / 100.0 : null);
        result.setFinalScore(finalScore);
        result.setLetterGrade(letterGrade);
        result.setPassed(passed);
        result.setChairmanSummary(request.getChairmanSummary());
        result.setUpdatedAt(LocalDateTime.now());

        FinalResult saved = finalResultRepository.save(result);
        return mapToFinalResultDto(saved);
    }

    @Transactional
    public void publishResults(PublishResultRequest request) {
        RegistrationPeriod period = periodRepository.findForUpdate(request.getPeriodId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đợt đăng ký"));

        boolean publish = Boolean.TRUE.equals(request.getPublish());
        if (publish && period.getStatus() != PeriodStatus.COMPLETED) {
            throw new BadRequestException("Chỉ được công bố kết quả khi đợt đã hoàn thành!");
        }
        List<FinalResult> results = publish ? resultValidation.validatePeriodResults(period)
                : finalResultRepository.findByPeriodId(period.getId());
        LocalDateTime now = LocalDateTime.now();

        for (FinalResult res : results) {
            boolean newlyPublished = publish && !Boolean.TRUE.equals(res.getIsPublished());
            res.setIsPublished(publish);
            if (!publish) res.setPublishedAt(null);
            else if (newlyPublished) res.setPublishedAt(now);
            finalResultRepository.save(res);
            if (newlyPublished) {
                List<String> recipients = res.getStudentGroup().getMembers().stream()
                        .map(m -> m.getStudent().getEmail())
                        .filter(email -> email != null && !email.isBlank()).map(String::trim).toList();
                events.publishEvent(new ResultPublishedEvent(res.getId(), period.getName(),
                        res.getTopic().getTitleVi(), res.getFinalScore(), res.getLetterGrade(), recipients));
            }
        }
    }

    @Transactional(readOnly = true)
    public List<FinalResultDto> getResultsByPeriod(Long periodId) {
        if (SecurityUtils.hasRole(RoleType.ROLE_STUDENT)) {
            return getMyStudentResults(periodId);
        }
        return finalResultRepository.findByPeriodId(periodId).stream()
                .map(this::mapToFinalResultDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FinalResultDto getTopicResult(Long topicId) {
        validateStudentTopicAccess(topicId);
        FinalResult result = finalResultRepository.findByTopicId(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Chưa có kết quả tổng hợp cho đề tài này!"));
        return mapToFinalResultDto(result);
    }

    @Transactional(readOnly = true)
    public List<FinalResultDto> getMyStudentResults(Long periodId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (periodId != null) {
            GroupMember member = memberRepository.findByStudentIdAndPeriodId(currentUserId, periodId)
                    .orElseThrow(() -> new ForbiddenException("Bạn chưa đăng ký đề tài trong đợt này!"));
            if (member.getGroup().getTopic() == null) {
                throw new ForbiddenException("Bạn chưa đăng ký đề tài trong đợt này!");
            }
        }
        List<FinalResult> results = periodId == null
                ? finalResultRepository.findPublishedResultsByStudentId(currentUserId)
                : finalResultRepository.findPublishedResultsByStudentIdAndPeriodId(currentUserId, periodId);
        return results.stream()
                .map(this::mapToFinalResultDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RegistrationPeriodDto> getMyResultPeriods() {
        return periodRepository.findRegisteredByStudentId(SecurityUtils.getCurrentUserId()).stream()
                .map(periodService::mapToDto).toList();
    }

    @Transactional(readOnly = true)
    public List<GradeDto> getTopicGrades(Long topicId) {
        validateStudentTopicAccess(topicId);
        return gradeRepository.findByTopicId(topicId).stream()
                .map(this::mapToGradeDto)
                .collect(Collectors.toList());
    }

    private void validateStudentTopicAccess(Long topicId) {
        if (!SecurityUtils.hasRole(RoleType.ROLE_STUDENT)) return;
        StudentGroup group = groupRepository.findByTopicId(topicId)
                .orElseThrow(() -> new ForbiddenException("Bạn không có quyền xem điểm đề tài này!"));
        boolean owns = group.getMembers().stream()
                .anyMatch(m -> Objects.equals(m.getStudent().getId(), SecurityUtils.getCurrentUserId()));
        if (!owns) throw new ForbiddenException("Bạn không có quyền xem điểm đề tài này!");
        FinalResult result = finalResultRepository.findByTopicId(topicId)
                .orElseThrow(() -> new ForbiddenException("Kết quả chưa được công bố!"));
        if (!Boolean.TRUE.equals(result.getIsPublished())) {
            throw new ForbiddenException("Kết quả chưa được công bố!");
        }
    }

    private String convertToLetterGrade(double score) {
        if (score >= 8.5) return "A";
        if (score >= 8.0) return "B+";
        if (score >= 7.0) return "B";
        if (score >= 6.5) return "C+";
        if (score >= 5.5) return "C";
        if (score >= 5.0) return "D+";
        if (score >= 4.0) return "D";
        return "F";
    }

    public GradeDto mapToGradeDto(Grade g) {
        return GradeDto.builder()
                .id(g.getId())
                .periodId(g.getPeriod().getId())
                .topicId(g.getTopic().getId())
                .topicTitleVi(g.getTopic().getTitleVi())
                .studentGroupId(g.getStudentGroup().getId())
                .studentGroupName(g.getStudentGroup().getName())
                .graderId(g.getGrader().getId())
                .graderName(g.getGrader().getFullName())
                .graderRole(g.getGraderRole())
                .score(g.getScore())
                .feedback(g.getFeedback())
                .gradedAt(g.getGradedAt())
                .build();
    }

    public FinalResultDto mapToFinalResultDto(FinalResult res) {
        List<GradeDto> detailedGrades = gradeRepository.findByTopicId(res.getTopic().getId()).stream()
                .map(this::mapToGradeDto)
                .collect(Collectors.toList());

        return FinalResultDto.builder()
                .id(res.getId())
                .periodId(res.getPeriod().getId())
                .periodName(res.getPeriod().getName())
                .topicId(res.getTopic().getId())
                .topicTitleVi(res.getTopic().getTitleVi())
                .topic(topicService.mapToDto(res.getTopic()))
                .studentGroupId(res.getStudentGroup().getId())
                .studentGroupName(res.getStudentGroup().getName())
                .studentGroup(groupService.mapToDto(res.getStudentGroup()))
                .advisorAvgScore(res.getAdvisorAvgScore())
                .reviewerScore(res.getReviewerScore())
                .councilAvgScore(res.getCouncilAvgScore())
                .finalScore(res.getFinalScore())
                .letterGrade(res.getLetterGrade())
                .passed(res.getPassed())
                .isPublished(res.getIsPublished())
                .publishedAt(res.getPublishedAt())
                .chairmanSummary(res.getChairmanSummary())
                .detailedGrades(detailedGrades)
                .build();
    }
}
