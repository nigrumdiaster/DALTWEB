package com.daltweb.topicmanagement.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import com.daltweb.topicmanagement.entity.FinalResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinalResultRepository extends JpaRepository<FinalResult, Long> {
    List<FinalResult> findByPeriodId(Long periodId);

    Optional<FinalResult> findByTopicId(Long topicId);

    Page<FinalResult> findByPeriodId(Long periodId, Pageable pageable);

    @Query("SELECT fr FROM FinalResult fr " +
           "JOIN fr.studentGroup g " +
           "JOIN g.members m " +
           "WHERE m.student.id = :studentId AND fr.isPublished = true")
    Page<FinalResult> findPublishedResultsByStudentId(@Param("studentId") Long studentId, Pageable pageable);

    @Query("SELECT fr FROM FinalResult fr JOIN fr.studentGroup.members m " +
            "WHERE m.student.id = :studentId AND fr.isPublished = true AND fr.period.id = :periodId")
    Page<FinalResult> findPublishedResultsByStudentIdAndPeriodId(@Param("studentId") Long studentId,
            @Param("periodId") Long periodId, Pageable pageable);
}
