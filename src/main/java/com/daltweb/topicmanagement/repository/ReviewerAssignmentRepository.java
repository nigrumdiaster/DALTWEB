package com.daltweb.topicmanagement.repository;

import com.daltweb.topicmanagement.entity.ReviewerAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewerAssignmentRepository extends JpaRepository<ReviewerAssignment, Long> {
    List<ReviewerAssignment> findByPeriodId(Long periodId);
    List<ReviewerAssignment> findByReviewerId(Long reviewerId);
    Optional<ReviewerAssignment> findByTopicId(Long topicId);
    Optional<ReviewerAssignment> findByPeriodIdAndTopicId(Long periodId, Long topicId);
    Boolean existsByPeriodIdAndTopicId(Long periodId, Long topicId);
}
