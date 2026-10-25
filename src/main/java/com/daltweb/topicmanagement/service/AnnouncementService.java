package com.daltweb.topicmanagement.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import com.daltweb.topicmanagement.constant.RoleType;
import com.daltweb.topicmanagement.dto.announcement.AnnouncementDto;
import com.daltweb.topicmanagement.dto.announcement.AnnouncementRequest;
import com.daltweb.topicmanagement.entity.Announcement;
import com.daltweb.topicmanagement.entity.User;
import com.daltweb.topicmanagement.event.AnnouncementCreatedEvent;
import com.daltweb.topicmanagement.exception.ResourceNotFoundException;
import com.daltweb.topicmanagement.repository.AnnouncementRepository;
import com.daltweb.topicmanagement.repository.UserRepository;
import com.daltweb.topicmanagement.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public AnnouncementService(AnnouncementRepository announcementRepository, UserRepository userRepository,
                               ApplicationEventPublisher eventPublisher) {
        this.announcementRepository = announcementRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public Page<AnnouncementDto> getAnnouncements(String keyword, RoleType targetRole, Pageable pageable) {
        RoleType role = SecurityUtils.getCurrentUserRole();
        boolean canSeeAll = role == null || role == RoleType.ROLE_ADMIN || role == RoleType.ROLE_DEAN;
        String pattern = "%" + (keyword == null ? "" : keyword.toLowerCase(java.util.Locale.ROOT)) + "%";
        return announcementRepository.searchAnnouncements(role, canSeeAll, targetRole, pattern, pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public AnnouncementDto getAnnouncementById(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông báo với ID: " + id));
        return mapToDto(announcement);
    }

    @Transactional
    public AnnouncementDto createAnnouncement(AnnouncementRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        Announcement announcement = Announcement.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .targetRole(request.getTargetRole())
                .isPinned(request.getIsPinned() != null ? request.getIsPinned() : false)
                .fileUrl(request.getFileUrl())
                .author(currentUser)
                .build();

        Announcement saved = announcementRepository.save(announcement);
        if (Boolean.TRUE.equals(request.getSendEmail())) {
            eventPublisher.publishEvent(new AnnouncementCreatedEvent(saved.getId(), saved.getTitle(),
                    saved.getContent(), saved.getTargetRole(), currentUser.getFullName(), saved.getFileUrl()));
        }
        return mapToDto(saved);
    }

    @Transactional
    public AnnouncementDto updateAnnouncement(Long id, AnnouncementRequest request) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông báo với ID: " + id));

        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        announcement.setTargetRole(request.getTargetRole());
        if (request.getIsPinned() != null) {
            announcement.setIsPinned(request.getIsPinned());
        }
        announcement.setFileUrl(request.getFileUrl());
        announcement.setUpdatedAt(LocalDateTime.now());

        Announcement updated = announcementRepository.save(announcement);
        return mapToDto(updated);
    }

    @Transactional
    public void deleteAnnouncement(Long id) {
        if (!announcementRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy thông báo với ID: " + id);
        }
        announcementRepository.deleteById(id);
    }

    public AnnouncementDto mapToDto(Announcement a) {
        return AnnouncementDto.builder()
                .id(a.getId())
                .title(a.getTitle())
                .content(a.getContent())
                .targetRole(a.getTargetRole())
                .isPinned(a.getIsPinned())
                .fileUrl(a.getFileUrl())
                .authorId(a.getAuthor() != null ? a.getAuthor().getId() : null)
                .authorName(a.getAuthor() != null ? a.getAuthor().getFullName() : null)
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }
}
