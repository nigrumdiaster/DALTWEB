package com.daltweb.topicmanagement.dto.council;

import jakarta.validation.constraints.NotNull;

public class FinalizeCouncilResultRequest {

    @NotNull(message = "ID đề tài không được để trống")
    private Long topicId;

    private String chairmanSummary;

    public FinalizeCouncilResultRequest() {}

    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }
    public String getChairmanSummary() { return chairmanSummary; }
    public void setChairmanSummary(String chairmanSummary) { this.chairmanSummary = chairmanSummary; }
}
