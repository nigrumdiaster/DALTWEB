package com.daltweb.topicmanagement.dto.announcement;

import com.daltweb.topicmanagement.constant.RoleType;
import jakarta.validation.constraints.NotBlank;

public class AnnouncementRequest {

    @NotBlank(message = "Tiêu đề thông báo không được để trống")
    private String title;

    @NotBlank(message = "Nội dung thông báo không được để trống")
    private String content;

    private RoleType targetRole;
    private Boolean isPinned = false;
    private String fileUrl;

    public AnnouncementRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public RoleType getTargetRole() { return targetRole; }
    public void setTargetRole(RoleType targetRole) { this.targetRole = targetRole; }
    public Boolean getIsPinned() { return isPinned; }
    public void setIsPinned(Boolean isPinned) { this.isPinned = isPinned; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
}
