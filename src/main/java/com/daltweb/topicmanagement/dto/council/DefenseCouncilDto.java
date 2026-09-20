package com.daltweb.topicmanagement.dto.council;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DefenseCouncilDto {
    private Long id;
    private String code;
    private String name;
    private Long periodId;
    private String periodName;
    private LocalDate defenseDate;
    private String location;
    private String notes;

    private List<CouncilMemberDto> members = new ArrayList<>();
    private List<CouncilTopicAssignmentDto> assignedTopics = new ArrayList<>();
    private LocalDateTime createdAt;

    public DefenseCouncilDto() {}

    public DefenseCouncilDto(Long id, String code, String name, Long periodId, String periodName, LocalDate defenseDate, String location, String notes, List<CouncilMemberDto> members, List<CouncilTopicAssignmentDto> assignedTopics, LocalDateTime createdAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.periodId = periodId;
        this.periodName = periodName;
        this.defenseDate = defenseDate;
        this.location = location;
        this.notes = notes;
        this.members = members != null ? members : new ArrayList<>();
        this.assignedTopics = assignedTopics != null ? assignedTopics : new ArrayList<>();
        this.createdAt = createdAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String code;
        private String name;
        private Long periodId;
        private String periodName;
        private LocalDate defenseDate;
        private String location;
        private String notes;
        private List<CouncilMemberDto> members = new ArrayList<>();
        private List<CouncilTopicAssignmentDto> assignedTopics = new ArrayList<>();
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder code(String code) { this.code = code; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder periodId(Long periodId) { this.periodId = periodId; return this; }
        public Builder periodName(String periodName) { this.periodName = periodName; return this; }
        public Builder defenseDate(LocalDate defenseDate) { this.defenseDate = defenseDate; return this; }
        public Builder location(String location) { this.location = location; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }
        public Builder members(List<CouncilMemberDto> members) { this.members = members; return this; }
        public Builder assignedTopics(List<CouncilTopicAssignmentDto> assignedTopics) { this.assignedTopics = assignedTopics; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public DefenseCouncilDto build() {
            return new DefenseCouncilDto(id, code, name, periodId, periodName, defenseDate, location, notes, members, assignedTopics, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getPeriodId() { return periodId; }
    public void setPeriodId(Long periodId) { this.periodId = periodId; }
    public String getPeriodName() { return periodName; }
    public void setPeriodName(String periodName) { this.periodName = periodName; }
    public LocalDate getDefenseDate() { return defenseDate; }
    public void setDefenseDate(LocalDate defenseDate) { this.defenseDate = defenseDate; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public List<CouncilMemberDto> getMembers() { return members; }
    public void setMembers(List<CouncilMemberDto> members) { this.members = members; }
    public List<CouncilTopicAssignmentDto> getAssignedTopics() { return assignedTopics; }
    public void setAssignedTopics(List<CouncilTopicAssignmentDto> assignedTopics) { this.assignedTopics = assignedTopics; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
