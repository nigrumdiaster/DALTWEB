package com.daltweb.topicmanagement.repository;

import com.daltweb.topicmanagement.constant.GraderRole;
import com.daltweb.topicmanagement.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByTopicId(Long topicId);
    List<Grade> findByStudentGroupId(Long studentGroupId);
    List<Grade> findByPeriodId(Long periodId);
    List<Grade> findByTopicIdAndGraderRole(Long topicId, GraderRole graderRole);
    Optional<Grade> findByTopicIdAndGraderIdAndGraderRole(Long topicId, Long graderId, GraderRole graderRole);

    @Query("SELECT AVG(g.score) FROM Grade g WHERE g.topic.id = :topicId AND g.graderRole IN (:roles)")
    Double findAvgScoreByTopicAndRoles(@Param("topicId") Long topicId, @Param("roles") List<GraderRole> roles);
}
