package com.daltweb.topicmanagement.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import com.daltweb.topicmanagement.constant.GroupStatus;
import com.daltweb.topicmanagement.entity.StudentGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentGroupRepository extends JpaRepository<StudentGroup, Long> {
    @Query("SELECT g.period.id AS periodId, COUNT(g) AS total FROM StudentGroup g WHERE g.period.id IN :ids GROUP BY g.period.id")
    List<PeriodCount> countForPeriods(@Param("ids") java.util.Collection<Long> ids);
    List<StudentGroup> findByPeriodId(Long periodId);
    Optional<StudentGroup> findByTopicId(Long topicId);
    Optional<StudentGroup> findByCode(String code);

    @Query("SELECT g FROM StudentGroup g LEFT JOIN g.topic t LEFT JOIN t.coAdvisor co WHERE " +
           "(:periodId IS NULL OR g.period.id = :periodId) AND (:status IS NULL OR g.status = :status) AND " +
           "(:advisorId IS NULL OR t.primaryAdvisor.id = :advisorId OR co.id = :advisorId) AND " +
           "(LOWER(g.name) LIKE :pattern OR LOWER(g.code) LIKE :pattern OR LOWER(t.titleVi) LIKE :pattern OR " +
           "EXISTS (SELECT m.id FROM GroupMember m WHERE m.group = g AND " +
           "(LOWER(m.student.fullName) LIKE :pattern OR LOWER(m.student.code) LIKE :pattern)))")
    Page<StudentGroup> filterGroups(@Param("periodId") Long periodId, @Param("status") GroupStatus status,
            @Param("pattern") String pattern, @Param("advisorId") Long advisorId, Pageable pageable);

    List<StudentGroup> findByTopicIdIn(java.util.Collection<Long> topicIds);
    boolean existsByPeriodId(Long periodId);

    long countByPeriodId(Long periodId);
}
