package com.daltweb.topicmanagement.dto.announcement;

import com.daltweb.topicmanagement.constant.RoleType;
import java.time.LocalDateTime;

public class AnnouncementDto {
    private Long id;
    private String title;
    private String content;
    private RoleType targetRole;
    private Boolean isPinned;
    private String fileUrl;
    private Long authorId;
    private String authorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AnnouncementDto() {}

    public AnnouncementDto(Long id, String title, String content, RoleType targetRole, Boolean isPinned, String fileUrl, Long authorId, String authorName, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.targetRole = targetRole;
        this.isPinned = isPinned;
        this.fileUrl = fileUrl;
        this.authorId = authorId;
        this.authorName = authorName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String title;
        private String content;
        private RoleType targetRole;
        private Boolean isPinned;
        private String fileUrl;
        private Long authorId;
        private String authorName;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder content(String content) { this.content = content; return this; }
        public Builder targetRole(RoleType targetRole) { this.targetRole = targetRole; return this; }
        public Builder isPinned(Boolean isPinned) { this.isPinned = isPinned; return this; }
        public Builder fileUrl(String fileUrl) { this.fileUrl = fileUrl; return this; }
        public Builder authorId(Long authorId) { this.authorId = authorId; return this; }
        public Builder authorName(String authorName) { this.authorName = authorName; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public AnnouncementDto build() {
            return new AnnouncementDto(id, title, content, targetRole, isPinned, fileUrl, authorId, authorName, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
