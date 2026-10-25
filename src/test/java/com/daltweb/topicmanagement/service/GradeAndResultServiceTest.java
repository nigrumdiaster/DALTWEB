package com.daltweb.topicmanagement.service;

import com.daltweb.topicmanagement.constant.*;
import com.daltweb.topicmanagement.dto.common.Pagination;
import com.daltweb.topicmanagement.dto.council.FinalizeCouncilResultRequest;
import com.daltweb.topicmanagement.dto.grade.PublishResultRequest;
import com.daltweb.topicmanagement.entity.*;
import com.daltweb.topicmanagement.event.ResultPublishedEvent;
import com.daltweb.topicmanagement.exception.*;
import com.daltweb.topicmanagement.repository.*;
import com.daltweb.topicmanagement.security.CustomUserDetails;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GradeAndResultServiceTest {
    private static final org.springframework.data.domain.Pageable PAGE = Pagination.request(0, 50);
    @Mock GradeRepository grades;
    @Mock FinalResultRepository results;
    @Mock TopicRepository topics;
    @Mock StudentGroupRepository groups;
    @Mock RegistrationPeriodRepository periods;
    @Mock ReviewerAssignmentRepository reviewers;
    @Mock CouncilMemberRepository councilMembers;
    @Mock CouncilTopicAssignmentRepository assignments;
    @Mock UserRepository users;
    @Mock TopicService topicService;
    @Mock StudentGroupService groupService;
    @Mock GroupMemberRepository members;
    @Mock RegistrationPeriodService periodService;
    @Mock ResultValidationService validation;
    @Mock ApplicationEventPublisher events;
    @InjectMocks GradeAndResultService service;

    RegistrationPeriod period;
    StudentGroup group;
    Topic topic;
    FinalResult result;

    @BeforeEach
    void setup() {
        authenticate(RoleType.ROLE_STUDENT);
        period = RegistrationPeriod.builder().id(10L).name("Đợt 1").type(PeriodType.COURSE_PROJECT)
                .status(PeriodStatus.COMPLETED).build();
        topic = Topic.builder().id(20L).titleVi("Đề tài").period(period).build();
        group = StudentGroup.builder().id(30L).period(period).topic(topic).build();
        for (long id = 1; id <= 3; id++) {
            group.getMembers().add(GroupMember.builder().group(group)
                    .student(User.builder().id(id).email("student" + id + "@example.com").build()).build());
        }
        result = FinalResult.builder().id(40L).period(period).topic(topic).studentGroup(group)
                .finalScore(8.0).letterGrade("B+").isPublished(true).build();
    }

    private void authenticate(RoleType role) {
        CustomUserDetails principal = CustomUserDetails.build(User.builder().id(1L).username("user")
                .role(role).active(true).build());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    @AfterEach void clear() { SecurityContextHolder.clearContext(); }

    @Test void onlyTheAssignedAdvisorCanSubmitAdvisorGrades() {
        authenticate(RoleType.ROLE_LECTURER);
        period.setStatus(PeriodStatus.IN_PROGRESS);
        topic.setPrimaryAdvisor(User.builder().id(2L).build());
        when(users.findById(1L)).thenReturn(Optional.of(User.builder().id(1L).build()));
        when(topics.findById(20L)).thenReturn(Optional.of(topic));
        var request = new com.daltweb.topicmanagement.dto.grade.AdvisorGradeRequest();
        request.setTopicId(20L);
        assertThatThrownBy(() -> service.gradeAsAdvisor(request)).isInstanceOf(ForbiddenException.class);
        verifyNoInteractions(grades);
    }

    @Test void studentCannotQueryUnregisteredPeriodThroughEitherRoute() {
        assertThatThrownBy(() -> service.getResultsByPeriod(99L, PAGE)).isInstanceOf(ForbiddenException.class);
        assertThatThrownBy(() -> service.getMyStudentResults(99L, PAGE)).isInstanceOf(ForbiddenException.class);
        verifyNoInteractions(results);
    }

    @Test void studentCannotQueryPeriodWithOnlyAnUnregisteredGroup() {
        group.setTopic(null);
        when(members.findByStudentIdAndPeriodId(1L, 10L)).thenReturn(Optional.of(group.getMembers().get(0)));
        assertThatThrownBy(() -> service.getResultsByPeriod(10L, PAGE)).isInstanceOf(ForbiddenException.class);
        verifyNoInteractions(results);
    }

    @Test void registeredPeriodQueryOnlyUsesOwnPublishedResults() {
        when(members.findByStudentIdAndPeriodId(1L, 10L)).thenReturn(Optional.of(group.getMembers().get(0)));
        when(results.findPublishedResultsByStudentIdAndPeriodId(1L, 10L, PAGE)).thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(result)));
        assertThat(service.getResultsByPeriod(10L, PAGE)).hasSize(1);
        verify(results, never()).findByPeriodId(any());
    }

    @Test void studentCannotReadOtherGroupsEvenWithinTheirPeriod() {
        group.getMembers().remove(0);
        when(groups.findByTopicId(20L)).thenReturn(Optional.of(group));
        assertThatThrownBy(() -> service.getTopicResult(20L)).isInstanceOf(ForbiddenException.class);
        assertThatThrownBy(() -> service.getTopicGrades(20L)).isInstanceOf(ForbiddenException.class);
        verifyNoInteractions(grades, results);
    }

    @Test void studentCannotReadOwnUnpublishedResultsOrComponentGrades() {
        result.setIsPublished(false);
        when(groups.findByTopicId(20L)).thenReturn(Optional.of(group));
        when(results.findByTopicId(20L)).thenReturn(Optional.of(result));
        assertThatThrownBy(() -> service.getTopicResult(20L)).isInstanceOf(ForbiddenException.class);
        assertThatThrownBy(() -> service.getTopicGrades(20L)).isInstanceOf(ForbiddenException.class);
        verifyNoInteractions(grades);
    }

    @Test void studentCanReadOwnPublishedResultsAndGrades() {
        when(groups.findByTopicId(20L)).thenReturn(Optional.of(group));
        when(results.findByTopicId(20L)).thenReturn(Optional.of(result));
        assertThat(service.getTopicResult(20L).getTopicId()).isEqualTo(20L);
        assertThat(service.getTopicGrades(20L)).isEmpty();
    }

    @Test void staffRetainsPeriodWideAccess() {
        authenticate(RoleType.ROLE_DEAN);
        when(results.findByPeriodId(10L, PAGE)).thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(result)));
        assertThat(service.getResultsByPeriod(10L, PAGE)).hasSize(1);
        verifyNoInteractions(members);
    }

    @Test void publishesOneSnapshotForEveryMemberAndDoesNotResendOnRepeatedPublish() {
        authenticate(RoleType.ROLE_DEAN);
        result.setIsPublished(false);
        when(periods.findForUpdate(10L)).thenReturn(Optional.of(period));
        when(validation.validatePeriodResults(period)).thenReturn(List.of(result));
        service.publishResults(new PublishResultRequest(10L, true));
        var publishedAt = result.getPublishedAt();
        service.publishResults(new PublishResultRequest(10L, true));
        ArgumentCaptor<ResultPublishedEvent> event = ArgumentCaptor.forClass(ResultPublishedEvent.class);
        verify(events).publishEvent(event.capture());
        assertThat(event.getValue().recipientEmails()).containsExactly(
                "student1@example.com", "student2@example.com", "student3@example.com");
        assertThat(event.getValue().finalScore()).isEqualTo(8.0);
        assertThat(result.getPublishedAt()).isEqualTo(publishedAt).isNotNull();
    }

    @Test void unpublishingDoesNotSendEmail() {
        when(periods.findForUpdate(10L)).thenReturn(Optional.of(period));
        when(results.findByPeriodId(10L)).thenReturn(List.of(result));
        service.publishResults(new PublishResultRequest(10L, false));
        assertThat(result.getIsPublished()).isFalse();
        verifyNoInteractions(events, validation);
    }

    @Test void cannotPublishBeforeCompletion() {
        period.setStatus(PeriodStatus.REVIEWING);
        when(periods.findForUpdate(10L)).thenReturn(Optional.of(period));
        assertThatThrownBy(() -> service.publishResults(new PublishResultRequest(10L, true)))
                .isInstanceOf(BadRequestException.class);
        verifyNoInteractions(events, results);
    }

    @Test void cannotFinalizeBeforeAssessmentStage() {
        authenticate(RoleType.ROLE_DEAN);
        when(topics.findById(20L)).thenReturn(Optional.of(topic));
        when(groups.findByTopicId(20L)).thenReturn(Optional.of(group));
        FinalizeCouncilResultRequest request = new FinalizeCouncilResultRequest();
        request.setTopicId(20L);
        assertThatThrownBy(() -> service.finalizeTopicResult(request)).isInstanceOf(BadRequestException.class);
        verifyNoInteractions(validation);
    }
}
