package com.daltweb.topicmanagement.entity;

import com.daltweb.topicmanagement.constant.GroupRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_members",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"group_id", "student_id"})
       })
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "members"})
    private StudentGroup group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password"})
    private User student;

    @Enumerated(EnumType.STRING)
    @Column(name = "group_role", nullable = false, length = 20)
    private GroupRole groupRole = GroupRole.MEMBER;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt = LocalDateTime.now();

    public GroupMember() {}

    public GroupMember(Long id, StudentGroup group, User student, GroupRole groupRole, LocalDateTime joinedAt) {
        this.id = id;
        this.group = group;
        this.student = student;
        this.groupRole = groupRole != null ? groupRole : GroupRole.MEMBER;
        this.joinedAt = joinedAt != null ? joinedAt : LocalDateTime.now();
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private StudentGroup group;
        private User student;
        private GroupRole groupRole = GroupRole.MEMBER;
        private LocalDateTime joinedAt = LocalDateTime.now();

        public Builder id(Long id) { this.id = id; return this; }
        public Builder group(StudentGroup group) { this.group = group; return this; }
        public Builder student(User student) { this.student = student; return this; }
        public Builder groupRole(GroupRole groupRole) { this.groupRole = groupRole; return this; }
        public Builder joinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; return this; }
        public GroupMember build() { return new GroupMember(id, group, student, groupRole, joinedAt); }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public StudentGroup getGroup() { return group; }
    public void setGroup(StudentGroup group) { this.group = group; }
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    public GroupRole getGroupRole() { return groupRole; }
    public void setGroupRole(GroupRole groupRole) { this.groupRole = groupRole; }
    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
}
