package com.daltweb.topicmanagement.entity;

import com.daltweb.topicmanagement.constant.GroupStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "student_groups")
public class StudentGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, unique = true)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "period_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private RegistrationPeriod period;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Topic topic;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private GroupStatus status = GroupStatus.FORMING;

    @Column(name = "report_url", length = 500)
    private String reportUrl;

    @Column(name = "report_note", length = 1000)
    private String reportNote;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMember> members = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public StudentGroup() {}

    public StudentGroup(Long id, String code, String name, RegistrationPeriod period, Topic topic, GroupStatus status, String reportUrl, String reportNote, LocalDateTime submittedAt, List<GroupMember> members, LocalDateTime createdAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.period = period;
        this.topic = topic;
        this.status = status != null ? status : GroupStatus.FORMING;
        this.reportUrl = reportUrl;
        this.reportNote = reportNote;
        this.submittedAt = submittedAt;
        this.members = members != null ? members : new ArrayList<>();
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String code;
        private String name;
        private RegistrationPeriod period;
        private Topic topic;
        private GroupStatus status = GroupStatus.FORMING;
        private String reportUrl;
        private String reportNote;
        private LocalDateTime submittedAt;
        private List<GroupMember> members = new ArrayList<>();
        private LocalDateTime createdAt = LocalDateTime.now();

        public Builder id(Long id) { this.id = id; return this; }
        public Builder code(String code) { this.code = code; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder period(RegistrationPeriod period) { this.period = period; return this; }
        public Builder topic(Topic topic) { this.topic = topic; return this; }
        public Builder status(GroupStatus status) { this.status = status; return this; }
        public Builder reportUrl(String reportUrl) { this.reportUrl = reportUrl; return this; }
        public Builder reportNote(String reportNote) { this.reportNote = reportNote; return this; }
        public Builder submittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; return this; }
        public Builder members(List<GroupMember> members) { this.members = members; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public StudentGroup build() {
            return new StudentGroup(id, code, name, period, topic, status, reportUrl, reportNote, submittedAt, members, createdAt);
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
    public Topic getTopic() { return topic; }
    public void setTopic(Topic topic) { this.topic = topic; }
    public GroupStatus getStatus() { return status; }
    public void setStatus(GroupStatus status) { this.status = status; }
    public String getReportUrl() { return reportUrl; }
    public void setReportUrl(String reportUrl) { this.reportUrl = reportUrl; }
    public String getReportNote() { return reportNote; }
    public void setReportNote(String reportNote) { this.reportNote = reportNote; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public List<GroupMember> getMembers() { return members; }
    public void setMembers(List<GroupMember> members) { this.members = members; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
