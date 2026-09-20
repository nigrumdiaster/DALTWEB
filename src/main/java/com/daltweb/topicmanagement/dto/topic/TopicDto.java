package com.daltweb.topicmanagement.dto.topic;

import com.daltweb.topicmanagement.constant.TopicApprovalStatus;
import java.time.LocalDateTime;

public class TopicDto {
    private Long id;
    private String code;
    private String titleVi;
    private String titleEn;
    private String description;
    private String requirements;
    private Integer maxStudents;

    private Long departmentId;
    private String departmentName;

    private Long periodId;
    private String periodName;

    private Long primaryAdvisorId;
    private String primaryAdvisorName;
    private String primaryAdvisorEmail;

    private Long coAdvisorId;
    private String coAdvisorName;
    private String coAdvisorEmail;

    private TopicApprovalStatus approvalStatus;
    private String rejectionReason;
    private Long approvedById;
    private String approvedByName;
    private LocalDateTime approvedAt;

    private Boolean isAssigned;
    private Long assignedGroupId;
    private String assignedGroupName;

    private LocalDateTime createdAt;

    public TopicDto() {}

    public TopicDto(Long id, String code, String titleVi, String titleEn, String description, String requirements, Integer maxStudents, Long departmentId, String departmentName, Long periodId, String periodName, Long primaryAdvisorId, String primaryAdvisorName, String primaryAdvisorEmail, Long coAdvisorId, String coAdvisorName, String coAdvisorEmail, TopicApprovalStatus approvalStatus, String rejectionReason, Long approvedById, String approvedByName, LocalDateTime approvedAt, Boolean isAssigned, Long assignedGroupId, String assignedGroupName, LocalDateTime createdAt) {
        this.id = id;
        this.code = code;
        this.titleVi = titleVi;
        this.titleEn = titleEn;
        this.description = description;
        this.requirements = requirements;
        this.maxStudents = maxStudents;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.periodId = periodId;
        this.periodName = periodName;
        this.primaryAdvisorId = primaryAdvisorId;
        this.primaryAdvisorName = primaryAdvisorName;
        this.primaryAdvisorEmail = primaryAdvisorEmail;
        this.coAdvisorId = coAdvisorId;
        this.coAdvisorName = coAdvisorName;
        this.coAdvisorEmail = coAdvisorEmail;
        this.approvalStatus = approvalStatus;
        this.rejectionReason = rejectionReason;
        this.approvedById = approvedById;
        this.approvedByName = approvedByName;
        this.approvedAt = approvedAt;
        this.isAssigned = isAssigned;
        this.assignedGroupId = assignedGroupId;
        this.assignedGroupName = assignedGroupName;
        this.createdAt = createdAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String code;
        private String titleVi;
        private String titleEn;
        private String description;
        private String requirements;
        private Integer maxStudents;
        private Long departmentId;
        private String departmentName;
        private Long periodId;
        private String periodName;
        private Long primaryAdvisorId;
        private String primaryAdvisorName;
        private String primaryAdvisorEmail;
        private Long coAdvisorId;
        private String coAdvisorName;
        private String coAdvisorEmail;
        private TopicApprovalStatus approvalStatus;
        private String rejectionReason;
        private Long approvedById;
        private String approvedByName;
        private LocalDateTime approvedAt;
        private Boolean isAssigned;
        private Long assignedGroupId;
        private String assignedGroupName;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder code(String code) { this.code = code; return this; }
        public Builder titleVi(String titleVi) { this.titleVi = titleVi; return this; }
        public Builder titleEn(String titleEn) { this.titleEn = titleEn; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder requirements(String requirements) { this.requirements = requirements; return this; }
        public Builder maxStudents(Integer maxStudents) { this.maxStudents = maxStudents; return this; }
        public Builder departmentId(Long departmentId) { this.departmentId = departmentId; return this; }
        public Builder departmentName(String departmentName) { this.departmentName = departmentName; return this; }
        public Builder periodId(Long periodId) { this.periodId = periodId; return this; }
        public Builder periodName(String periodName) { this.periodName = periodName; return this; }
        public Builder primaryAdvisorId(Long primaryAdvisorId) { this.primaryAdvisorId = primaryAdvisorId; return this; }
        public Builder primaryAdvisorName(String primaryAdvisorName) { this.primaryAdvisorName = primaryAdvisorName; return this; }
        public Builder primaryAdvisorEmail(String primaryAdvisorEmail) { this.primaryAdvisorEmail = primaryAdvisorEmail; return this; }
        public Builder coAdvisorId(Long coAdvisorId) { this.coAdvisorId = coAdvisorId; return this; }
        public Builder coAdvisorName(String coAdvisorName) { this.coAdvisorName = coAdvisorName; return this; }
        public Builder coAdvisorEmail(String coAdvisorEmail) { this.coAdvisorEmail = coAdvisorEmail; return this; }
        public Builder approvalStatus(TopicApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; return this; }
        public Builder rejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; return this; }
        public Builder approvedById(Long approvedById) { this.approvedById = approvedById; return this; }
        public Builder approvedByName(String approvedByName) { this.approvedByName = approvedByName; return this; }
        public Builder approvedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; return this; }
        public Builder isAssigned(Boolean isAssigned) { this.isAssigned = isAssigned; return this; }
        public Builder assignedGroupId(Long assignedGroupId) { this.assignedGroupId = assignedGroupId; return this; }
        public Builder assignedGroupName(String assignedGroupName) { this.assignedGroupName = assignedGroupName; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public TopicDto build() {
            return new TopicDto(id, code, titleVi, titleEn, description, requirements, maxStudents, departmentId, departmentName, periodId, periodName, primaryAdvisorId, primaryAdvisorName, primaryAdvisorEmail, coAdvisorId, coAdvisorName, coAdvisorEmail, approvalStatus, rejectionReason, approvedById, approvedByName, approvedAt, isAssigned, assignedGroupId, assignedGroupName, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getTitleVi() { return titleVi; }
    public void setTitleVi(String titleVi) { this.titleVi = titleVi; }
    public String getTitleEn() { return titleEn; }
    public void setTitleEn(String titleEn) { this.titleEn = titleEn; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }
    public Integer getMaxStudents() { return maxStudents; }
    public void setMaxStudents(Integer maxStudents) { this.maxStudents = maxStudents; }
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public Long getPeriodId() { return periodId; }
    public void setPeriodId(Long periodId) { this.periodId = periodId; }
    public String getPeriodName() { return periodName; }
    public void setPeriodName(String periodName) { this.periodName = periodName; }
    public Long getPrimaryAdvisorId() { return primaryAdvisorId; }
    public void setPrimaryAdvisorId(Long primaryAdvisorId) { this.primaryAdvisorId = primaryAdvisorId; }
    public String getPrimaryAdvisorName() { return primaryAdvisorName; }
    public void setPrimaryAdvisorName(String primaryAdvisorName) { this.primaryAdvisorName = primaryAdvisorName; }
    public String getPrimaryAdvisorEmail() { return primaryAdvisorEmail; }
    public void setPrimaryAdvisorEmail(String primaryAdvisorEmail) { this.primaryAdvisorEmail = primaryAdvisorEmail; }
    public Long getCoAdvisorId() { return coAdvisorId; }
    public void setCoAdvisorId(Long coAdvisorId) { this.coAdvisorId = coAdvisorId; }
    public String getCoAdvisorName() { return coAdvisorName; }
    public void setCoAdvisorName(String coAdvisorName) { this.coAdvisorName = coAdvisorName; }
    public String getCoAdvisorEmail() { return coAdvisorEmail; }
    public void setCoAdvisorEmail(String coAdvisorEmail) { this.coAdvisorEmail = coAdvisorEmail; }
    public TopicApprovalStatus getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(TopicApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public Long getApprovedById() { return approvedById; }
    public void setApprovedById(Long approvedById) { this.approvedById = approvedById; }
    public String getApprovedByName() { return approvedByName; }
    public void setApprovedByName(String approvedByName) { this.approvedByName = approvedByName; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    public Boolean getIsAssigned() { return isAssigned; }
    public void setIsAssigned(Boolean isAssigned) { this.isAssigned = isAssigned; }
    public Long getAssignedGroupId() { return assignedGroupId; }
    public void setAssignedGroupId(Long assignedGroupId) { this.assignedGroupId = assignedGroupId; }
    public String getAssignedGroupName() { return assignedGroupName; }
    public void setAssignedGroupName(String assignedGroupName) { this.assignedGroupName = assignedGroupName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
