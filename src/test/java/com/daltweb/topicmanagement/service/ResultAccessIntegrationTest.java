package com.daltweb.topicmanagement.service;

import com.daltweb.topicmanagement.constant.*;
import com.daltweb.topicmanagement.dto.common.Pagination;
import com.daltweb.topicmanagement.entity.*;
import com.daltweb.topicmanagement.dto.council.FinalizeCouncilResultRequest;
import com.daltweb.topicmanagement.dto.grade.PublishResultRequest;
import com.daltweb.topicmanagement.dto.period.PeriodStatusRequest;
import com.daltweb.topicmanagement.event.ResultPublishedEvent;
import com.daltweb.topicmanagement.exception.ForbiddenException;
import com.daltweb.topicmanagement.repository.*;
import com.daltweb.topicmanagement.security.CustomUserDetails;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest(properties = {"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"})
@Import({GradeAndResultService.class, StudentGroupService.class, TopicService.class,
        RegistrationPeriodService.class, ResultValidationService.class, ReviewerService.class})
@RecordApplicationEvents
class ResultAccessIntegrationTest {
    private static final org.springframework.data.domain.Pageable PAGE = Pagination.request(0, 50);
    @Autowired GradeAndResultService service;
    @Autowired ReviewerService reviewerService;
    @Autowired UserRepository users;
    @Autowired DepartmentRepository departments;
    @Autowired RegistrationPeriodRepository periods;
    @Autowired TopicRepository topics;
    @Autowired StudentGroupRepository groups;
    @Autowired FinalResultRepository results;
    @Autowired GradeRepository grades;
    @Autowired RegistrationPeriodService periodService;
    @Autowired ApplicationEvents events;
    User student;
    User other;
    User lecturer;
    Department department;
    RegistrationPeriod ownPeriod;
    RegistrationPeriod otherPeriod;
    RegistrationPeriod unpublishedPeriod;
    RegistrationPeriod pendingPeriod;
    RegistrationPeriod formingPeriod;
    Topic ownTopic;
    Topic otherTopic;
    Topic unpublishedTopic;

    @BeforeEach void setup() {
        student = user("one", RoleType.ROLE_STUDENT);
        other = user("two", RoleType.ROLE_STUDENT);
        lecturer = user("lecturer", RoleType.ROLE_LECTURER);
        department = departments.save(Department.builder().code("TEST").name("Bộ môn").build());
        ownPeriod = period("Đợt của tôi");
        ownTopic = registration(ownPeriod, student, "own", true);
        otherTopic = registration(ownPeriod, other, "other", true);
        otherPeriod = period("Đợt khác");
        registration(otherPeriod, other, "foreign", true);
        unpublishedPeriod = period("Chưa công bố");
        unpublishedTopic = registration(unpublishedPeriod, student, "unpublished", false);
        pendingPeriod = period("Chưa có kết quả");
        registration(pendingPeriod, student, "pending", null);
        formingPeriod = period("Mới lập nhóm");
        StudentGroup forming = StudentGroup.builder().name("Nhóm đang lập").period(formingPeriod).build();
        forming.getMembers().add(GroupMember.builder().group(forming).student(student).groupRole(GroupRole.LEADER).build());
        groups.saveAndFlush(forming);
        CustomUserDetails principal = CustomUserDetails.build(student);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    @AfterEach void clear() { SecurityContextHolder.clearContext(); }

    private User user(String name, RoleType role) {
        return users.save(User.builder().username(name).password("unused").fullName(name)
                .email(name + "@example.com").role(role).build());
    }

    private RegistrationPeriod period(String name) {
        LocalDateTime now = LocalDateTime.now();
        return periods.save(RegistrationPeriod.builder().name(name).type(PeriodType.COURSE_PROJECT)
                .lecturerRegStart(now.minusDays(4)).lecturerRegEnd(now.minusDays(3))
                .studentRegStart(now.minusDays(2)).studentRegEnd(now.minusDays(1)).build());
    }

    private Topic registration(RegistrationPeriod period, User owner, String code, Boolean published) {
        Topic topic = topics.save(Topic.builder().code(code).titleVi(code).description("Test")
                .department(department).period(period).primaryAdvisor(lecturer).build());
        StudentGroup group = StudentGroup.builder().name(code).period(period).topic(topic).build();
        group.getMembers().add(GroupMember.builder().group(group).student(owner).groupRole(GroupRole.LEADER).build());
        groups.saveAndFlush(group);
        if (published != null) {
            results.saveAndFlush(FinalResult.builder().period(period).topic(topic).studentGroup(group)
                    .finalScore(8.0).isPublished(published).build());
        }
        return topic;
    }

    @Test void realQueriesOnlyReturnOwnPublishedResults() {
        assertThat(service.getMyStudentResults(null, PAGE)).extracting(r -> r.getTopicId()).containsExactly(ownTopic.getId());
        assertThat(service.getResultsByPeriod(ownPeriod.getId(), PAGE)).extracting(r -> r.getTopicId()).containsExactly(ownTopic.getId());
        assertThat(service.getMyStudentResults(unpublishedPeriod.getId(), PAGE)).isEmpty();
        assertThat(service.getMyStudentResults(pendingPeriod.getId(), PAGE)).isEmpty();
    }

    @Test void selectorIncludesRegisteredPeriodsEvenBeforeResultsExist() {
        assertThat(service.getMyResultPeriods(PAGE)).extracting(p -> p.getId())
                .containsExactlyInAnyOrder(ownPeriod.getId(), unpublishedPeriod.getId(), pendingPeriod.getId());
    }

    @Test void unpagedRequestsStillEnforceStudentScope() {
        var all = Pagination.request(0, -1);
        assertThat(service.getResultsByPeriod(ownPeriod.getId(), all)).extracting(r -> r.getTopicId())
                .containsExactly(ownTopic.getId());
        assertThat(service.getMyStudentResults(null, all)).extracting(r -> r.getTopicId())
                .containsExactly(ownTopic.getId());
        assertThatThrownBy(() -> service.getResultsByPeriod(otherPeriod.getId(), all))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test void directCallsCannotRevealForeignOrUnpublishedGrades() {
        assertThatThrownBy(() -> service.getResultsByPeriod(otherPeriod.getId(), PAGE)).isInstanceOf(ForbiddenException.class);
        assertThatThrownBy(() -> service.getTopicResult(otherTopic.getId())).isInstanceOf(ForbiddenException.class);
        assertThatThrownBy(() -> service.getTopicGrades(otherTopic.getId())).isInstanceOf(ForbiddenException.class);
        assertThatThrownBy(() -> service.getTopicResult(unpublishedTopic.getId())).isInstanceOf(ForbiddenException.class);
        assertThatThrownBy(() -> reviewerService.getAssignmentsByPeriod(otherPeriod.getId(), null, PAGE)).isInstanceOf(ForbiddenException.class);
    }

    @Test void completeWorkflowFinalizesEveryGroupAndPublishesEachResultOnce() {
        User dean = user("dean", RoleType.ROLE_DEAN);
        CustomUserDetails principal = CustomUserDetails.build(dean);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
        ownPeriod.setStatus(PeriodStatus.REVIEWING);
        for (Topic topic : java.util.List.of(ownTopic, otherTopic)) {
            StudentGroup group = groups.findByTopicId(topic.getId()).orElseThrow();
            FinalResult old = results.findByTopicId(topic.getId()).orElseThrow();
            old.setIsPublished(false);
            grades.saveAndFlush(Grade.builder().period(ownPeriod).topic(topic).studentGroup(group)
                    .grader(lecturer).graderRole(GraderRole.ADVISOR).score(8.0).build());
            FinalizeCouncilResultRequest request = new FinalizeCouncilResultRequest();
            request.setTopicId(topic.getId());
            assertThat(service.finalizeTopicResult(request).getFinalScore()).isEqualTo(8.0);
        }
        periodService.updatePeriodStatus(ownPeriod.getId(), new PeriodStatusRequest(PeriodStatus.COMPLETED));
        service.publishResults(new PublishResultRequest(ownPeriod.getId(), true));
        service.publishResults(new PublishResultRequest(ownPeriod.getId(), true));
        assertThat(results.findByPeriodId(ownPeriod.getId())).allSatisfy(r -> assertThat(r.getIsPublished()).isTrue());
        assertThat(events.stream(ResultPublishedEvent.class).count()).isEqualTo(2);
    }
}
