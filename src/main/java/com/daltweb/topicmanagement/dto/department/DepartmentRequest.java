package com.daltweb.topicmanagement.dto.department;

import jakarta.validation.constraints.NotBlank;

public class DepartmentRequest {
    @NotBlank(message = "Mã bộ môn không được để trống")
    private String code;

    @NotBlank(message = "Tên bộ môn không được để trống")
    private String name;

    private String description;
    private Long headLecturerId;

    public DepartmentRequest() {}

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getHeadLecturerId() { return headLecturerId; }
    public void setHeadLecturerId(Long headLecturerId) { this.headLecturerId = headLecturerId; }
}
