package com.daltweb.topicmanagement.dto.council;

import com.daltweb.topicmanagement.constant.CouncilRole;

public class CouncilMemberDto {
    private Long id;
    private Long lecturerId;
    private String lecturerName;
    private String lecturerCode;
    private String lecturerEmail;
    private Long departmentId;
    private String departmentName;
    private CouncilRole councilRole;

    public CouncilMemberDto() {}

    public CouncilMemberDto(Long id, Long lecturerId, String lecturerName, String lecturerCode, String lecturerEmail, Long departmentId, String departmentName, CouncilRole councilRole) {
        this.id = id;
        this.lecturerId = lecturerId;
        this.lecturerName = lecturerName;
        this.lecturerCode = lecturerCode;
        this.lecturerEmail = lecturerEmail;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.councilRole = councilRole;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long lecturerId;
        private String lecturerName;
        private String lecturerCode;
        private String lecturerEmail;
        private Long departmentId;
        private String departmentName;
        private CouncilRole councilRole;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder lecturerId(Long lecturerId) { this.lecturerId = lecturerId; return this; }
        public Builder lecturerName(String lecturerName) { this.lecturerName = lecturerName; return this; }
        public Builder lecturerCode(String lecturerCode) { this.lecturerCode = lecturerCode; return this; }
        public Builder lecturerEmail(String lecturerEmail) { this.lecturerEmail = lecturerEmail; return this; }
        public Builder departmentId(Long departmentId) { this.departmentId = departmentId; return this; }
        public Builder departmentName(String departmentName) { this.departmentName = departmentName; return this; }
        public Builder councilRole(CouncilRole councilRole) { this.councilRole = councilRole; return this; }
        public CouncilMemberDto build() { return new CouncilMemberDto(id, lecturerId, lecturerName, lecturerCode, lecturerEmail, departmentId, departmentName, councilRole); }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getLecturerId() { return lecturerId; }
    public void setLecturerId(Long lecturerId) { this.lecturerId = lecturerId; }
    public String getLecturerName() { return lecturerName; }
    public void setLecturerName(String lecturerName) { this.lecturerName = lecturerName; }
    public String getLecturerCode() { return lecturerCode; }
    public void setLecturerCode(String lecturerCode) { this.lecturerCode = lecturerCode; }
    public String getLecturerEmail() { return lecturerEmail; }
    public void setLecturerEmail(String lecturerEmail) { this.lecturerEmail = lecturerEmail; }
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public CouncilRole getCouncilRole() { return councilRole; }
    public void setCouncilRole(CouncilRole councilRole) { this.councilRole = councilRole; }
}
