package com.daltweb.topicmanagement.dto.auth;

import com.daltweb.topicmanagement.constant.RoleType;
import java.time.LocalDateTime;

public class UserDto {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String code;
    private RoleType role;
    private Long departmentId;
    private String departmentName;
    private String studentClass;
    private Boolean active;
    private LocalDateTime createdAt;

    public UserDto() {}

    public UserDto(Long id, String username, String fullName, String email, String phone, String code, RoleType role, Long departmentId, String departmentName, String studentClass, Boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.code = code;
        this.role = role;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.studentClass = studentClass;
        this.active = active;
        this.createdAt = createdAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String username;
        private String fullName;
        private String email;
        private String phone;
        private String code;
        private RoleType role;
        private Long departmentId;
        private String departmentName;
        private String studentClass;
        private Boolean active;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder code(String code) { this.code = code; return this; }
        public Builder role(RoleType role) { this.role = role; return this; }
        public Builder departmentId(Long departmentId) { this.departmentId = departmentId; return this; }
        public Builder departmentName(String departmentName) { this.departmentName = departmentName; return this; }
        public Builder studentClass(String studentClass) { this.studentClass = studentClass; return this; }
        public Builder active(Boolean active) { this.active = active; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public UserDto build() { return new UserDto(id, username, fullName, email, phone, code, role, departmentId, departmentName, studentClass, active, createdAt); }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public RoleType getRole() { return role; }
    public void setRole(RoleType role) { this.role = role; }
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public String getStudentClass() { return studentClass; }
    public void setStudentClass(String studentClass) { this.studentClass = studentClass; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
