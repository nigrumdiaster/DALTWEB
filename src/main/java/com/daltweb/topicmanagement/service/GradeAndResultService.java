package com.daltweb.topicmanagement.service;

import com.daltweb.topicmanagement.constant.CouncilRole;
import com.daltweb.topicmanagement.constant.GraderRole;
import com.daltweb.topicmanagement.constant.RoleType;
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

    public GradeAndResultService(GradeRepository gradeRepository, FinalResultRepository finalResultRepository, TopicRepository topicRepository, StudentGroupRepository groupRepository, RegistrationPeriodRepository periodRepository, ReviewerAssignmentRepository reviewerAssignmentRepository, CouncilMemberRepository councilMemberRepository, CouncilTopicAssignmentRepository councilTopicAssignmentRepository, UserRepository userRepository, TopicService topicService, StudentGroupService groupService) {
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
    }

    @Transactional
    public GradeDto gradeAsAdvisor(AdvisorGradeRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đề tài với ID: " + request.getTopicId()));

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

        List<Grade> allGrades = gradeRepository.findByTopicId(topic.getId());

        List<Grade> advisorGrades = allGrades.stream()
                .filter(g -> g.getGraderRole() == GraderRole.ADVISOR || g.getGraderRole() == GraderRole.CO_ADVISOR)
                .collect(Collectors.toList());
        Double advisorAvg = advisorGrades.isEmpty() ? null :
                advisorGrades.stream().mapToDouble(Grade::getScore).average().orElse(0.0);

        var reviewerAssignment = reviewerAssignmentRepository.findByTopicId(topic.getId()).orElse(null);
        Double reviewerScore = reviewerAssignment != null ? reviewerAssignment.getScore() : null;

        List<Grade> councilGrades = allGrades.stream()
                .filter(g -> g.getGraderRole() == GraderRole.COUNCIL_CHAIRMAN ||
                             g.getGraderRole() == GraderRole.COUNCIL_SECRETARY ||
                             g.getGraderRole() == GraderRole.COUNCIL_MEMBER)
                .collect(Collectors.toList());
        Double councilAvg = councilGrades.isEmpty() ? null :
                councilGrades.stream().mapToDouble(Grade::getScore).average().orElse(0.0);

        List<Double> validScores = new ArrayList<>();
        if (advisorAvg != null) validScores.add(advisorAvg);
        if (reviewerScore != null) validScores.add(reviewerScore);
        if (councilAvg != null) validScores.add(councilAvg);

        if (validScores.isEmpty()) {
            throw new BadRequestException("Chưa có điểm thành phần nào được nhập cho đề tài này!");
        }

        double rawFinalScore = validScores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double finalScore = Math.round(rawFinalScore * 100.0) / 100.0;

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
        RegistrationPeriod period = periodRepository.findById(request.getPeriodId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đợt đăng ký"));

        List<FinalResult> results = finalResultRepository.findByPeriodId(period.getId());
        LocalDateTime now = LocalDateTime.now();

        for (FinalResult res : results) {
            res.setIsPublished(request.getPublish());
            res.setPublishedAt(request.getPublish() ? now : null);
            finalResultRepository.save(res);
        }
    }

    @Transactional(readOnly = true)
    public List<FinalResultDto> getResultsByPeriod(Long periodId) {
        return finalResultRepository.findByPeriodId(periodId).stream()
                .map(this::mapToFinalResultDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FinalResultDto getTopicResult(Long topicId) {
        FinalResult result = finalResultRepository.findByTopicId(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Chưa có kết quả tổng hợp cho đề tài này!"));
        return mapToFinalResultDto(result);
    }

    @Transactional(readOnly = true)
    public List<FinalResultDto> getMyStudentResults() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return finalResultRepository.findPublishedResultsByStudentId(currentUserId).stream()
                .map(this::mapToFinalResultDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GradeDto> getTopicGrades(Long topicId) {
        return gradeRepository.findByTopicId(topicId).stream()
                .map(this::mapToGradeDto)
                .collect(Collectors.toList());
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
