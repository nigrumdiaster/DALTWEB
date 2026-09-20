package com.daltweb.topicmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "final_results",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"period_id", "topic_id"})
       })
public class FinalResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "period_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private RegistrationPeriod period;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Topic topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_group_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private StudentGroup studentGroup;

    @Column(name = "advisor_avg_score")
    private Double advisorAvgScore;

    @Column(name = "reviewer_score")
    private Double reviewerScore;

    @Column(name = "council_avg_score")
    private Double councilAvgScore;

    @Column(name = "final_score", nullable = false)
    private Double finalScore;

    @Column(name = "letter_grade", length = 5)
    private String letterGrade;

    @Column(nullable = false)
    private Boolean passed = true;

    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = false;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "chairman_summary", columnDefinition = "TEXT")
    private String chairmanSummary;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public FinalResult() {}

    public FinalResult(Long id, RegistrationPeriod period, Topic topic, StudentGroup studentGroup, Double advisorAvgScore, Double reviewerScore, Double councilAvgScore, Double finalScore, String letterGrade, Boolean passed, Boolean isPublished, LocalDateTime publishedAt, String chairmanSummary, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.period = period;
        this.topic = topic;
        this.studentGroup = studentGroup;
        this.advisorAvgScore = advisorAvgScore;
        this.reviewerScore = reviewerScore;
        this.councilAvgScore = councilAvgScore;
        this.finalScore = finalScore;
        this.letterGrade = letterGrade;
        this.passed = passed != null ? passed : true;
        this.isPublished = isPublished != null ? isPublished : false;
        this.publishedAt = publishedAt;
        this.chairmanSummary = chairmanSummary;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private RegistrationPeriod period;
        private Topic topic;
        private StudentGroup studentGroup;
        private Double advisorAvgScore;
        private Double reviewerScore;
        private Double councilAvgScore;
        private Double finalScore;
        private String letterGrade;
        private Boolean passed = true;
        private Boolean isPublished = false;
        private LocalDateTime publishedAt;
        private String chairmanSummary;
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder period(RegistrationPeriod period) { this.period = period; return this; }
        public Builder topic(Topic topic) { this.topic = topic; return this; }
        public Builder studentGroup(StudentGroup studentGroup) { this.studentGroup = studentGroup; return this; }
        public Builder advisorAvgScore(Double advisorAvgScore) { this.advisorAvgScore = advisorAvgScore; return this; }
        public Builder reviewerScore(Double reviewerScore) { this.reviewerScore = reviewerScore; return this; }
        public Builder councilAvgScore(Double councilAvgScore) { this.councilAvgScore = councilAvgScore; return this; }
        public Builder finalScore(Double finalScore) { this.finalScore = finalScore; return this; }
        public Builder letterGrade(String letterGrade) { this.letterGrade = letterGrade; return this; }
        public Builder passed(Boolean passed) { this.passed = passed; return this; }
        public Builder isPublished(Boolean isPublished) { this.isPublished = isPublished; return this; }
        public Builder publishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; return this; }
        public Builder chairmanSummary(String chairmanSummary) { this.chairmanSummary = chairmanSummary; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public FinalResult build() {
            return new FinalResult(id, period, topic, studentGroup, advisorAvgScore, reviewerScore, councilAvgScore, finalScore, letterGrade, passed, isPublished, publishedAt, chairmanSummary, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public RegistrationPeriod getPeriod() { return period; }
    public void setPeriod(RegistrationPeriod period) { this.period = period; }
    public Topic getTopic() { return topic; }
    public void setTopic(Topic topic) { this.topic = topic; }
    public StudentGroup getStudentGroup() { return studentGroup; }
    public void setStudentGroup(StudentGroup studentGroup) { this.studentGroup = studentGroup; }
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
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
