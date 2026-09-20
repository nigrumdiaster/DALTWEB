package com.daltweb.topicmanagement.dto.group;

import jakarta.validation.constraints.NotNull;

public class RegisterTopicRequest {
    @NotNull(message = "ID đề tài không được để trống")
    private Long topicId;

    public RegisterTopicRequest() {}
    public RegisterTopicRequest(Long topicId) { this.topicId = topicId; }

    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }
}
