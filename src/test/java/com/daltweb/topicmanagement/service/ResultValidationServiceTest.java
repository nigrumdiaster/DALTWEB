package com.daltweb.topicmanagement.service;

import com.daltweb.topicmanagement.constant.*;
import com.daltweb.topicmanagement.entity.*;
import com.daltweb.topicmanagement.exception.BadRequestException;
import com.daltweb.topicmanagement.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResultValidationServiceTest {
    @Mock GradeRepository grades;
    @Mock ReviewerAssignmentRepository reviewers;
    @Mock CouncilTopicAssignmentRepository assignments;
    @Mock CouncilMemberRepository councilMembers;
    @Mock StudentGroupRepository groups;
    @Mock FinalResultRepository results;
    @InjectMocks ResultValidationService service;
    RegistrationPeriod period;
    Topic topic;
    User advisor;
    List<Grade> marks;

    @BeforeEach void setup() {
        period = RegistrationPeriod.builder().id(1L).type(PeriodType.COURSE_PROJECT).build();
        advisor = User.builder().id(2L).build();
        topic = Topic.builder().id(3L).period(period).primaryAdvisor(advisor).build();
        marks = new ArrayList<>();
    }

    private void advisorScore() {
        marks.add(Grade.builder().grader(advisor).graderRole(GraderRole.ADVISOR).score(8.0).build());
        when(grades.findByTopicId(3L)).thenReturn(marks);
    }

    @Test void requiresPrimaryAdvisorGrade() {
        assertThatThrownBy(() -> service.calculate(topic)).isInstanceOf(BadRequestException.class);
    }

    @Test void ignoresLegacyCoAdvisorGrades() {
        advisorScore();
        marks.add(Grade.builder().grader(User.builder().id(4L).build())
                .graderRole(GraderRole.CO_ADVISOR).score(2.0).build());
        assertThat(service.calculate(topic).advisor()).isEqualTo(8);
        assertThat(service.calculate(topic).total()).isEqualTo(8);
    }

    @ParameterizedTest @ValueSource(doubles = {-1, 11, Double.NaN, Double.POSITIVE_INFINITY})
    void rejectsInvalidScores(double score) {
        advisorScore();
        marks.get(0).setScore(score);
        assertThatThrownBy(() -> service.calculate(topic)).isInstanceOf(BadRequestException.class);
    }

    @Test void courseProjectCanFinalizeWithAdvisorOnly() {
        advisorScore();
        assertThat(service.calculate(topic).total()).isEqualTo(8);
    }

    @Test void specializedProjectRequiresReviewer() {
        advisorScore();
        period.setType(PeriodType.SPECIALIZED_PROJECT);
        assertThatThrownBy(() -> service.calculate(topic)).isInstanceOf(BadRequestException.class);
    }

    private void reviewerScore(Double score) {
        when(reviewers.findByTopicId(3L)).thenReturn(Optional.of(ReviewerAssignment.builder()
                .period(period).reviewer(User.builder().id(5L).build()).score(score).build()));
    }

    @Test void assignedReviewerMustHaveSubmittedScore() {
        advisorScore();
        reviewerScore(null);
        assertThatThrownBy(() -> service.calculate(topic)).isInstanceOf(BadRequestException.class);
    }

    @Test void graduationThesisRequiresCouncilAssignment() {
        advisorScore();
        reviewerScore(7.0);
        period.setType(PeriodType.GRADUATION_THESIS);
        assertThatThrownBy(() -> service.calculate(topic)).isInstanceOf(BadRequestException.class);
    }

    private void council(boolean complete) {
        DefenseCouncil council = DefenseCouncil.builder().id(6L).period(period).build();
        when(assignments.findByTopicId(3L)).thenReturn(Optional.of(CouncilTopicAssignment.builder().council(council).build()));
        List<CouncilMember> members = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            User lecturer = User.builder().id(10L + i).build();
            CouncilRole role = CouncilRole.values()[i];
            members.add(CouncilMember.builder().lecturer(lecturer).councilRole(role).build());
            if (complete || i < 2) {
                GraderRole graderRole = i == 0 ? GraderRole.COUNCIL_CHAIRMAN
                        : i == 1 ? GraderRole.COUNCIL_SECRETARY : GraderRole.COUNCIL_MEMBER;
                marks.add(Grade.builder().grader(lecturer).graderRole(graderRole).score(6.0 + i).build());
            }
        }
        when(councilMembers.findByCouncilId(6L)).thenReturn(members);
    }

    @Test void allCouncilMembersMustGradeBeforeFinalization() {
        advisorScore();
        council(false);
        assertThatThrownBy(() -> service.calculate(topic)).isInstanceOf(BadRequestException.class);
    }

    @Test void averagesEveryCouncilMemberAndThenAvailableRequiredComponents() {
        advisorScore();
        reviewerScore(9.0);
        council(true);
        period.setType(PeriodType.GRADUATION_THESIS);
        var scores = service.calculate(topic);
        assertThat(scores.council()).isEqualTo(7.0);
        assertThat(scores.total()).isEqualTo(8.0);
    }

    @Test void periodCannotCompleteWithAnUnfinalizedGroup() {
        StudentGroup group = StudentGroup.builder().id(9L).topic(topic).period(period).build();
        when(groups.findByPeriodId(1L)).thenReturn(List.of(group));
        assertThatThrownBy(() -> service.validatePeriodResults(period)).isInstanceOf(BadRequestException.class);
    }

    @Test void changedComponentScoreRequiresFinalizingAgain() {
        advisorScore();
        StudentGroup group = StudentGroup.builder().id(9L).topic(topic).period(period).build();
        when(groups.findByPeriodId(1L)).thenReturn(List.of(group));
        when(results.findByPeriodId(1L)).thenReturn(List.of(FinalResult.builder().topic(topic)
                .studentGroup(group).finalScore(7.0).advisorAvgScore(7.0).build()));
        assertThatThrownBy(() -> service.validatePeriodResults(period)).isInstanceOf(BadRequestException.class)
                .hasMessageContaining("chốt lại");
    }
}
