package com.daltweb.topicmanagement.dto.council;

import java.time.LocalDateTime;

public class CouncilTopicAssignmentDto {
    private Long id;
    private Long topicId;
    private String topicCode;
    private String topicTitleVi;
    private Long primaryAdvisorId;
    private String primaryAdvisorName;
    private Long coAdvisorId;
    private String coAdvisorName;

    private Long studentGroupId;
    private String studentGroupName;

    private LocalDateTime defenseTime;
    private String notes;

    public CouncilTopicAssignmentDto() {}

    public CouncilTopicAssignmentDto(Long id, Long topicId, String topicCode, String topicTitleVi, Long primaryAdvisorId, String primaryAdvisorName, Long coAdvisorId, String coAdvisorName, Long studentGroupId, String studentGroupName, LocalDateTime defenseTime, String notes) {
        this.id = id;
        this.topicId = topicId;
        this.topicCode = topicCode;
        this.topicTitleVi = topicTitleVi;
        this.primaryAdvisorId = primaryAdvisorId;
        this.primaryAdvisorName = primaryAdvisorName;
        this.coAdvisorId = coAdvisorId;
        this.coAdvisorName = coAdvisorName;
        this.studentGroupId = studentGroupId;
        this.studentGroupName = studentGroupName;
        this.defenseTime = defenseTime;
        this.notes = notes;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long topicId;
        private String topicCode;
        private String topicTitleVi;
        private Long primaryAdvisorId;
        private String primaryAdvisorName;
        private Long coAdvisorId;
        private String coAdvisorName;
        private Long studentGroupId;
        private String studentGroupName;
        private LocalDateTime defenseTime;
        private String notes;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder topicId(Long topicId) { this.topicId = topicId; return this; }
        public Builder topicCode(String topicCode) { this.topicCode = topicCode; return this; }
        public Builder topicTitleVi(String topicTitleVi) { this.topicTitleVi = topicTitleVi; return this; }
        public Builder primaryAdvisorId(Long primaryAdvisorId) { this.primaryAdvisorId = primaryAdvisorId; return this; }
        public Builder primaryAdvisorName(String primaryAdvisorName) { this.primaryAdvisorName = primaryAdvisorName; return this; }
        public Builder coAdvisorId(Long coAdvisorId) { this.coAdvisorId = coAdvisorId; return this; }
        public Builder coAdvisorName(String coAdvisorName) { this.coAdvisorName = coAdvisorName; return this; }
        public Builder studentGroupId(Long studentGroupId) { this.studentGroupId = studentGroupId; return this; }
        public Builder studentGroupName(String studentGroupName) { this.studentGroupName = studentGroupName; return this; }
        public Builder defenseTime(LocalDateTime defenseTime) { this.defenseTime = defenseTime; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }
        public CouncilTopicAssignmentDto build() {
            return new CouncilTopicAssignmentDto(id, topicId, topicCode, topicTitleVi, primaryAdvisorId, primaryAdvisorName, coAdvisorId, coAdvisorName, studentGroupId, studentGroupName, defenseTime, notes);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }
    public String getTopicCode() { return topicCode; }
    public void setTopicCode(String topicCode) { this.topicCode = topicCode; }
    public String getTopicTitleVi() { return topicTitleVi; }
    public void setTopicTitleVi(String topicTitleVi) { this.topicTitleVi = topicTitleVi; }
    public Long getPrimaryAdvisorId() { return primaryAdvisorId; }
    public void setPrimaryAdvisorId(Long primaryAdvisorId) { this.primaryAdvisorId = primaryAdvisorId; }
    public String getPrimaryAdvisorName() { return primaryAdvisorName; }
    public void setPrimaryAdvisorName(String primaryAdvisorName) { this.primaryAdvisorName = primaryAdvisorName; }
    public Long getCoAdvisorId() { return coAdvisorId; }
    public void setCoAdvisorId(Long coAdvisorId) { this.coAdvisorId = coAdvisorId; }
    public String getCoAdvisorName() { return coAdvisorName; }
    public void setCoAdvisorName(String coAdvisorName) { this.coAdvisorName = coAdvisorName; }
    public Long getStudentGroupId() { return studentGroupId; }
    public void setStudentGroupId(Long studentGroupId) { this.studentGroupId = studentGroupId; }
    public String getStudentGroupName() { return studentGroupName; }
    public void setStudentGroupName(String studentGroupName) { this.studentGroupName = studentGroupName; }
    public LocalDateTime getDefenseTime() { return defenseTime; }
    public void setDefenseTime(LocalDateTime defenseTime) { this.defenseTime = defenseTime; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
