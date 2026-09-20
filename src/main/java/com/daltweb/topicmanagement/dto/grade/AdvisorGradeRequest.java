package com.daltweb.topicmanagement.dto.grade;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class AdvisorGradeRequest {

    @NotNull(message = "ID đề tài không được để trống")
    private Long topicId;

    @NotNull(message = "Điểm hướng dẫn không được để trống")
    @DecimalMin(value = "0.0", message = "Điểm tối thiểu là 0.0")
    @DecimalMax(value = "10.0", message = "Điểm tối đa là 10.0")
    private Double score;

    private String feedback;

    public AdvisorGradeRequest() {}

    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
}
