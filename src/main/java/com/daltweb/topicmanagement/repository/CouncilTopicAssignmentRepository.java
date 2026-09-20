package com.daltweb.topicmanagement.repository;

import com.daltweb.topicmanagement.entity.CouncilTopicAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouncilTopicAssignmentRepository extends JpaRepository<CouncilTopicAssignment, Long> {
    List<CouncilTopicAssignment> findByCouncilId(Long councilId);
    Optional<CouncilTopicAssignment> findByTopicId(Long topicId);
    Boolean existsByCouncilIdAndTopicId(Long councilId, Long topicId);
}
