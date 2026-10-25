package com.daltweb.topicmanagement.repository;

import com.daltweb.topicmanagement.entity.*;
import com.daltweb.topicmanagement.constant.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Boolean existsByCode(String code);

    @Query("SELECT DISTINCT u.email FROM User u WHERE u.active = true " +
           "AND u.email IS NOT NULL AND TRIM(u.email) <> '' AND (:role IS NULL OR u.role = :role)")
    List<String> findAnnouncementRecipientEmails(@Param("role") RoleType role);

    @Query("SELECT u FROM User u WHERE (:role IS NULL OR u.role = :role) AND " +
           "(:departmentId IS NULL OR u.department.id = :departmentId) AND " +
           "(LOWER(u.fullName) LIKE :pattern OR LOWER(u.username) LIKE :pattern OR " +
           "LOWER(u.code) LIKE :pattern OR LOWER(u.email) LIKE :pattern OR LOWER(u.phone) LIKE :pattern)")
    @EntityGraph(attributePaths = {"department"})
    Page<User> searchUsersByPattern(@Param("role") RoleType role, @Param("departmentId") Long departmentId,
            @Param("pattern") String pattern, Pageable pageable);

    default Page<User> searchUsers(RoleType role, Long departmentId, String keyword, Pageable pageable) {
        String pattern = "%" + (keyword == null ? "" : keyword.toLowerCase(Locale.ROOT)) + "%";
        return searchUsersByPattern(role, departmentId, pattern, pageable);
    }

}
