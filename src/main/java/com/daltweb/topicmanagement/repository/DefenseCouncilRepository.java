package com.daltweb.topicmanagement.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import com.daltweb.topicmanagement.entity.DefenseCouncil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DefenseCouncilRepository extends JpaRepository<DefenseCouncil, Long> {

    @org.springframework.data.jpa.repository.Query("SELECT c FROM DefenseCouncil c WHERE c.period.id = :periodId AND " +
            "(:lecturerId IS NULL OR EXISTS (SELECT m.id FROM CouncilMember m WHERE m.council = c AND m.lecturer.id = :lecturerId))")
    Page<DefenseCouncil> findByPeriodId(@org.springframework.data.repository.query.Param("periodId") Long periodId,
            @org.springframework.data.repository.query.Param("lecturerId") Long lecturerId, Pageable pageable);
}
