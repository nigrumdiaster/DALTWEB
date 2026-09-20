package com.daltweb.topicmanagement.repository;

import com.daltweb.topicmanagement.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    List<GroupMember> findByGroupId(Long groupId);
    List<GroupMember> findByStudentId(Long studentId);
    Optional<GroupMember> findByGroupIdAndStudentId(Long groupId, Long studentId);

    @Query("SELECT gm FROM GroupMember gm WHERE gm.student.id = :studentId AND gm.group.period.id = :periodId")
    Optional<GroupMember> findByStudentIdAndPeriodId(@Param("studentId") Long studentId, @Param("periodId") Long periodId);

    @Query("SELECT COUNT(gm) > 0 FROM GroupMember gm WHERE gm.student.id = :studentId AND gm.group.period.id = :periodId")
    Boolean existsByStudentIdAndPeriodId(@Param("studentId") Long studentId, @Param("periodId") Long periodId);
}
