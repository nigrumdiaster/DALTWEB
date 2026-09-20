package com.daltweb.topicmanagement.entity;

import com.daltweb.topicmanagement.constant.CouncilRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "council_members",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"council_id", "lecturer_id"})
       })
public class CouncilMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "council_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "members", "assignedTopics"})
    private DefenseCouncil council;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecturer_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "department"})
    private User lecturer;

    @Enumerated(EnumType.STRING)
    @Column(name = "council_role", nullable = false, length = 30)
    private CouncilRole councilRole = CouncilRole.MEMBER;

    @Column(name = "assigned_at", nullable = false, updatable = false)
    private LocalDateTime assignedAt = LocalDateTime.now();

    public CouncilMember() {}

    public CouncilMember(Long id, DefenseCouncil council, User lecturer, CouncilRole councilRole, LocalDateTime assignedAt) {
        this.id = id;
        this.council = council;
        this.lecturer = lecturer;
        this.councilRole = councilRole != null ? councilRole : CouncilRole.MEMBER;
        this.assignedAt = assignedAt != null ? assignedAt : LocalDateTime.now();
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private DefenseCouncil council;
        private User lecturer;
        private CouncilRole councilRole = CouncilRole.MEMBER;
        private LocalDateTime assignedAt = LocalDateTime.now();

        public Builder id(Long id) { this.id = id; return this; }
        public Builder council(DefenseCouncil council) { this.council = council; return this; }
        public Builder lecturer(User lecturer) { this.lecturer = lecturer; return this; }
        public Builder councilRole(CouncilRole councilRole) { this.councilRole = councilRole; return this; }
        public Builder assignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; return this; }
        public CouncilMember build() { return new CouncilMember(id, council, lecturer, councilRole, assignedAt); }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public DefenseCouncil getCouncil() { return council; }
    public void setCouncil(DefenseCouncil council) { this.council = council; }
    public User getLecturer() { return lecturer; }
    public void setLecturer(User lecturer) { this.lecturer = lecturer; }
    public CouncilRole getCouncilRole() { return councilRole; }
    public void setCouncilRole(CouncilRole councilRole) { this.councilRole = councilRole; }
    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }
}
