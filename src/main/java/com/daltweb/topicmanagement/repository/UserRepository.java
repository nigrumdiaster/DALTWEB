package com.daltweb.topicmanagement.repository;

import com.daltweb.topicmanagement.constant.RoleType;
import com.daltweb.topicmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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
           "(:keyword IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.code) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<User> searchUsers(@Param("role") RoleType role,
                           @Param("departmentId") Long departmentId,
                           @Param("keyword") String keyword);
}
