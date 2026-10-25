package com.daltweb.topicmanagement.service;

import com.daltweb.topicmanagement.constant.*;
import com.daltweb.topicmanagement.dto.group.*;
import com.daltweb.topicmanagement.entity.*;
import com.daltweb.topicmanagement.exception.BadRequestException;
import com.daltweb.topicmanagement.repository.*;
import com.daltweb.topicmanagement.security.CustomUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentGroupServiceTest {
    @Mock StudentGroupRepository groups;
    @Mock GroupMemberRepository members;
    @Mock RegistrationPeriodRepository periods;
    @Mock TopicRepository topics;
    @Mock UserRepository users;
    @Mock TopicService topicService;
    @InjectMocks StudentGroupService service;

    private StudentGroup group;
    private RegistrationPeriod period;
    private User leader;

    @BeforeEach
    void setUp() {
        leader = User.builder().id(1L).username("leader").role(RoleType.ROLE_STUDENT).active(true).build();
        CustomUserDetails principal = CustomUserDetails.build(leader);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
        period = RegistrationPeriod.builder().id(10L)
                .status(PeriodStatus.PHASE2_STUDENT_REGISTRATION)
                .studentRegStart(LocalDateTime.now().minusDays(1))
                .studentRegEnd(LocalDateTime.now().plusDays(1)).build();
        group = StudentGroup.builder().id(20L).period(period).status(GroupStatus.FORMING)
                .members(new ArrayList<>()).build();
    }

    @AfterEach
    void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    private void withMembers(int count) {
        for (int i = 0; i < count; i++) {
            User student = i == 0 ? leader : User.builder().id((long) i + 1).build();
            group.getMembers().add(GroupMember.builder().group(group).student(student)
                    .groupRole(i == 0 ? GroupRole.LEADER : GroupRole.MEMBER).build());
        }
        when(groups.findById(group.getId())).thenReturn(Optional.of(group));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 6})
    void rejectsRegistrationOutsideThreeToFiveMembers(int count) {
        withMembers(count);
        RegisterTopicRequest request = new RegisterTopicRequest();
        request.setTopicId(30L);

        assertThatThrownBy(() -> service.registerTopic(group.getId(), request))
                .isInstanceOf(BadRequestException.class).hasMessageContaining("3 đến 5");
        verifyNoInteractions(topics);
        verify(groups, never()).save(any());
        assertThat(group.getTopic()).isNull();
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 4, 5})
    void permitsRegistrationWithThreeToFiveMembers(int count) {
        withMembers(count);
        Topic topic = Topic.builder().id(30L).period(period)
                .approvalStatus(TopicApprovalStatus.APPROVED).isAssigned(false).build();
        when(topics.findById(topic.getId())).thenReturn(Optional.of(topic));
        when(groups.save(group)).thenReturn(group);
        RegisterTopicRequest request = new RegisterTopicRequest();
        request.setTopicId(topic.getId());

        service.registerTopic(group.getId(), request);

        assertThat(group.getTopic()).isSameAs(topic);
        assertThat(group.getStatus()).isEqualTo(GroupStatus.REGISTERED);
        verify(topics).save(topic);
        verify(groups).save(group);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 6})
    void rejectsReportOutsideThreeToFiveMembers(int count) {
        withMembers(count);
        group.setTopic(Topic.builder().id(30L).build());
        SubmitReportRequest request = new SubmitReportRequest();
        request.setReportUrl("https://example.com/report");

        assertThatThrownBy(() -> service.submitReport(group.getId(), request))
                .isInstanceOf(BadRequestException.class).hasMessageContaining("3 đến 5");
        verify(groups, never()).save(any());
        assertThat(group.getReportUrl()).isNull();
        assertThat(group.getSubmittedAt()).isNull();
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 4, 5})
    void permitsReportWithThreeToFiveMembers(int count) {
        withMembers(count);
        group.setTopic(Topic.builder().id(30L).build());
        when(groups.save(group)).thenReturn(group);
        SubmitReportRequest request = new SubmitReportRequest();
        request.setReportUrl("https://example.com/report");

        service.submitReport(group.getId(), request);

        assertThat(group.getReportUrl()).isEqualTo(request.getReportUrl());
        assertThat(group.getStatus()).isEqualTo(GroupStatus.SUBMITTED);
        assertThat(group.getSubmittedAt()).isNotNull();
        verify(groups).save(group);
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 4})
    void permitsAddingFourthAndFifthMembers(int count) {
        withMembers(count);
        User student = User.builder().id(99L).build();
        when(users.findById(99L)).thenReturn(Optional.of(student));
        AddMemberRequest request = new AddMemberRequest();
        request.setStudentId(99L);

        service.addMember(group.getId(), request);

        assertThat(group.getMembers()).hasSize(count + 1);
        verify(members).save(any(GroupMember.class));
    }

    @Test
    void rejectsSixthMember() {
        withMembers(5);
        AddMemberRequest request = new AddMemberRequest();
        request.setStudentId(99L);

        assertThatThrownBy(() -> service.addMember(group.getId(), request))
                .isInstanceOf(BadRequestException.class).hasMessageContaining("5 sinh viên");
        verifyNoInteractions(members, users);
    }

    @Test
    void cannotReduceRegisteredGroupBelowThreeMembers() {
        withMembers(3);
        group.setTopic(Topic.builder().id(30L).build());
        GroupMember member = group.getMembers().get(1);
        when(members.findByGroupIdAndStudentId(group.getId(), member.getStudent().getId()))
                .thenReturn(Optional.of(member));

        assertThatThrownBy(() -> service.removeMember(group.getId(), member.getStudent().getId()))
                .isInstanceOf(BadRequestException.class).hasMessageContaining("ít nhất 3");
        assertThat(group.getMembers()).hasSize(3);
        verify(members, never()).delete(any());
    }
}
