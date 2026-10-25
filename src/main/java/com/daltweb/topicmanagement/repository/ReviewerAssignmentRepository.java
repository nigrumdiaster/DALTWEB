package com.daltweb.topicmanagement.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import com.daltweb.topicmanagement.entity.ReviewerAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewerAssignmentRepository extends JpaRepository<ReviewerAssignment, Long> {
    void deleteByTopicId(Long topicId);
    Page<ReviewerAssignment> findByPeriodIdAndTopicIdIn(Long periodId, List<Long> topicIds, Pageable pageable);

    Optional<ReviewerAssignment> findByTopicId(Long topicId);
    Optional<ReviewerAssignment> findByPeriodIdAndTopicId(Long periodId, Long topicId);

    Page<ReviewerAssignment> findByPeriodId(Long periodId, Pageable pageable);

    Page<ReviewerAssignment> findByReviewerId(Long reviewerId, Pageable pageable);

}
