package com.daltweb.topicmanagement.entity;

import com.daltweb.topicmanagement.constant.GraderRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "grades")
public class Grade {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grader_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password"})
    private User grader;

    @Enumerated(EnumType.STRING)
    @Column(name = "grader_role", nullable = false, length = 30)
    private GraderRole graderRole;

    @Column(nullable = false)
    private Double score;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "graded_at", nullable = false, updatable = false)
    private LocalDateTime gradedAt = LocalDateTime.now();

    public Grade() {}

    public Grade(Long id, RegistrationPeriod period, Topic topic, StudentGroup studentGroup, User grader, GraderRole graderRole, Double score, String feedback, LocalDateTime gradedAt) {
        this.id = id;
        this.period = period;
        this.topic = topic;
        this.studentGroup = studentGroup;
        this.grader = grader;
        this.graderRole = graderRole;
        this.score = score;
        this.feedback = feedback;
        this.gradedAt = gradedAt != null ? gradedAt : LocalDateTime.now();
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private RegistrationPeriod period;
        private Topic topic;
        private StudentGroup studentGroup;
        private User grader;
        private GraderRole graderRole;
        private Double score;
        private String feedback;
        private LocalDateTime gradedAt = LocalDateTime.now();

        public Builder id(Long id) { this.id = id; return this; }
        public Builder period(RegistrationPeriod period) { this.period = period; return this; }
        public Builder topic(Topic topic) { this.topic = topic; return this; }
        public Builder studentGroup(StudentGroup studentGroup) { this.studentGroup = studentGroup; return this; }
        public Builder grader(User grader) { this.grader = grader; return this; }
        public Builder graderRole(GraderRole graderRole) { this.graderRole = graderRole; return this; }
        public Builder score(Double score) { this.score = score; return this; }
        public Builder feedback(String feedback) { this.feedback = feedback; return this; }
        public Builder gradedAt(LocalDateTime gradedAt) { this.gradedAt = gradedAt; return this; }
        public Grade build() { return new Grade(id, period, topic, studentGroup, grader, graderRole, score, feedback, gradedAt); }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public RegistrationPeriod getPeriod() { return period; }
    public void setPeriod(RegistrationPeriod period) { this.period = period; }
    public Topic getTopic() { return topic; }
    public void setTopic(Topic topic) { this.topic = topic; }
    public StudentGroup getStudentGroup() { return studentGroup; }
    public void setStudentGroup(StudentGroup studentGroup) { this.studentGroup = studentGroup; }
    public User getGrader() { return grader; }
    public void setGrader(User grader) { this.grader = grader; }
    public GraderRole getGraderRole() { return graderRole; }
    public void setGraderRole(GraderRole graderRole) { this.graderRole = graderRole; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    public LocalDateTime getGradedAt() { return gradedAt; }
    public void setGradedAt(LocalDateTime gradedAt) { this.gradedAt = gradedAt; }
}
