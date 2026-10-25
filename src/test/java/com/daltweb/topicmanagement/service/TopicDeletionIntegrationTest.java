package com.daltweb.topicmanagement.service;

import com.daltweb.topicmanagement.constant.*;
import com.daltweb.topicmanagement.entity.*;
import com.daltweb.topicmanagement.exception.BadRequestException;
import com.daltweb.topicmanagement.repository.*;
import com.daltweb.topicmanagement.security.CustomUserDetails;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest(showSql = false, properties = {"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect", "spring.jpa.hibernate.ddl-auto=create-drop"})
@Import(TopicService.class)
class TopicDeletionIntegrationTest {
    @Autowired TopicService service;
    @Autowired TopicRepository topics;
    @Autowired StudentGroupRepository groups;
    @Autowired UserRepository users;
    @Autowired DepartmentRepository departments;
    @Autowired RegistrationPeriodRepository periods;
    @Autowired GradeRepository grades;
    @Autowired FinalResultRepository results;
    @Autowired ReviewerAssignmentRepository reviewers;
    @Autowired CouncilTopicAssignmentRepository assignments;
    @Autowired DefenseCouncilRepository councils;
    @Autowired EntityManager em;
    User lecturer;
    Topic target, other;
    StudentGroup targetGroup;
    DefenseCouncil council;

    @BeforeEach void setup() {
        lecturer = user("lecturer", RoleType.ROLE_LECTURER);
        User student = user("student", RoleType.ROLE_STUDENT);
        Department department = departments.save(Department.builder().code("DEL").name("Department").build());
        var now = LocalDateTime.now();
        RegistrationPeriod period = periods.save(RegistrationPeriod.builder().name("Period")
                .type(PeriodType.GRADUATION_THESIS).status(PeriodStatus.COMPLETED)
                .lecturerRegStart(now.minusDays(10)).lecturerRegEnd(now.minusDays(9))
                .studentRegStart(now.minusDays(8)).studentRegEnd(now.minusDays(7)).build());
        council = councils.save(DefenseCouncil.builder().name("Council").period(period).build());
        target = topic("TARGET", period, department);
        other = topic("OTHER", period, department);
        targetGroup = attachDependencies(target, student, period);
        attachDependencies(other, student, period);
        em.flush();
    }

    User user(String username, RoleType role) {
        return users.save(User.builder().username(username).password("unused").fullName(username)
                .email(username + "@example.com").role(role).active(true).build());
    }

    void signIn(User user) {
        CustomUserDetails principal = CustomUserDetails.build(user);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    Topic topic(String code, RegistrationPeriod period, Department department) {
        return topics.save(Topic.builder().code(code).titleVi(code).description("Description")
                .period(period).department(department).primaryAdvisor(lecturer)
                .approvalStatus(TopicApprovalStatus.APPROVED).isAssigned(true).build());
    }

    StudentGroup attachDependencies(Topic topic, User student, RegistrationPeriod period) {
        StudentGroup group = StudentGroup.builder().name(topic.getCode()).topic(topic).period(period)
                .status(GroupStatus.COMPLETED).reportUrl("https://example.com/report").reportNote("Report")
                .submittedAt(LocalDateTime.now()).build();
        group.getMembers().add(GroupMember.builder().group(group).student(student).groupRole(GroupRole.LEADER).build());
        groups.save(group);
        grades.save(Grade.builder().period(period).topic(topic).studentGroup(group).grader(lecturer)
                .graderRole(GraderRole.ADVISOR).score(8.0).build());
        results.save(FinalResult.builder().period(period).topic(topic).studentGroup(group).finalScore(8.0).isPublished(true).build());
        reviewers.save(ReviewerAssignment.builder().period(period).topic(topic).studentGroup(group).reviewer(lecturer).build());
        assignments.save(CouncilTopicAssignment.builder().council(council).topic(topic).studentGroup(group).build());
        return group;
    }

    @AfterEach void clearSecurity() { SecurityContextHolder.clearContext(); }

    @ParameterizedTest
    @EnumSource(value = RoleType.class, names = {"ROLE_ADMIN", "ROLE_DEAN"})
    void deletesOnlyTargetDependenciesAndPreservesGroupsAndCouncil(RoleType role) {
        signIn(user("administrator", role));
        Long topicId = target.getId(), groupId = targetGroup.getId(), otherId = other.getId(), councilId = council.getId();
        service.deleteTopic(topicId);
        em.flush();
        em.clear();
        assertThat(topics.findById(topicId)).isEmpty();
        StudentGroup preserved = groups.findById(groupId).orElseThrow();
        assertThat(preserved.getTopic()).isNull();
        assertThat(preserved.getStatus()).isEqualTo(GroupStatus.FORMING);
        assertThat(preserved.getMembers()).hasSize(1);
        assertThat(preserved.getReportUrl()).isNull();
        assertThat(preserved.getSubmittedAt()).isNull();
        assertThat(groups.count()).isEqualTo(2);
        assertThat(councils.findById(councilId)).isPresent();
        assertThat(grades.findByTopicId(topicId)).isEmpty();
        assertThat(results.findByTopicId(topicId)).isEmpty();
        assertThat(reviewers.findByTopicId(topicId)).isEmpty();
        assertThat(assignments.findByTopicId(topicId)).isEmpty();
        assertThat(topics.findById(otherId)).isPresent();
        assertThat(grades.findByTopicId(otherId)).hasSize(1);
        assertThat(results.findByTopicId(otherId)).isPresent();
        assertThat(reviewers.findByTopicId(otherId)).isPresent();
        assertThat(assignments.findByTopicId(otherId)).isPresent();
    }

    @Test void lecturerCannotDeleteApprovedTopicOrItsDependentRecords() {
        signIn(lecturer);
        assertThatThrownBy(() -> service.deleteTopic(target.getId())).isInstanceOf(BadRequestException.class);
        em.flush();
        em.clear();
        assertThat(topics.findById(target.getId())).isPresent();
        assertThat(groups.findById(targetGroup.getId()).orElseThrow().getTopic()).isNotNull();
        assertThat(grades.findByTopicId(target.getId())).hasSize(1);
        assertThat(results.findByTopicId(target.getId())).isPresent();
    }
}
