package com.daltweb.topicmanagement.repository;

import com.daltweb.topicmanagement.constant.PeriodStatus;
import com.daltweb.topicmanagement.constant.PeriodType;
import com.daltweb.topicmanagement.entity.RegistrationPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationPeriodRepository extends JpaRepository<RegistrationPeriod, Long> {
    List<RegistrationPeriod> findAllByOrderByCreatedAtDesc();
    List<RegistrationPeriod> findByStatus(PeriodStatus status);
    List<RegistrationPeriod> findByType(PeriodType type);

    @Query("SELECT p FROM RegistrationPeriod p WHERE " +
           "(:type IS NULL OR p.type = :type) AND " +
           "(:status IS NULL OR p.status = :status) " +
           "ORDER BY p.createdAt DESC")
    List<RegistrationPeriod> filterPeriods(@Param("type") PeriodType type,
                                           @Param("status") PeriodStatus status);
}
