package com.daltweb.topicmanagement.repository;

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
    List<FinalResult> findByPeriodIdAndIsPublishedTrue(Long periodId);
    Optional<FinalResult> findByTopicId(Long topicId);
    Optional<FinalResult> findByStudentGroupId(Long studentGroupId);

    @Query("SELECT fr FROM FinalResult fr " +
           "JOIN fr.studentGroup g " +
           "JOIN g.members m " +
           "WHERE m.student.id = :studentId AND fr.isPublished = true")
    List<FinalResult> findPublishedResultsByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT fr FROM FinalResult fr " +
           "JOIN fr.studentGroup g " +
           "JOIN g.members m " +
           "WHERE m.student.id = :studentId AND fr.period.id = :periodId")
    Optional<FinalResult> findByStudentIdAndPeriodId(@Param("studentId") Long studentId, @Param("periodId") Long periodId);
}
