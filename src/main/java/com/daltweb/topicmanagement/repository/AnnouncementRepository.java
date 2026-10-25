package com.daltweb.topicmanagement.repository;

import com.daltweb.topicmanagement.entity.*;
import com.daltweb.topicmanagement.constant.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    @Query("SELECT a FROM Announcement a WHERE " +
           "(:canSeeAll = true OR a.targetRole IS NULL OR a.targetRole = :role) AND " +
           "(:targetRole IS NULL OR a.targetRole = :targetRole) AND " +
           "(LOWER(a.title) LIKE :pattern OR LOWER(a.content) LIKE :pattern OR LOWER(a.author.fullName) LIKE :pattern) " +
           "ORDER BY a.isPinned DESC, a.createdAt DESC")
    @EntityGraph(attributePaths = {"author"})
    Page<Announcement> searchAnnouncements(@Param("role") RoleType role, @Param("canSeeAll") boolean canSeeAll,
            @Param("targetRole") RoleType targetRole, @Param("pattern") String pattern, Pageable pageable);

}
