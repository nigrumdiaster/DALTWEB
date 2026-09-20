package com.daltweb.topicmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviewer_assignments",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"period_id", "topic_id"})
       })
public class ReviewerAssignment {

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
    @JoinColumn(name = "student_group_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private StudentGroup studentGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password"})
    private User reviewer;

    @Column(name = "score")
    private Double score;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "assigned_at", nullable = false, updatable = false)
    private LocalDateTime assignedAt = LocalDateTime.now();

    public ReviewerAssignment() {}

    public ReviewerAssignment(Long id, RegistrationPeriod period, Topic topic, StudentGroup studentGroup, User reviewer, Double score, String feedback, LocalDateTime submittedAt, LocalDateTime assignedAt) {
        this.id = id;
        this.period = period;
        this.topic = topic;
        this.studentGroup = studentGroup;
        this.reviewer = reviewer;
        this.score = score;
        this.feedback = feedback;
        this.submittedAt = submittedAt;
        this.assignedAt = assignedAt != null ? assignedAt : LocalDateTime.now();
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private RegistrationPeriod period;
        private Topic topic;
        private StudentGroup studentGroup;
        private User reviewer;
        private Double score;
        private String feedback;
        private LocalDateTime submittedAt;
        private LocalDateTime assignedAt = LocalDateTime.now();

        public Builder id(Long id) { this.id = id; return this; }
        public Builder period(RegistrationPeriod period) { this.period = period; return this; }
        public Builder topic(Topic topic) { this.topic = topic; return this; }
        public Builder studentGroup(StudentGroup studentGroup) { this.studentGroup = studentGroup; return this; }
        public Builder reviewer(User reviewer) { this.reviewer = reviewer; return this; }
        public Builder score(Double score) { this.score = score; return this; }
        public Builder feedback(String feedback) { this.feedback = feedback; return this; }
        public Builder submittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; return this; }
        public Builder assignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; return this; }
        public ReviewerAssignment build() { return new ReviewerAssignment(id, period, topic, studentGroup, reviewer, score, feedback, submittedAt, assignedAt); }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public RegistrationPeriod getPeriod() { return period; }
    public void setPeriod(RegistrationPeriod period) { this.period = period; }
    public Topic getTopic() { return topic; }
    public void setTopic(Topic topic) { this.topic = topic; }
    public StudentGroup getStudentGroup() { return studentGroup; }
    public void setStudentGroup(StudentGroup studentGroup) { this.studentGroup = studentGroup; }
    public User getReviewer() { return reviewer; }
    public void setReviewer(User reviewer) { this.reviewer = reviewer; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }
}
