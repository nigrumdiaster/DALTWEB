package com.daltweb.topicmanagement.repository;

import com.daltweb.topicmanagement.constant.RoleType;
import com.daltweb.topicmanagement.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findAllByOrderByIsPinnedDescCreatedAtDesc();

    @Query("SELECT a FROM Announcement a WHERE " +
           "(a.targetRole IS NULL OR a.targetRole = :role) " +
           "ORDER BY a.isPinned DESC, a.createdAt DESC")
    List<Announcement> findAnnouncementsForRole(@Param("role") RoleType role);
}
