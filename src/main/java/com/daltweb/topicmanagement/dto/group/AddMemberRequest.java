package com.daltweb.topicmanagement.dto.group;

import jakarta.validation.constraints.NotNull;

public class AddMemberRequest {
    @NotNull(message = "ID sinh viên không được để trống")
    private Long studentId;

    public AddMemberRequest() {}
    public AddMemberRequest(Long studentId) { this.studentId = studentId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
}
