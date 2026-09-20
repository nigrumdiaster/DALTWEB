package com.daltweb.topicmanagement.dto.reviewer;

import jakarta.validation.constraints.NotNull;

public class AssignReviewerRequest {

    @NotNull(message = "ID đợt đăng ký không được để trống")
    private Long periodId;

    @NotNull(message = "ID đề tài không được để trống")
    private Long topicId;

    @NotNull(message = "ID giảng viên phản biện không được để trống")
    private Long reviewerId;

    public AssignReviewerRequest() {}

    public Long getPeriodId() { return periodId; }
    public void setPeriodId(Long periodId) { this.periodId = periodId; }
    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }
    public Long getReviewerId() { return reviewerId; }
    public void setReviewerId(Long reviewerId) { this.reviewerId = reviewerId; }
}
