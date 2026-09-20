package com.daltweb.topicmanagement.dto.auth;

import com.daltweb.topicmanagement.constant.RoleType;

public class LoginResponse {
    private String token;
    private String tokenType = "Bearer";
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String code;
    private RoleType role;
    private Long departmentId;
    private String departmentName;
    private String studentClass;

    public LoginResponse() {}

    public LoginResponse(String token, String tokenType, Long id, String username, String fullName, String email, String code, RoleType role, Long departmentId, String departmentName, String studentClass) {
        this.token = token;
        this.tokenType = tokenType != null ? tokenType : "Bearer";
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.code = code;
        this.role = role;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.studentClass = studentClass;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String token;
        private String tokenType = "Bearer";
        private Long id;
        private String username;
        private String fullName;
        private String email;
        private String code;
        private RoleType role;
        private Long departmentId;
        private String departmentName;
        private String studentClass;

        public Builder token(String token) { this.token = token; return this; }
        public Builder tokenType(String tokenType) { this.tokenType = tokenType; return this; }
        public Builder id(Long id) { this.id = id; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder code(String code) { this.code = code; return this; }
        public Builder role(RoleType role) { this.role = role; return this; }
        public Builder departmentId(Long departmentId) { this.departmentId = departmentId; return this; }
        public Builder departmentName(String departmentName) { this.departmentName = departmentName; return this; }
        public Builder studentClass(String studentClass) { this.studentClass = studentClass; return this; }
        public LoginResponse build() {
            return new LoginResponse(token, tokenType, id, username, fullName, email, code, role, departmentId, departmentName, studentClass);
        }
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
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
}
