package com.daltweb.topicmanagement.dto.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class GroupCreateRequest {

    @NotBlank(message = "Tên nhóm không được để trống")
    private String name;

    @NotNull(message = "Đợt đăng ký không được để trống")
    private Long periodId;

    private List<Long> memberStudentIds;

    public GroupCreateRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getPeriodId() { return periodId; }
    public void setPeriodId(Long periodId) { this.periodId = periodId; }
    public List<Long> getMemberStudentIds() { return memberStudentIds; }
    public void setMemberStudentIds(List<Long> memberStudentIds) { this.memberStudentIds = memberStudentIds; }
}
