package com.daltweb.topicmanagement.dto.grade;

import jakarta.validation.constraints.NotNull;

public class PublishResultRequest {
    @NotNull(message = "ID đợt đăng ký không được để trống")
    private Long periodId;

    private Boolean publish = true;

    public PublishResultRequest() {}
    public PublishResultRequest(Long periodId, Boolean publish) {
        this.periodId = periodId;
        this.publish = publish != null ? publish : true;
    }

    public Long getPeriodId() { return periodId; }
    public void setPeriodId(Long periodId) { this.periodId = periodId; }
    public Boolean getPublish() { return publish; }
    public void setPublish(Boolean publish) { this.publish = publish; }
}
