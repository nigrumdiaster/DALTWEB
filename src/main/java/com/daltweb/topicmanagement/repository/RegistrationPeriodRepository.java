package com.daltweb.topicmanagement.repository;

import com.daltweb.topicmanagement.entity.*;
import com.daltweb.topicmanagement.constant.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface RegistrationPeriodRepository extends JpaRepository<RegistrationPeriod, Long> {
    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM RegistrationPeriod p WHERE p.id = :id")
    Optional<RegistrationPeriod> findForUpdate(@Param("id") Long id);

    @Query("SELECT p FROM RegistrationPeriod p WHERE (:type IS NULL OR p.type = :type) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:excludeDraft = false OR p.status <> com.daltweb.topicmanagement.constant.PeriodStatus.DRAFT) AND " +
           "(LOWER(p.name) LIKE :pattern OR LOWER(p.semester) LIKE :pattern OR LOWER(p.academicYear) LIKE :pattern) " +
           "ORDER BY p.createdAt DESC")
    Page<RegistrationPeriod> filterPeriods(@Param("type") PeriodType type, @Param("status") PeriodStatus status,
            @Param("pattern") String pattern, @Param("excludeDraft") boolean excludeDraft, Pageable pageable);

    @Query("SELECT DISTINCT p FROM RegistrationPeriod p JOIN StudentGroup g ON g.period = p " +
           "JOIN g.members m WHERE m.student.id = :studentId AND g.topic IS NOT NULL ORDER BY p.createdAt DESC")
    Page<RegistrationPeriod> findRegisteredByStudentId(@Param("studentId") Long studentId, Pageable pageable);

}
