package com.daltweb.topicmanagement.dto.grade;

import com.daltweb.topicmanagement.dto.group.StudentGroupDto;
import com.daltweb.topicmanagement.dto.topic.TopicDto;
import java.time.LocalDateTime;
import java.util.List;

public class FinalResultDto {
    private Long id;
    private Long periodId;
    private String periodName;

    private Long topicId;
    private String topicTitleVi;
    private TopicDto topic;

    private Long studentGroupId;
    private String studentGroupName;
    private StudentGroupDto studentGroup;

    private Double advisorAvgScore;
    private Double reviewerScore;
    private Double councilAvgScore;
    private Double finalScore;
    private String letterGrade;
    private Boolean passed;
    private Boolean isPublished;
    private LocalDateTime publishedAt;
    private String chairmanSummary;

    private List<GradeDto> detailedGrades;

    public FinalResultDto() {}

    public FinalResultDto(Long id, Long periodId, String periodName, Long topicId, String topicTitleVi, TopicDto topic, Long studentGroupId, String studentGroupName, StudentGroupDto studentGroup, Double advisorAvgScore, Double reviewerScore, Double councilAvgScore, Double finalScore, String letterGrade, Boolean passed, Boolean isPublished, LocalDateTime publishedAt, String chairmanSummary, List<GradeDto> detailedGrades) {
        this.id = id;
        this.periodId = periodId;
        this.periodName = periodName;
        this.topicId = topicId;
        this.topicTitleVi = topicTitleVi;
        this.topic = topic;
        this.studentGroupId = studentGroupId;
        this.studentGroupName = studentGroupName;
        this.studentGroup = studentGroup;
        this.advisorAvgScore = advisorAvgScore;
        this.reviewerScore = reviewerScore;
        this.councilAvgScore = councilAvgScore;
        this.finalScore = finalScore;
        this.letterGrade = letterGrade;
        this.passed = passed;
        this.isPublished = isPublished;
        this.publishedAt = publishedAt;
        this.chairmanSummary = chairmanSummary;
        this.detailedGrades = detailedGrades;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long periodId;
        private String periodName;
        private Long topicId;
        private String topicTitleVi;
        private TopicDto topic;
        private Long studentGroupId;
        private String studentGroupName;
        private StudentGroupDto studentGroup;
        private Double advisorAvgScore;
        private Double reviewerScore;
        private Double councilAvgScore;
        private Double finalScore;
        private String letterGrade;
        private Boolean passed;
        private Boolean isPublished;
        private LocalDateTime publishedAt;
        private String chairmanSummary;
        private List<GradeDto> detailedGrades;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder periodId(Long periodId) { this.periodId = periodId; return this; }
        public Builder periodName(String periodName) { this.periodName = periodName; return this; }
        public Builder topicId(Long topicId) { this.topicId = topicId; return this; }
        public Builder topicTitleVi(String topicTitleVi) { this.topicTitleVi = topicTitleVi; return this; }
        public Builder topic(TopicDto topic) { this.topic = topic; return this; }
        public Builder studentGroupId(Long studentGroupId) { this.studentGroupId = studentGroupId; return this; }
        public Builder studentGroupName(String studentGroupName) { this.studentGroupName = studentGroupName; return this; }
        public Builder studentGroup(StudentGroupDto studentGroup) { this.studentGroup = studentGroup; return this; }
        public Builder advisorAvgScore(Double advisorAvgScore) { this.advisorAvgScore = advisorAvgScore; return this; }
        public Builder reviewerScore(Double reviewerScore) { this.reviewerScore = reviewerScore; return this; }
        public Builder councilAvgScore(Double councilAvgScore) { this.councilAvgScore = councilAvgScore; return this; }
        public Builder finalScore(Double finalScore) { this.finalScore = finalScore; return this; }
        public Builder letterGrade(String letterGrade) { this.letterGrade = letterGrade; return this; }
        public Builder passed(Boolean passed) { this.passed = passed; return this; }
        public Builder isPublished(Boolean isPublished) { this.isPublished = isPublished; return this; }
        public Builder publishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; return this; }
        public Builder chairmanSummary(String chairmanSummary) { this.chairmanSummary = chairmanSummary; return this; }
        public Builder detailedGrades(List<GradeDto> detailedGrades) { this.detailedGrades = detailedGrades; return this; }
        public FinalResultDto build() {
            return new FinalResultDto(id, periodId, periodName, topicId, topicTitleVi, topic, studentGroupId, studentGroupName, studentGroup, advisorAvgScore, reviewerScore, councilAvgScore, finalScore, letterGrade, passed, isPublished, publishedAt, chairmanSummary, detailedGrades);
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
    public String getTopicTitleVi() { return topicTitleVi; }
    public void setTopicTitleVi(String topicTitleVi) { this.topicTitleVi = topicTitleVi; }
    public TopicDto getTopic() { return topic; }
    public void setTopic(TopicDto topic) { this.topic = topic; }
    public Long getStudentGroupId() { return studentGroupId; }
    public void setStudentGroupId(Long studentGroupId) { this.studentGroupId = studentGroupId; }
    public String getStudentGroupName() { return studentGroupName; }
    public void setStudentGroupName(String studentGroupName) { this.studentGroupName = studentGroupName; }
    public StudentGroupDto getStudentGroup() { return studentGroup; }
    public void setStudentGroup(StudentGroupDto studentGroup) { this.studentGroup = studentGroup; }
    public Double getAdvisorAvgScore() { return advisorAvgScore; }
    public void setAdvisorAvgScore(Double advisorAvgScore) { this.advisorAvgScore = advisorAvgScore; }
    public Double getReviewerScore() { return reviewerScore; }
    public void setReviewerScore(Double reviewerScore) { this.reviewerScore = reviewerScore; }
    public Double getCouncilAvgScore() { return councilAvgScore; }
    public void setCouncilAvgScore(Double councilAvgScore) { this.councilAvgScore = councilAvgScore; }
    public Double getFinalScore() { return finalScore; }
    public void setFinalScore(Double finalScore) { this.finalScore = finalScore; }
    public String getLetterGrade() { return letterGrade; }
    public void setLetterGrade(String letterGrade) { this.letterGrade = letterGrade; }
    public Boolean getPassed() { return passed; }
    public void setPassed(Boolean passed) { this.passed = passed; }
    public Boolean getIsPublished() { return isPublished; }
    public void setIsPublished(Boolean isPublished) { this.isPublished = isPublished; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
    public String getChairmanSummary() { return chairmanSummary; }
    public void setChairmanSummary(String chairmanSummary) { this.chairmanSummary = chairmanSummary; }
    public List<GradeDto> getDetailedGrades() { return detailedGrades; }
    public void setDetailedGrades(List<GradeDto> detailedGrades) { this.detailedGrades = detailedGrades; }
}
