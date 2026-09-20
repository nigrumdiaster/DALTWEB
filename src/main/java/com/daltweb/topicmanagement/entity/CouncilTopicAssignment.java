package com.daltweb.topicmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "council_topic_assignments",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"council_id", "topic_id"})
       })
public class CouncilTopicAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "council_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "assignedTopics"})
    private DefenseCouncil council;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Topic topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_group_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private StudentGroup studentGroup;

    @Column(name = "defense_time")
    private LocalDateTime defenseTime;

    @Column(length = 500)
    private String notes;

    @Column(name = "assigned_at", nullable = false, updatable = false)
    private LocalDateTime assignedAt = LocalDateTime.now();

    public CouncilTopicAssignment() {}

    public CouncilTopicAssignment(Long id, DefenseCouncil council, Topic topic, StudentGroup studentGroup, LocalDateTime defenseTime, String notes, LocalDateTime assignedAt) {
        this.id = id;
        this.council = council;
        this.topic = topic;
        this.studentGroup = studentGroup;
        this.defenseTime = defenseTime;
        this.notes = notes;
        this.assignedAt = assignedAt != null ? assignedAt : LocalDateTime.now();
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private DefenseCouncil council;
        private Topic topic;
        private StudentGroup studentGroup;
        private LocalDateTime defenseTime;
        private String notes;
        private LocalDateTime assignedAt = LocalDateTime.now();

        public Builder id(Long id) { this.id = id; return this; }
        public Builder council(DefenseCouncil council) { this.council = council; return this; }
        public Builder topic(Topic topic) { this.topic = topic; return this; }
        public Builder studentGroup(StudentGroup studentGroup) { this.studentGroup = studentGroup; return this; }
        public Builder defenseTime(LocalDateTime defenseTime) { this.defenseTime = defenseTime; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }
        public Builder assignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; return this; }
        public CouncilTopicAssignment build() { return new CouncilTopicAssignment(id, council, topic, studentGroup, defenseTime, notes, assignedAt); }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public DefenseCouncil getCouncil() { return council; }
    public void setCouncil(DefenseCouncil council) { this.council = council; }
    public Topic getTopic() { return topic; }
    public void setTopic(Topic topic) { this.topic = topic; }
    public StudentGroup getStudentGroup() { return studentGroup; }
    public void setStudentGroup(StudentGroup studentGroup) { this.studentGroup = studentGroup; }
    public LocalDateTime getDefenseTime() { return defenseTime; }
    public void setDefenseTime(LocalDateTime defenseTime) { this.defenseTime = defenseTime; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }
}
