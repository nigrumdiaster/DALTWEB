package com.daltweb.topicmanagement.event;

import com.daltweb.topicmanagement.constant.RoleType;

// Snapshot values so background delivery does not depend on managed JPA entities.
public record AnnouncementCreatedEvent(Long id, String title, String content, RoleType targetRole,
                                       String authorName, String fileUrl) {
}
