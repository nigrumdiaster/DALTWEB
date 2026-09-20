package com.daltweb.topicmanagement.repository;

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
    List<StudentGroup> findByPeriodId(Long periodId);
    Optional<StudentGroup> findByTopicId(Long topicId);
    Optional<StudentGroup> findByCode(String code);

    @Query("SELECT g FROM StudentGroup g WHERE " +
           "(:periodId IS NULL OR g.period.id = :periodId) AND " +
           "(:status IS NULL OR g.status = :status)")
    List<StudentGroup> filterGroups(@Param("periodId") Long periodId,
                                    @Param("status") GroupStatus status);
}
