package com.daltweb.topicmanagement.dto.group;

import com.daltweb.topicmanagement.constant.GroupRole;
import java.time.LocalDateTime;

public class GroupMemberDto {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentCode;
    private String studentEmail;
    private String studentPhone;
    private String studentClass;
    private GroupRole groupRole;
    private LocalDateTime joinedAt;

    public GroupMemberDto() {}

    public GroupMemberDto(Long id, Long studentId, String studentName, String studentCode, String studentEmail, String studentPhone, String studentClass, GroupRole groupRole, LocalDateTime joinedAt) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentCode = studentCode;
        this.studentEmail = studentEmail;
        this.studentPhone = studentPhone;
        this.studentClass = studentClass;
        this.groupRole = groupRole;
        this.joinedAt = joinedAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long studentId;
        private String studentName;
        private String studentCode;
        private String studentEmail;
        private String studentPhone;
        private String studentClass;
        private GroupRole groupRole;
        private LocalDateTime joinedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder studentId(Long studentId) { this.studentId = studentId; return this; }
        public Builder studentName(String studentName) { this.studentName = studentName; return this; }
        public Builder studentCode(String studentCode) { this.studentCode = studentCode; return this; }
        public Builder studentEmail(String studentEmail) { this.studentEmail = studentEmail; return this; }
        public Builder studentPhone(String studentPhone) { this.studentPhone = studentPhone; return this; }
        public Builder studentClass(String studentClass) { this.studentClass = studentClass; return this; }
        public Builder groupRole(GroupRole groupRole) { this.groupRole = groupRole; return this; }
        public Builder joinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; return this; }
        public GroupMemberDto build() { return new GroupMemberDto(id, studentId, studentName, studentCode, studentEmail, studentPhone, studentClass, groupRole, joinedAt); }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getStudentCode() { return studentCode; }
    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }
    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }
    public String getStudentPhone() { return studentPhone; }
    public void setStudentPhone(String studentPhone) { this.studentPhone = studentPhone; }
    public String getStudentClass() { return studentClass; }
    public void setStudentClass(String studentClass) { this.studentClass = studentClass; }
    public GroupRole getGroupRole() { return groupRole; }
    public void setGroupRole(GroupRole groupRole) { this.groupRole = groupRole; }
    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
}
