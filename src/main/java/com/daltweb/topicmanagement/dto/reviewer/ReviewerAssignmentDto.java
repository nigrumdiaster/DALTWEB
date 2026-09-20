package com.daltweb.topicmanagement.dto.reviewer;

import java.time.LocalDateTime;

public class ReviewerAssignmentDto {
    private Long id;
    private Long periodId;
    private String periodName;

    private Long topicId;
    private String topicCode;
    private String topicTitleVi;

    private Long studentGroupId;
    private String studentGroupName;

    private Long reviewerId;
    private String reviewerName;
    private String reviewerCode;
    private String reviewerEmail;

    private Double score;
    private String feedback;
    private LocalDateTime submittedAt;
    private LocalDateTime assignedAt;

    public ReviewerAssignmentDto() {}

    public ReviewerAssignmentDto(Long id, Long periodId, String periodName, Long topicId, String topicCode, String topicTitleVi, Long studentGroupId, String studentGroupName, Long reviewerId, String reviewerName, String reviewerCode, String reviewerEmail, Double score, String feedback, LocalDateTime submittedAt, LocalDateTime assignedAt) {
        this.id = id;
        this.periodId = periodId;
        this.periodName = periodName;
        this.topicId = topicId;
        this.topicCode = topicCode;
        this.topicTitleVi = topicTitleVi;
        this.studentGroupId = studentGroupId;
        this.studentGroupName = studentGroupName;
        this.reviewerId = reviewerId;
        this.reviewerName = reviewerName;
        this.reviewerCode = reviewerCode;
        this.reviewerEmail = reviewerEmail;
        this.score = score;
        this.feedback = feedback;
        this.submittedAt = submittedAt;
        this.assignedAt = assignedAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long periodId;
        private String periodName;
        private Long topicId;
        private String topicCode;
        private String topicTitleVi;
        private Long studentGroupId;
        private String studentGroupName;
        private Long reviewerId;
        private String reviewerName;
        private String reviewerCode;
        private String reviewerEmail;
        private Double score;
        private String feedback;
        private LocalDateTime submittedAt;
        private LocalDateTime assignedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder periodId(Long periodId) { this.periodId = periodId; return this; }
        public Builder periodName(String periodName) { this.periodName = periodName; return this; }
        public Builder topicId(Long topicId) { this.topicId = topicId; return this; }
        public Builder topicCode(String topicCode) { this.topicCode = topicCode; return this; }
        public Builder topicTitleVi(String topicTitleVi) { this.topicTitleVi = topicTitleVi; return this; }
        public Builder studentGroupId(Long studentGroupId) { this.studentGroupId = studentGroupId; return this; }
        public Builder studentGroupName(String studentGroupName) { this.studentGroupName = studentGroupName; return this; }
        public Builder reviewerId(Long reviewerId) { this.reviewerId = reviewerId; return this; }
        public Builder reviewerName(String reviewerName) { this.reviewerName = reviewerName; return this; }
        public Builder reviewerCode(String reviewerCode) { this.reviewerCode = reviewerCode; return this; }
        public Builder reviewerEmail(String reviewerEmail) { this.reviewerEmail = reviewerEmail; return this; }
        public Builder score(Double score) { this.score = score; return this; }
        public Builder feedback(String feedback) { this.feedback = feedback; return this; }
        public Builder submittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; return this; }
        public Builder assignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; return this; }
        public ReviewerAssignmentDto build() {
            return new ReviewerAssignmentDto(id, periodId, periodName, topicId, topicCode, topicTitleVi, studentGroupId, studentGroupName, reviewerId, reviewerName, reviewerCode, reviewerEmail, score, feedback, submittedAt, assignedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPeriodId() { return periodId; }
    public void setPeriodId(Long periodId) { this.periodId = periodId; }
    public String getPeriodName() { return periodName; }
    public void setPeriodName(String periodName) { this.periodName = periodName; }
    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }
    public String getTopicCode() { return topicCode; }
    public void setTopicCode(String topicCode) { this.topicCode = topicCode; }
    public String getTopicTitleVi() { return topicTitleVi; }
    public void setTopicTitleVi(String topicTitleVi) { this.topicTitleVi = topicTitleVi; }
    public Long getStudentGroupId() { return studentGroupId; }
    public void setStudentGroupId(Long studentGroupId) { this.studentGroupId = studentGroupId; }
    public String getStudentGroupName() { return studentGroupName; }
    public void setStudentGroupName(String studentGroupName) { this.studentGroupName = studentGroupName; }
    public Long getReviewerId() { return reviewerId; }
    public void setReviewerId(Long reviewerId) { this.reviewerId = reviewerId; }
    public String getReviewerName() { return reviewerName; }
    public void setReviewerName(String reviewerName) { this.reviewerName = reviewerName; }
    public String getReviewerCode() { return reviewerCode; }
    public void setReviewerCode(String reviewerCode) { this.reviewerCode = reviewerCode; }
    public String getReviewerEmail() { return reviewerEmail; }
    public void setReviewerEmail(String reviewerEmail) { this.reviewerEmail = reviewerEmail; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }
}
