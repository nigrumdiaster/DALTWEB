package com.daltweb.topicmanagement.service;

import com.daltweb.topicmanagement.constant.*;
import com.daltweb.topicmanagement.dto.topic.*;
import com.daltweb.topicmanagement.entity.*;
import com.daltweb.topicmanagement.exception.BadRequestException;
import com.daltweb.topicmanagement.exception.ForbiddenException;
import com.daltweb.topicmanagement.repository.*;
import com.daltweb.topicmanagement.security.CustomUserDetails;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopicServiceTest {
    @Mock TopicRepository topics;
    @Mock DepartmentRepository departments;
    @Mock RegistrationPeriodRepository periods;
    @Mock UserRepository users;
    @Mock StudentGroupRepository groups;
    @Mock GradeRepository grades;
    @Mock FinalResultRepository results;
    @Mock ReviewerAssignmentRepository reviewers;
    @Mock CouncilTopicAssignmentRepository councilTopics;
    @InjectMocks TopicService service;
    Topic topic;
    Department department;
    User actor;

    @BeforeEach void setup() {
        department = Department.builder().id(2L).build();
        topic = Topic.builder().id(10L).titleVi("Original").maxStudents(3)
                .department(department).primaryAdvisor(User.builder().id(20L).build())
                .period(RegistrationPeriod.builder().id(3L).status(PeriodStatus.IN_PROGRESS).build())
                .approvalStatus(TopicApprovalStatus.APPROVED).isAssigned(false)
                .approvedBy(User.builder().id(99L).build()).approvedAt(LocalDateTime.now()).build();
        when(topics.findById(10L)).thenReturn(Optional.of(topic));
        signIn(1L, RoleType.ROLE_ADMIN);
    }

    void signIn(Long id, RoleType role) {
        actor = User.builder().id(id).username("actor").role(role).active(true).build();
        CustomUserDetails principal = CustomUserDetails.build(actor);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    @AfterEach void clearSecurity() { SecurityContextHolder.clearContext(); }

    TopicUpdateRequest update() {
        TopicUpdateRequest request = new TopicUpdateRequest();
        request.setTitleVi("Updated title");
        request.setDescription("Updated description");
        request.setDepartmentId(2L);
        request.setMaxStudents(3);
        return request;
    }

    TopicApprovalRequest approval(TopicApprovalStatus status) {
        TopicApprovalRequest request = new TopicApprovalRequest();
        request.setApprovalStatus(status);
        return request;
    }

    @ParameterizedTest
    @EnumSource(value = RoleType.class, names = {"ROLE_ADMIN", "ROLE_DEAN"})
    void administratorCanEditApprovedTopicAndKeepApproval(RoleType role) {
        signIn(1L, role);
        when(departments.findById(2L)).thenReturn(Optional.of(department));
        when(topics.save(topic)).thenReturn(topic);
        var approvedAt = topic.getApprovedAt();
        service.updateTopic(10L, update());
        assertThat(topic.getTitleVi()).isEqualTo("Updated title");
        assertThat(topic.getCoAdvisor()).isNull();
        assertThat(topic.getApprovalStatus()).isEqualTo(TopicApprovalStatus.APPROVED);
        assertThat(topic.getApprovedAt()).isEqualTo(approvedAt);
        assertThat(topic.getApprovedBy().getId()).isEqualTo(99L);
    }

    @Test void adminCanEditAssignedTopicWithoutRemovingItsGroup() {
        topic.setIsAssigned(true);
        when(departments.findById(2L)).thenReturn(Optional.of(department));
        when(topics.save(topic)).thenReturn(topic);
        service.updateTopic(10L, update());
        assertThat(topic.getIsAssigned()).isTrue();
        assertThat(topic.getApprovalStatus()).isEqualTo(TopicApprovalStatus.APPROVED);
        verify(groups, never()).save(any());
        verify(groups, never()).delete(any());
    }

    @Test void adminCannotReduceCapacityBelowTheRegisteredGroupSize() {
        topic.setIsAssigned(true);
        when(groups.findByTopicId(10L)).thenReturn(Optional.of(StudentGroup.builder()
                .members(Arrays.asList(new GroupMember(), new GroupMember(), new GroupMember())).build()));
        TopicUpdateRequest request = update();
        request.setMaxStudents(2);
        assertThatThrownBy(() -> service.updateTopic(10L, request)).isInstanceOf(BadRequestException.class);
        verify(topics, never()).save(any());
        assertThat(topic.getTitleVi()).isEqualTo("Original");
    }

    @Test void lecturerStillCannotEditAnAssignedTopic() {
        signIn(20L, RoleType.ROLE_LECTURER);
        topic.setIsAssigned(true);
        assertThatThrownBy(() -> service.updateTopic(10L, update())).isInstanceOf(BadRequestException.class);
        verify(topics, never()).save(any());
    }

    @Test void lecturerEditingOwnUnassignedApprovedTopicMustResubmitForApproval() {
        signIn(20L, RoleType.ROLE_LECTURER);
        when(departments.findById(2L)).thenReturn(Optional.of(department));
        when(topics.save(topic)).thenReturn(topic);
        service.updateTopic(10L, update());
        assertThat(topic.getApprovalStatus()).isEqualTo(TopicApprovalStatus.PENDING);
        assertThat(topic.getApprovedBy()).isNull();
        assertThat(topic.getApprovedAt()).isNull();
    }

    @ParameterizedTest
    @EnumSource(value = RoleType.class, names = {"ROLE_ADMIN", "ROLE_DEAN"})
    void administratorCanManageApprovalOutsideReviewPhase(RoleType role) {
        signIn(1L, role);
        when(users.findById(1L)).thenReturn(Optional.of(actor));
        when(topics.save(topic)).thenReturn(topic);
        service.approveTopic(10L, approval(TopicApprovalStatus.PENDING));
        assertThat(topic.getApprovalStatus()).isEqualTo(TopicApprovalStatus.PENDING);
    }

    @Test void departmentHeadCannotApproveFinally() {
        signIn(1L, RoleType.ROLE_DEPARTMENT_HEAD);
        department.setHeadLecturer(actor);
        assertThatThrownBy(() -> service.approveTopic(10L, approval(TopicApprovalStatus.REJECTED)))
                .isInstanceOf(ForbiddenException.class);
        verify(topics, never()).save(any());
    }

    @Test void assignedTopicCannotLoseApprovalEvenForAdmin() {
        topic.setIsAssigned(true);
        for (var status : List.of(TopicApprovalStatus.PENDING, TopicApprovalStatus.REJECTED)) {
            assertThatThrownBy(() -> service.approveTopic(10L, approval(status))).isInstanceOf(BadRequestException.class);
        }
        verify(topics, never()).save(any());
    }

    @Test void adminCanDeleteAnUnassignedApprovedTopic() {
        service.deleteTopic(10L);
        verify(topics).delete(topic);
    }

    @Test void adminDeletesAssignedTopicButPreservesGroupAndMembers() {
        topic.setIsAssigned(true);
        StudentGroup group = StudentGroup.builder().topic(topic).status(GroupStatus.SUBMITTED)
                .reportUrl("https://example.com/report").reportNote("Old report")
                .submittedAt(LocalDateTime.now()).members(new ArrayList<>(List.of(new GroupMember()))).build();
        when(groups.findByTopicIdIn(List.of(10L))).thenReturn(List.of(group));
        service.deleteTopic(10L);
        assertThat(group.getTopic()).isNull();
        assertThat(group.getStatus()).isEqualTo(GroupStatus.FORMING);
        assertThat(group.getMembers()).hasSize(1);
        assertThat(group.getReportUrl()).isNull();
        assertThat(group.getReportNote()).isNull();
        assertThat(group.getSubmittedAt()).isNull();
        verify(results).deleteByTopicId(10L);
        verify(grades).deleteByTopicId(10L);
        verify(reviewers).deleteByTopicId(10L);
        verify(councilTopics).deleteByTopicId(10L);
        verify(topics).delete(topic);
    }

    @Test void lecturerCannotDeleteAssignedTopic() {
        signIn(20L, RoleType.ROLE_LECTURER);
        topic.setIsAssigned(true);
        topic.setApprovalStatus(TopicApprovalStatus.PENDING);
        assertThatThrownBy(() -> service.deleteTopic(10L)).isInstanceOf(BadRequestException.class);
        verify(topics, never()).delete(any());
        verifyNoInteractions(grades, results, reviewers, councilTopics);
    }

    @Test void unrelatedLecturerCannotEditOrDeleteAnotherLecturersTopic() {
        signIn(30L, RoleType.ROLE_LECTURER);
        topic.setApprovalStatus(TopicApprovalStatus.PENDING);
        assertThatThrownBy(() -> service.updateTopic(10L, update())).isInstanceOf(ForbiddenException.class);
        assertThatThrownBy(() -> service.deleteTopic(10L)).isInstanceOf(ForbiddenException.class);
        verify(topics, never()).delete(any());
    }
}
