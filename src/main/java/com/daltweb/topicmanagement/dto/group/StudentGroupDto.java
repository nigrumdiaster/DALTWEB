package com.daltweb.topicmanagement.dto.group;

import com.daltweb.topicmanagement.constant.GroupStatus;
import com.daltweb.topicmanagement.dto.topic.TopicDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StudentGroupDto {
    private Long id;
    private String code;
    private String name;
    private Long periodId;
    private String periodName;

    private Long topicId;
    private String topicTitleVi;
    private TopicDto topic;

    private GroupStatus status;
    private String reportUrl;
    private String reportNote;
    private LocalDateTime submittedAt;

    private Long leaderId;
    private String leaderName;

    private List<GroupMemberDto> members = new ArrayList<>();
    private LocalDateTime createdAt;

    public StudentGroupDto() {}

    public StudentGroupDto(Long id, String code, String name, Long periodId, String periodName, Long topicId, String topicTitleVi, TopicDto topic, GroupStatus status, String reportUrl, String reportNote, LocalDateTime submittedAt, Long leaderId, String leaderName, List<GroupMemberDto> members, LocalDateTime createdAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.periodId = periodId;
        this.periodName = periodName;
        this.topicId = topicId;
        this.topicTitleVi = topicTitleVi;
        this.topic = topic;
        this.status = status;
        this.reportUrl = reportUrl;
        this.reportNote = reportNote;
        this.submittedAt = submittedAt;
        this.leaderId = leaderId;
        this.leaderName = leaderName;
        this.members = members != null ? members : new ArrayList<>();
        this.createdAt = createdAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String code;
        private String name;
        private Long periodId;
        private String periodName;
        private Long topicId;
        private String topicTitleVi;
        private TopicDto topic;
        private GroupStatus status;
        private String reportUrl;
        private String reportNote;
        private LocalDateTime submittedAt;
        private Long leaderId;
        private String leaderName;
        private List<GroupMemberDto> members = new ArrayList<>();
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder code(String code) { this.code = code; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder periodId(Long periodId) { this.periodId = periodId; return this; }
        public Builder periodName(String periodName) { this.periodName = periodName; return this; }
        public Builder topicId(Long topicId) { this.topicId = topicId; return this; }
        public Builder topicTitleVi(String topicTitleVi) { this.topicTitleVi = topicTitleVi; return this; }
        public Builder topic(TopicDto topic) { this.topic = topic; return this; }
        public Builder status(GroupStatus status) { this.status = status; return this; }
        public Builder reportUrl(String reportUrl) { this.reportUrl = reportUrl; return this; }
        public Builder reportNote(String reportNote) { this.reportNote = reportNote; return this; }
        public Builder submittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; return this; }
        public Builder leaderId(Long leaderId) { this.leaderId = leaderId; return this; }
        public Builder leaderName(String leaderName) { this.leaderName = leaderName; return this; }
        public Builder members(List<GroupMemberDto> members) { this.members = members; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public StudentGroupDto build() {
            return new StudentGroupDto(id, code, name, periodId, periodName, topicId, topicTitleVi, topic, status, reportUrl, reportNote, submittedAt, leaderId, leaderName, members, createdAt);
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
    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }
    public String getTopicTitleVi() { return topicTitleVi; }
    public void setTopicTitleVi(String topicTitleVi) { this.topicTitleVi = topicTitleVi; }
    public TopicDto getTopic() { return topic; }
    public void setTopic(TopicDto topic) { this.topic = topic; }
    public GroupStatus getStatus() { return status; }
    public void setStatus(GroupStatus status) { this.status = status; }
    public String getReportUrl() { return reportUrl; }
    public void setReportUrl(String reportUrl) { this.reportUrl = reportUrl; }
    public String getReportNote() { return reportNote; }
    public void setReportNote(String reportNote) { this.reportNote = reportNote; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public Long getLeaderId() { return leaderId; }
    public void setLeaderId(Long leaderId) { this.leaderId = leaderId; }
    public String getLeaderName() { return leaderName; }
    public void setLeaderName(String leaderName) { this.leaderName = leaderName; }
    public List<GroupMemberDto> getMembers() { return members; }
    public void setMembers(List<GroupMemberDto> members) { this.members = members; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
