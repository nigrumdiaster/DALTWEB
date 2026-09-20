package com.daltweb.topicmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "defense_councils")
public class DefenseCouncil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, unique = true)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "period_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private RegistrationPeriod period;

    @Column(name = "defense_date")
    private LocalDate defenseDate;

    @Column(length = 100)
    private String location;

    @Column(length = 500)
    private String notes;

    @OneToMany(mappedBy = "council", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CouncilMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "council", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CouncilTopicAssignment> assignedTopics = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public DefenseCouncil() {}

    public DefenseCouncil(Long id, String code, String name, RegistrationPeriod period, LocalDate defenseDate, String location, String notes, List<CouncilMember> members, List<CouncilTopicAssignment> assignedTopics, LocalDateTime createdAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.period = period;
        this.defenseDate = defenseDate;
        this.location = location;
        this.notes = notes;
        this.members = members != null ? members : new ArrayList<>();
        this.assignedTopics = assignedTopics != null ? assignedTopics : new ArrayList<>();
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String code;
        private String name;
        private RegistrationPeriod period;
        private LocalDate defenseDate;
        private String location;
        private String notes;
        private List<CouncilMember> members = new ArrayList<>();
        private List<CouncilTopicAssignment> assignedTopics = new ArrayList<>();
        private LocalDateTime createdAt = LocalDateTime.now();

        public Builder id(Long id) { this.id = id; return this; }
        public Builder code(String code) { this.code = code; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder period(RegistrationPeriod period) { this.period = period; return this; }
        public Builder defenseDate(LocalDate defenseDate) { this.defenseDate = defenseDate; return this; }
        public Builder location(String location) { this.location = location; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }
        public Builder members(List<CouncilMember> members) { this.members = members; return this; }
        public Builder assignedTopics(List<CouncilTopicAssignment> assignedTopics) { this.assignedTopics = assignedTopics; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public DefenseCouncil build() {
            return new DefenseCouncil(id, code, name, period, defenseDate, location, notes, members, assignedTopics, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public RegistrationPeriod getPeriod() { return period; }
    public void setPeriod(RegistrationPeriod period) { this.period = period; }
    public LocalDate getDefenseDate() { return defenseDate; }
    public void setDefenseDate(LocalDate defenseDate) { this.defenseDate = defenseDate; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public List<CouncilMember> getMembers() { return members; }
    public void setMembers(List<CouncilMember> members) { this.members = members; }
    public List<CouncilTopicAssignment> getAssignedTopics() { return assignedTopics; }
    public void setAssignedTopics(List<CouncilTopicAssignment> assignedTopics) { this.assignedTopics = assignedTopics; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
