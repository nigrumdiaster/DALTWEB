package com.daltweb.topicmanagement.repository;

import com.daltweb.topicmanagement.constant.RoleType;
import com.daltweb.topicmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Boolean existsByCode(String code);

    List<User> findByRole(RoleType role);
    List<User> findByDepartmentId(Long departmentId);
    List<User> findByRoleAndDepartmentId(RoleType role, Long departmentId);

    @Query("SELECT u FROM User u WHERE " +
           "(:role IS NULL OR u.role = :role) AND " +
           "(:departmentId IS NULL OR u.department.id = :departmentId) AND " +
           "(LOWER(u.fullName) LIKE :pattern OR " +
           "LOWER(u.username) LIKE :pattern OR " +
           "LOWER(u.code) LIKE :pattern)")
    List<User> searchUsersByPattern(@Param("role") RoleType role,
                           @Param("departmentId") Long departmentId,
                           @Param("pattern") String pattern);

    default List<User> searchUsers(RoleType role, Long departmentId, String keyword) {
        String pattern = "%" + (keyword == null ? "" : keyword.toLowerCase(Locale.ROOT)) + "%";
        return searchUsersByPattern(role, departmentId, pattern);
    }
}
