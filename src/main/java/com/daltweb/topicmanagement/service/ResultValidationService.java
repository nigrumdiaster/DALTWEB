package com.daltweb.topicmanagement.service;

import com.daltweb.topicmanagement.constant.*;
import com.daltweb.topicmanagement.entity.*;
import com.daltweb.topicmanagement.exception.BadRequestException;
import com.daltweb.topicmanagement.repository.*;
import org.springframework.stereotype.Service;

import java.util.*;

/** Shared checks for finalization, period completion and publication. */
@Service
public class ResultValidationService {
    private final GradeRepository grades;
    private final ReviewerAssignmentRepository reviewers;
    private final CouncilTopicAssignmentRepository assignments;
    private final CouncilMemberRepository councilMembers;
    private final StudentGroupRepository groups;
    private final FinalResultRepository results;

    public ResultValidationService(GradeRepository grades, ReviewerAssignmentRepository reviewers,
            CouncilTopicAssignmentRepository assignments, CouncilMemberRepository councilMembers,
            StudentGroupRepository groups, FinalResultRepository results) {
        this.grades = grades;
        this.reviewers = reviewers;
        this.assignments = assignments;
        this.councilMembers = councilMembers;
        this.groups = groups;
        this.results = results;
    }

    public record Scores(double advisor, Double reviewer, Double council) {
        public double total() {
            double sum = advisor;
            int count = 1;
            if (reviewer != null) { sum += reviewer; count++; }
            if (council != null) { sum += council; count++; }
            return round(sum / count);
        }
    }

    public static double round(double value) { return Math.round(value * 100.0) / 100.0; }

    public Scores calculate(Topic topic) {
        List<Grade> all = grades.findByTopicId(topic.getId());
        double advisor = requiredGrade(all, topic.getPrimaryAdvisor(), GraderRole.ADVISOR);
        PeriodType type = topic.getPeriod().getType();
        var reviewer = reviewers.findByTopicId(topic.getId()).orElse(null);
        Double reviewerScore = null;
        if (type == PeriodType.SPECIALIZED_PROJECT || type == PeriodType.GRADUATION_THESIS || reviewer != null) {
            if (reviewer == null || reviewer.getReviewer() == null
                    || !Objects.equals(reviewer.getPeriod().getId(), topic.getPeriod().getId())
                    || isAdvisor(topic, reviewer.getReviewer())) {
                throw new BadRequestException("Đề tài chưa có phân công phản biện hợp lệ!");
            }
            reviewerScore = validScore(reviewer.getScore());
        }
        var assignment = assignments.findByTopicId(topic.getId()).orElse(null);
        Double councilScore = null;
        if (type == PeriodType.GRADUATION_THESIS || assignment != null) {
            if (assignment == null || !Objects.equals(assignment.getCouncil().getPeriod().getId(), topic.getPeriod().getId())) {
                throw new BadRequestException("Đề tài chưa được phân công hội đồng hợp lệ!");
            }
            List<CouncilMember> members = councilMembers.findByCouncilId(assignment.getCouncil().getId());
            if (members.size() < 3 || members.size() > 5
                    || members.stream().map(m -> m.getLecturer().getId()).distinct().count() != members.size()
                    || members.stream().filter(m -> m.getCouncilRole() == CouncilRole.CHAIRMAN).count() != 1
                    || members.stream().filter(m -> m.getCouncilRole() == CouncilRole.SECRETARY).count() != 1) {
                throw new BadRequestException("Hội đồng phải có 3–5 giảng viên, đúng một Chủ tịch và một Thư ký!");
            }
            double sum = 0;
            for (CouncilMember member : members) {
                if (isAdvisor(topic, member.getLecturer())) {
                    throw new BadRequestException("GVHD không được chấm đề tài của mình trong hội đồng!");
                }
                GraderRole role = switch (member.getCouncilRole()) {
                    case CHAIRMAN -> GraderRole.COUNCIL_CHAIRMAN;
                    case SECRETARY -> GraderRole.COUNCIL_SECRETARY;
                    case MEMBER -> GraderRole.COUNCIL_MEMBER;
                };
                sum += requiredGrade(all, member.getLecturer(), role);
            }
            councilScore = sum / members.size();
        }
        return new Scores(advisor, reviewerScore, councilScore);
    }

    private boolean isAdvisor(Topic topic, User user) {
        return Objects.equals(topic.getPrimaryAdvisor().getId(), user.getId());
    }

    private double requiredGrade(List<Grade> all, User grader, GraderRole role) {
        List<Grade> matching = all.stream().filter(g -> g.getGraderRole() == role
                && Objects.equals(g.getGrader().getId(), grader.getId())).toList();
        if (matching.size() != 1) {
            throw new BadRequestException("Chưa đủ điểm hợp lệ của người chấm: " + grader.getId() + " (" + role + ")!");
        }
        return validScore(matching.get(0).getScore());
    }

    private double validScore(Double score) {
        if (score == null || !Double.isFinite(score) || score < 0 || score > 10) {
            throw new BadRequestException("Chưa đủ điểm thành phần hợp lệ trong khoảng 0–10!");
        }
        return score;
    }

    public List<FinalResult> validatePeriodResults(RegistrationPeriod period) {
        List<StudentGroup> registered = groups.findByPeriodId(period.getId()).stream()
                .filter(g -> g.getTopic() != null).toList();
        if (registered.isEmpty()) {
            throw new BadRequestException("Đợt chưa có nhóm đăng ký đề tài để chốt hoặc công bố kết quả!");
        }
        List<FinalResult> finalized = results.findByPeriodId(period.getId());
        for (StudentGroup group : registered) {
            FinalResult result = finalized.stream().filter(r -> Objects.equals(r.getStudentGroup().getId(), group.getId())
                    && Objects.equals(r.getTopic().getId(), group.getTopic().getId())).findFirst()
                    .orElseThrow(() -> new BadRequestException("Chưa chốt kết quả cho nhóm: " + group.getCode()));
            Scores scores = calculate(group.getTopic());
            if (!Objects.equals(result.getFinalScore(), scores.total())
                    || !Objects.equals(result.getAdvisorAvgScore(), round(scores.advisor()))
                    || !Objects.equals(result.getReviewerScore(), scores.reviewer() == null ? null : round(scores.reviewer()))
                    || !Objects.equals(result.getCouncilAvgScore(), scores.council() == null ? null : round(scores.council()))) {
                throw new BadRequestException("Điểm thành phần đã thay đổi hoặc chưa đủ. Cần chốt lại kết quả nhóm: " + group.getCode());
            }
        }
        if (finalized.size() != registered.size()) {
            throw new BadRequestException("Danh sách kết quả không khớp các nhóm đã đăng ký đề tài!");
        }
        return finalized;
    }
}
