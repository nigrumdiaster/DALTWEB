package com.daltweb.topicmanagement.repository;

import com.daltweb.topicmanagement.constant.CouncilRole;
import com.daltweb.topicmanagement.entity.CouncilMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouncilMemberRepository extends JpaRepository<CouncilMember, Long> {
    List<CouncilMember> findByCouncilId(Long councilId);
    List<CouncilMember> findByLecturerId(Long lecturerId);
    Boolean existsByCouncilIdAndLecturerId(Long councilId, Long lecturerId);
    Optional<CouncilMember> findByCouncilIdAndCouncilRole(Long councilId, CouncilRole councilRole);
}
