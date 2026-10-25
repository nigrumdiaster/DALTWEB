package com.daltweb.topicmanagement.service;

import com.daltweb.topicmanagement.constant.*;
import com.daltweb.topicmanagement.dto.council.*;
import com.daltweb.topicmanagement.entity.*;
import com.daltweb.topicmanagement.exception.BadRequestException;
import com.daltweb.topicmanagement.exception.ResourceNotFoundException;
import com.daltweb.topicmanagement.repository.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest(showSql = false, properties = {"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect", "spring.jpa.hibernate.ddl-auto=create-drop"})
@Import(DefenseCouncilService.class)
class DefenseCouncilUpdateIntegrationTest {
    @Autowired DefenseCouncilService service;
    @Autowired DefenseCouncilRepository councils;
    @Autowired CouncilMemberRepository members;
    @Autowired UserRepository users;
    @Autowired RegistrationPeriodRepository periods;
    @Autowired TopicRepository topics;
    @Autowired DepartmentRepository departments;
    @Autowired CouncilTopicAssignmentRepository assignments;
    @Autowired StudentGroupRepository groups;
    @Autowired GradeRepository grades;
    @Autowired EntityManager em;
    DefenseCouncil council;
    User chair, secretary, member, replacement;
    RegistrationPeriod period;

    @BeforeEach void setup() {
        chair = lecturer("chair");
        secretary = lecturer("secretary");
        member = lecturer("member");
        replacement = lecturer("replacement");
        var now = LocalDateTime.now();
        period = periods.save(RegistrationPeriod.builder().name("Period")
                .type(PeriodType.GRADUATION_THESIS).status(PeriodStatus.DEFENSE)
                .lecturerRegStart(now.minusDays(10)).lecturerRegEnd(now.minusDays(9))
                .studentRegStart(now.minusDays(8)).studentRegEnd(now.minusDays(7)).build());
        CouncilCreateRequest create = new CouncilCreateRequest();
        create.setName("Original");
        create.setPeriodId(period.getId());
        create.setMembers(List.of(input(chair, CouncilRole.CHAIRMAN), input(secretary, CouncilRole.SECRETARY), input(member, CouncilRole.MEMBER)));
        Long id = service.createCouncil(create).getId();
        em.flush();
        em.clear();
        council = councils.findById(id).orElseThrow();
    }

    User lecturer(String name) {
        return users.save(User.builder().username(name).password("unused").fullName(name)
                .email(name + "@example.com").role(RoleType.ROLE_LECTURER).active(true).build());
    }

    CouncilMemberInput input(User user, CouncilRole role) {
        return new CouncilMemberInput(user.getId(), role);
    }

    CouncilCreateRequest update() {
        CouncilCreateRequest request = new CouncilCreateRequest();
        request.setName("Updated");
        request.setPeriodId(period.getId());
        request.setDefenseDate(LocalDate.of(2026, 11, 10));
        request.setLocation("Room 101");
        request.setNotes("Updated notes");
        request.setMembers(new ArrayList<>(List.of(input(chair, CouncilRole.CHAIRMAN),
                input(secretary, CouncilRole.SECRETARY), input(member, CouncilRole.MEMBER))));
        return request;
    }

    Topic assignedTopic(User advisor) {
        Department department = departments.save(Department.builder().code("IT").name("IT").build());
        Topic topic = topics.save(Topic.builder().code("TOPIC").titleVi("Topic").description("Description")
                .period(period).department(department).primaryAdvisor(advisor)
                .approvalStatus(TopicApprovalStatus.APPROVED).build());
        var assignment = assignments.save(CouncilTopicAssignment.builder().council(council).topic(topic).build());
        council.getAssignedTopics().add(assignment);
        return topic;
    }

    @Test void savesDetailsAndMembershipWithoutLosingAssignmentsOrExistingMemberIds() {
        assignedTopic(lecturer("advisor"));
        Long chairMemberId = council.getMembers().stream().filter(m -> m.getLecturer().getId().equals(chair.getId())).findFirst().orElseThrow().getId();
        String code = council.getCode();
        var request = update();
        request.setMembers(List.of(input(chair, CouncilRole.SECRETARY), input(secretary, CouncilRole.CHAIRMAN), input(replacement, CouncilRole.MEMBER)));
        service.updateCouncil(council.getId(), request);
        em.flush();
        em.clear();
        var result = service.getCouncilById(council.getId());
        assertThat(result.getName()).isEqualTo("Updated");
        assertThat(result.getCode()).isEqualTo(code);
        assertThat(result.getDefenseDate()).isEqualTo(request.getDefenseDate());
        assertThat(result.getLocation()).isEqualTo("Room 101");
        assertThat(result.getNotes()).isEqualTo("Updated notes");
        assertThat(result.getAssignedTopics()).hasSize(1);
        assertThat(result.getMembers()).extracting(CouncilMemberDto::getLecturerId)
                .containsExactlyInAnyOrder(chair.getId(), secretary.getId(), replacement.getId());
        assertThat(result.getMembers().stream().filter(m -> m.getId().equals(chairMemberId)).findFirst().orElseThrow().getCouncilRole())
                .isEqualTo(CouncilRole.SECRETARY);
        assertThat(members.findAll()).hasSize(3);
    }

    @Test void rejectsDuplicateLecturers() {
        var request = update();
        request.getMembers().set(2, input(chair, CouncilRole.MEMBER));
        assertThatThrownBy(() -> service.updateCouncil(council.getId(), request)).isInstanceOf(BadRequestException.class);
        assertThat(council.getName()).isEqualTo("Original");
    }

    @Test void rejectsInvalidRolesAndSize() {
        var request = update();
        request.getMembers().set(0, input(chair, CouncilRole.MEMBER));
        assertThatThrownBy(() -> service.updateCouncil(council.getId(), request)).isInstanceOf(BadRequestException.class);
        request.setMembers(List.of(input(chair, CouncilRole.CHAIRMAN), input(secretary, CouncilRole.SECRETARY)));
        assertThatThrownBy(() -> service.updateCouncil(council.getId(), request)).isInstanceOf(BadRequestException.class);
    }

    @Test void rejectsAdvisorAndCoAdvisorConflicts() {
        Topic topic = assignedTopic(replacement);
        var request = update();
        request.getMembers().set(2, input(replacement, CouncilRole.MEMBER));
        assertThatThrownBy(() -> service.updateCouncil(council.getId(), request)).isInstanceOf(BadRequestException.class);
        topic.setPrimaryAdvisor(lecturer("advisor"));
        topic.setCoAdvisor(replacement);
        assertThatThrownBy(() -> service.updateCouncil(council.getId(), request)).isInstanceOf(BadRequestException.class);
    }

    @Test void gradedCouncilRejectsMembershipChange() {
        Topic topic = assignedTopic(lecturer("advisor"));
        var group = groups.save(StudentGroup.builder().name("Group").topic(topic).period(period).status(GroupStatus.COMPLETED).build());
        grades.save(Grade.builder().period(period).topic(topic).studentGroup(group).grader(chair)
                .graderRole(GraderRole.COUNCIL_CHAIRMAN).score(8.0).build());
        var request = update();
        request.getMembers().set(2, input(replacement, CouncilRole.MEMBER));
        assertThatThrownBy(() -> service.updateCouncil(council.getId(), request)).isInstanceOf(BadRequestException.class);
        em.flush();
        assertThat(grades.findByTopicId(topic.getId())).hasSize(1);
    }

    @Test void rejectsPeriodChangeAndMissingCouncil() {
        var request = update();
        request.setPeriodId(-1L);
        assertThatThrownBy(() -> service.updateCouncil(council.getId(), request)).isInstanceOf(BadRequestException.class);
        assertThatThrownBy(() -> service.updateCouncil(-1L, update())).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test void deletesEmptyCouncilAndMembership() {
        Long id = council.getId();
        service.deleteCouncil(id);
        em.flush();
        em.clear();
        assertThat(councils.findById(id)).isEmpty();
        assertThat(members.findAll()).isEmpty();
        assertThat(users.findById(chair.getId())).isPresent();
    }

    @Test void deletionUnassignsTopicsAndPreservesGroupsAdvisorGradesAndOtherCouncils() {
        User advisor = lecturer("advisor");
        Topic topic = assignedTopic(advisor);
        var group = groups.save(StudentGroup.builder().name("Group").topic(topic).period(period).status(GroupStatus.COMPLETED).build());
        var grade = grades.save(Grade.builder().period(period).topic(topic).studentGroup(group).grader(advisor)
                .graderRole(GraderRole.ADVISOR).score(8.0).build());
        var other = service.createCouncil(update());
        service.deleteCouncil(council.getId());
        em.flush();
        em.clear();
        assertThat(councils.findById(council.getId())).isEmpty();
        assertThat(assignments.findByTopicId(topic.getId())).isEmpty();
        assertThat(topics.findById(topic.getId())).isPresent();
        assertThat(groups.findById(group.getId()).orElseThrow().getTopic().getId()).isEqualTo(topic.getId());
        assertThat(grades.findById(grade.getId()).orElseThrow().getScore()).isEqualTo(8.0);
        assertThat(service.getCouncilById(other.getId()).getMembers()).hasSize(3);
        var assign = new AssignTopicsToCouncilRequest();
        assign.setTopicIds(List.of(topic.getId()));
        service.assignTopicsToCouncil(other.getId(), assign);
        em.flush();
        assertThat(assignments.findByTopicId(topic.getId()).orElseThrow().getCouncil().getId()).isEqualTo(other.getId());
    }

    @ParameterizedTest
    @EnumSource(value = GraderRole.class, names = {"COUNCIL_CHAIRMAN", "COUNCIL_SECRETARY", "COUNCIL_MEMBER"})
    void deletingGradedCouncilIsRejected(GraderRole role) {
        Topic topic = assignedTopic(lecturer("advisor"));
        var group = groups.save(StudentGroup.builder().name("Group").topic(topic).period(period).status(GroupStatus.COMPLETED).build());
        grades.save(Grade.builder().period(period).topic(topic).studentGroup(group).grader(chair)
                .graderRole(role).score(8.0).build());
        assertThatThrownBy(() -> service.deleteCouncil(council.getId())).isInstanceOf(BadRequestException.class);
        em.flush(); em.clear();
        assertThat(councils.findById(council.getId())).isPresent();
        assertThat(assignments.findByTopicId(topic.getId())).isPresent();
        assertThat(topics.findById(topic.getId())).isPresent();
        assertThat(grades.findByTopicId(topic.getId())).hasSize(1);
    }

    @Test void deletingMissingCouncilReturnsNotFound() {
        assertThatThrownBy(() -> service.deleteCouncil(-1L)).isInstanceOf(ResourceNotFoundException.class);
    }

}
