package com.daltweb.topicmanagement.dto.department;

import java.time.LocalDateTime;

public class DepartmentDto {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Long headLecturerId;
    private String headLecturerName;
    private LocalDateTime createdAt;

    public DepartmentDto() {}

    public DepartmentDto(Long id, String code, String name, String description, Long headLecturerId, String headLecturerName, LocalDateTime createdAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.headLecturerId = headLecturerId;
        this.headLecturerName = headLecturerName;
        this.createdAt = createdAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String code;
        private String name;
        private String description;
        private Long headLecturerId;
        private String headLecturerName;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder code(String code) { this.code = code; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder headLecturerId(Long headLecturerId) { this.headLecturerId = headLecturerId; return this; }
        public Builder headLecturerName(String headLecturerName) { this.headLecturerName = headLecturerName; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public DepartmentDto build() { return new DepartmentDto(id, code, name, description, headLecturerId, headLecturerName, createdAt); }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getHeadLecturerId() { return headLecturerId; }
    public void setHeadLecturerId(Long headLecturerId) { this.headLecturerId = headLecturerId; }
    public String getHeadLecturerName() { return headLecturerName; }
    public void setHeadLecturerName(String headLecturerName) { this.headLecturerName = headLecturerName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
