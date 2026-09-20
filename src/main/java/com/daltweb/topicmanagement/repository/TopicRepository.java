package com.daltweb.topicmanagement.repository;

import com.daltweb.topicmanagement.constant.TopicApprovalStatus;
import com.daltweb.topicmanagement.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findByPeriodId(Long periodId);
    List<Topic> findByDepartmentId(Long departmentId);
    List<Topic> findByPrimaryAdvisorId(Long primaryAdvisorId);
    List<Topic> findByPeriodIdAndDepartmentId(Long periodId, Long departmentId);
    List<Topic> findByPeriodIdAndApprovalStatus(Long periodId, TopicApprovalStatus approvalStatus);
    List<Topic> findByPeriodIdAndApprovalStatusAndIsAssignedFalse(Long periodId, TopicApprovalStatus approvalStatus);

    @Query("SELECT t FROM Topic t WHERE " +
           "(:periodId IS NULL OR t.period.id = :periodId) AND " +
           "(:departmentId IS NULL OR t.department.id = :departmentId) AND " +
           "(:status IS NULL OR t.approvalStatus = :status) AND " +
           "(:isAssigned IS NULL OR t.isAssigned = :isAssigned) AND " +
           "(:keyword IS NULL OR LOWER(t.titleVi) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.titleEn) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.primaryAdvisor.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Topic> filterTopics(@Param("periodId") Long periodId,
                             @Param("departmentId") Long departmentId,
                             @Param("status") TopicApprovalStatus status,
                             @Param("isAssigned") Boolean isAssigned,
                             @Param("keyword") String keyword);
}
