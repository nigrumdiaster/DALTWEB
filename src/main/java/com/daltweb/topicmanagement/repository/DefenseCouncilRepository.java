package com.daltweb.topicmanagement.repository;

import com.daltweb.topicmanagement.entity.DefenseCouncil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DefenseCouncilRepository extends JpaRepository<DefenseCouncil, Long> {
    List<DefenseCouncil> findByPeriodId(Long periodId);
}
