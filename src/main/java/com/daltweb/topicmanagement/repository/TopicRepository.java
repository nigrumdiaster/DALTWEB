package com.daltweb.topicmanagement.repository;

import com.daltweb.topicmanagement.entity.*;
import com.daltweb.topicmanagement.constant.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findByPeriodId(Long periodId);
    long countByPeriodId(Long periodId);
    boolean existsByPeriodId(Long periodId);

    @Query("SELECT t.period.id AS periodId, COUNT(t) AS total FROM Topic t WHERE t.period.id IN :ids GROUP BY t.period.id")
    List<PeriodCount> countForPeriods(@Param("ids") Collection<Long> ids);

    @Query("SELECT t FROM Topic t WHERE " +
           "(:periodId IS NULL OR t.period.id = :periodId) AND " +
           "(:departmentId IS NULL OR t.department.id = :departmentId) AND " +
           "(:status IS NULL OR t.approvalStatus = :status) AND " +
           "(:isAssigned IS NULL OR t.isAssigned = :isAssigned) AND " +
           "(:advisorId IS NULL OR t.primaryAdvisor.id = :advisorId) AND " +
           "(LOWER(t.code) LIKE :pattern OR LOWER(t.titleVi) LIKE :pattern OR " +
           "LOWER(t.titleEn) LIKE :pattern OR LOWER(t.primaryAdvisor.fullName) LIKE :pattern OR " +
           "LOWER(t.department.name) LIKE :pattern)")
    @EntityGraph(attributePaths = {"department", "period", "primaryAdvisor", "approvedBy"})
    Page<Topic> filterTopicsByPattern(@Param("periodId") Long periodId, @Param("departmentId") Long departmentId,
            @Param("status") TopicApprovalStatus status, @Param("isAssigned") Boolean isAssigned,
            @Param("pattern") String pattern, @Param("advisorId") Long advisorId, Pageable pageable);

    default Page<Topic> filterTopics(Long periodId, Long departmentId, TopicApprovalStatus status,
            Boolean isAssigned, String keyword, Long advisorId, Pageable pageable) {
        String pattern = "%" + (keyword == null ? "" : keyword.toLowerCase(Locale.ROOT)) + "%";
        return filterTopicsByPattern(periodId, departmentId, status, isAssigned, pattern, advisorId, pageable);
    }

}
