package com.daltweb.topicmanagement.dto.grade;

import com.daltweb.topicmanagement.constant.GraderRole;
import java.time.LocalDateTime;

public class GradeDto {
    private Long id;
    private Long periodId;
    private Long topicId;
    private String topicTitleVi;
    private Long studentGroupId;
    private String studentGroupName;

    private Long graderId;
    private String graderName;
    private GraderRole graderRole;
    private Double score;
    private String feedback;
    private LocalDateTime gradedAt;

    public GradeDto() {}

    public GradeDto(Long id, Long periodId, Long topicId, String topicTitleVi, Long studentGroupId, String studentGroupName, Long graderId, String graderName, GraderRole graderRole, Double score, String feedback, LocalDateTime gradedAt) {
        this.id = id;
        this.periodId = periodId;
        this.topicId = topicId;
        this.topicTitleVi = topicTitleVi;
        this.studentGroupId = studentGroupId;
        this.studentGroupName = studentGroupName;
        this.graderId = graderId;
        this.graderName = graderName;
        this.graderRole = graderRole;
        this.score = score;
        this.feedback = feedback;
        this.gradedAt = gradedAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long periodId;
        private Long topicId;
        private String topicTitleVi;
        private Long studentGroupId;
        private String studentGroupName;
        private Long graderId;
        private String graderName;
        private GraderRole graderRole;
        private Double score;
        private String feedback;
        private LocalDateTime gradedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder periodId(Long periodId) { this.periodId = periodId; return this; }
        public Builder topicId(Long topicId) { this.topicId = topicId; return this; }
        public Builder topicTitleVi(String topicTitleVi) { this.topicTitleVi = topicTitleVi; return this; }
        public Builder studentGroupId(Long studentGroupId) { this.studentGroupId = studentGroupId; return this; }
        public Builder studentGroupName(String studentGroupName) { this.studentGroupName = studentGroupName; return this; }
        public Builder graderId(Long graderId) { this.graderId = graderId; return this; }
        public Builder graderName(String graderName) { this.graderName = graderName; return this; }
        public Builder graderRole(GraderRole graderRole) { this.graderRole = graderRole; return this; }
        public Builder score(Double score) { this.score = score; return this; }
        public Builder feedback(String feedback) { this.feedback = feedback; return this; }
        public Builder gradedAt(LocalDateTime gradedAt) { this.gradedAt = gradedAt; return this; }
        public GradeDto build() {
            return new GradeDto(id, periodId, topicId, topicTitleVi, studentGroupId, studentGroupName, graderId, graderName, graderRole, score, feedback, gradedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPeriodId() { return periodId; }
    public void setPeriodId(Long periodId) { this.periodId = periodId; }
    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }
    public String getTopicTitleVi() { return topicTitleVi; }
    public void setTopicTitleVi(String topicTitleVi) { this.topicTitleVi = topicTitleVi; }
    public Long getStudentGroupId() { return studentGroupId; }
    public void setStudentGroupId(Long studentGroupId) { this.studentGroupId = studentGroupId; }
    public String getStudentGroupName() { return studentGroupName; }
    public void setStudentGroupName(String studentGroupName) { this.studentGroupName = studentGroupName; }
    public Long getGraderId() { return graderId; }
    public void setGraderId(Long graderId) { this.graderId = graderId; }
    public String getGraderName() { return graderName; }
    public void setGraderName(String graderName) { this.graderName = graderName; }
    public GraderRole getGraderRole() { return graderRole; }
    public void setGraderRole(GraderRole graderRole) { this.graderRole = graderRole; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    public LocalDateTime getGradedAt() { return gradedAt; }
    public void setGradedAt(LocalDateTime gradedAt) { this.gradedAt = gradedAt; }
}
