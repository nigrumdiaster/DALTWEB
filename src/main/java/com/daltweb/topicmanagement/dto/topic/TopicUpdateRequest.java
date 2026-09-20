package com.daltweb.topicmanagement.dto.topic;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TopicUpdateRequest {

    @NotBlank(message = "Tên đề tài tiếng Việt không được để trống")
    private String titleVi;

    private String titleEn;

    @NotBlank(message = "Nội dung và mục tiêu đề tài không được để trống")
    private String description;

    private String requirements;

    @NotNull(message = "Số lượng sinh viên tối đa không được để trống")
    @Min(value = 1, message = "Số lượng sinh viên tối thiểu là 1")
    @Max(value = 3, message = "Số lượng sinh viên tối đa là 3")
    private Integer maxStudents = 3;

    @NotNull(message = "Bộ môn không được để trống")
    private Long departmentId;

    private Long coAdvisorId;

    public TopicUpdateRequest() {}

    public String getTitleVi() { return titleVi; }
    public void setTitleVi(String titleVi) { this.titleVi = titleVi; }
    public String getTitleEn() { return titleEn; }
    public void setTitleEn(String titleEn) { this.titleEn = titleEn; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }
    public Integer getMaxStudents() { return maxStudents; }
    public void setMaxStudents(Integer maxStudents) { this.maxStudents = maxStudents; }
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
    public Long getCoAdvisorId() { return coAdvisorId; }
    public void setCoAdvisorId(Long coAdvisorId) { this.coAdvisorId = coAdvisorId; }
}
