package com.daltweb.topicmanagement.entity;

import com.daltweb.topicmanagement.constant.TopicApprovalStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "topics")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, unique = true)
    private String code;

    @Column(name = "title_vi", nullable = false, length = 255)
    private String titleVi;

    @Column(name = "title_en", length = 255)
    private String titleEn;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    @Column(name = "max_students", nullable = false)
    private Integer maxStudents = 3;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "headLecturer"})
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "period_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "createdBy"})
    private RegistrationPeriod period;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_advisor_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "department"})
    private User primaryAdvisor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "co_advisor_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "department"})
    private User coAdvisor;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", nullable = false, length = 20)
    private TopicApprovalStatus approvalStatus = TopicApprovalStatus.PENDING;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password"})
    private User approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "is_assigned", nullable = false)
    private Boolean isAssigned = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Topic() {}

    public Topic(Long id, String code, String titleVi, String titleEn, String description, String requirements, Integer maxStudents, Department department, RegistrationPeriod period, User primaryAdvisor, User coAdvisor, TopicApprovalStatus approvalStatus, String rejectionReason, User approvedBy, LocalDateTime approvedAt, Boolean isAssigned, LocalDateTime createdAt) {
        this.id = id;
        this.code = code;
        this.titleVi = titleVi;
        this.titleEn = titleEn;
        this.description = description;
        this.requirements = requirements;
        this.maxStudents = maxStudents != null ? maxStudents : 3;
        this.department = department;
        this.period = period;
        this.primaryAdvisor = primaryAdvisor;
        this.coAdvisor = coAdvisor;
        this.approvalStatus = approvalStatus != null ? approvalStatus : TopicApprovalStatus.PENDING;
        this.rejectionReason = rejectionReason;
        this.approvedBy = approvedBy;
        this.approvedAt = approvedAt;
        this.isAssigned = isAssigned != null ? isAssigned : false;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String code;
        private String titleVi;
        private String titleEn;
        private String description;
        private String requirements;
        private Integer maxStudents = 3;
        private Department department;
        private RegistrationPeriod period;
        private User primaryAdvisor;
        private User coAdvisor;
        private TopicApprovalStatus approvalStatus = TopicApprovalStatus.PENDING;
        private String rejectionReason;
        private User approvedBy;
        private LocalDateTime approvedAt;
        private Boolean isAssigned = false;
        private LocalDateTime createdAt = LocalDateTime.now();

        public Builder id(Long id) { this.id = id; return this; }
        public Builder code(String code) { this.code = code; return this; }
        public Builder titleVi(String titleVi) { this.titleVi = titleVi; return this; }
        public Builder titleEn(String titleEn) { this.titleEn = titleEn; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder requirements(String requirements) { this.requirements = requirements; return this; }
        public Builder maxStudents(Integer maxStudents) { this.maxStudents = maxStudents; return this; }
        public Builder department(Department department) { this.department = department; return this; }
        public Builder period(RegistrationPeriod period) { this.period = period; return this; }
        public Builder primaryAdvisor(User primaryAdvisor) { this.primaryAdvisor = primaryAdvisor; return this; }
        public Builder coAdvisor(User coAdvisor) { this.coAdvisor = coAdvisor; return this; }
        public Builder approvalStatus(TopicApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; return this; }
        public Builder rejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; return this; }
        public Builder approvedBy(User approvedBy) { this.approvedBy = approvedBy; return this; }
        public Builder approvedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; return this; }
        public Builder isAssigned(Boolean isAssigned) { this.isAssigned = isAssigned; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Topic build() {
            return new Topic(id, code, titleVi, titleEn, description, requirements, maxStudents, department, period, primaryAdvisor, coAdvisor, approvalStatus, rejectionReason, approvedBy, approvedAt, isAssigned, createdAt);
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
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
    public RegistrationPeriod getPeriod() { return period; }
    public void setPeriod(RegistrationPeriod period) { this.period = period; }
    public User getPrimaryAdvisor() { return primaryAdvisor; }
    public void setPrimaryAdvisor(User primaryAdvisor) { this.primaryAdvisor = primaryAdvisor; }
    public User getCoAdvisor() { return coAdvisor; }
    public void setCoAdvisor(User coAdvisor) { this.coAdvisor = coAdvisor; }
    public TopicApprovalStatus getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(TopicApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public User getApprovedBy() { return approvedBy; }
    public void setApprovedBy(User approvedBy) { this.approvedBy = approvedBy; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    public Boolean getIsAssigned() { return isAssigned; }
    public void setIsAssigned(Boolean isAssigned) { this.isAssigned = isAssigned; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
