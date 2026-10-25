package com.daltweb.topicmanagement.repository;

import com.daltweb.topicmanagement.constant.*;
import com.daltweb.topicmanagement.dto.common.Pagination;
import com.daltweb.topicmanagement.entity.*;
import com.daltweb.topicmanagement.service.*;
import jakarta.persistence.EntityManager;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest(properties = {"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop", "spring.jpa.properties.hibernate.generate_statistics=true"})
@Import({TopicService.class, RegistrationPeriodService.class, ResultValidationService.class})
class PagedQueryIntegrationTest {
    @Autowired EntityManager entityManager;
    @Autowired DepartmentRepository departments;
    @Autowired UserRepository users;
    @Autowired RegistrationPeriodRepository periods;
    @Autowired TopicRepository topics;
    @Autowired StudentGroupRepository groups;
    @Autowired AnnouncementRepository announcements;
    @Autowired DefenseCouncilRepository councils;
    @Autowired CouncilMemberRepository councilMembers;
    @Autowired TopicService topicService;
    @Autowired RegistrationPeriodService periodService;
    Department department;
    User advisor;
    RegistrationPeriod period;

    @BeforeEach void setup() {
        department = departments.save(Department.builder().code("TEST").name("Bộ môn").build());
        advisor = users.save(User.builder().username("advisor").password("unused").fullName("Advisor")
                .email("advisor@example.com").role(RoleType.ROLE_LECTURER).build());
        period = period("Đợt 1");
    }

    private RegistrationPeriod period(String name) {
        var now = LocalDateTime.now();
        return periods.save(RegistrationPeriod.builder().name(name).type(PeriodType.COURSE_PROJECT)
                .status(PeriodStatus.IN_PROGRESS).lecturerRegStart(now.minusDays(5)).lecturerRegEnd(now.minusDays(4))
                .studentRegStart(now.minusDays(3)).studentRegEnd(now.minusDays(2)).build());
    }

    private Topic topic(RegistrationPeriod period, int index) {
        return topics.save(Topic.builder().code("CODE" + index).titleVi("Topic " + index).description("Description")
                .period(period).department(department).primaryAdvisor(advisor).approvalStatus(TopicApprovalStatus.APPROVED).build());
    }

    private void resetStatistics() {
        entityManager.flush();
        entityManager.clear();
        entityManager.getEntityManagerFactory().unwrap(SessionFactory.class).getStatistics().clear();
    }

    private long statements() {
        return entityManager.getEntityManagerFactory().unwrap(SessionFactory.class).getStatistics().getPrepareStatementCount();
    }

    @Test void topicPageUsesOneBulkGroupLookupInsteadOfOneQueryPerTopic() {
        for (int i = 1; i <= 23; i++) {
            Topic topic = topic(period, i);
            groups.save(StudentGroup.builder().name("Group " + i).period(period).topic(topic).build());
        }
        resetStatistics();
        var page = topicService.getTopics(null, null, null, null, null, null, Pagination.request(0, 10));
        assertThat(page.getContent()).hasSize(10);
        assertThat(page.getTotalElements()).isEqualTo(23);
        assertThat(page.getContent()).allSatisfy(t -> assertThat(t.getAssignedGroupName()).isNotBlank());
        assertThat(statements()).isLessThanOrEqualTo(3); // select page, count, groups in this page
    }

    @Test void keywordAndAdvisorFiltersApplyBeforeTheDatabasePage() {
        for (int i = 1; i <= 23; i++) topic(period, i);
        resetStatistics();
        var page = topicService.getTopics(null, null, TopicApprovalStatus.APPROVED, null, "CODE23", advisor.getId(), Pagination.request(0, 10));
        assertThat(page.getContent()).extracting(t -> t.getCode()).containsExactly("CODE23");
        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test void periodCountsAreAggregatedForThePageInsteadOfQueriedPerRow() {
        for (int i = 1; i <= 12; i++) {
            RegistrationPeriod p = i == 1 ? period : period("Đợt " + i);
            Topic topic = topic(p, i);
            groups.save(StudentGroup.builder().name("Group " + i).period(p).topic(topic).build());
        }
        resetStatistics();
        var page = periodService.getAllPeriods(null, null, null, false, Pagination.request(0, 10));
        assertThat(page.getTotalElements()).isEqualTo(12);
        assertThat(page.getContent()).hasSize(10).allSatisfy(p -> {
            assertThat(p.getTopicCount()).isEqualTo(1);
            assertThat(p.getGroupCount()).isEqualTo(1);
        });
        assertThat(statements()).isLessThanOrEqualTo(4); // page, count, topic counts, group counts
    }

    @Test void announcementRoleAndSearchFilterApplyBeforePagination() {
        for (int i = 0; i < 12; i++) {
            announcements.save(Announcement.builder().title("Lecturer " + i).content("Private")
                    .author(advisor).targetRole(RoleType.ROLE_LECTURER).build());
        }
        announcements.save(Announcement.builder().title("Student needle").content("Notice")
                .author(advisor).targetRole(RoleType.ROLE_STUDENT).build());
        resetStatistics();
        var visible = announcements.searchAnnouncements(RoleType.ROLE_STUDENT, false, null, "%needle%", Pagination.request(0, 10));
        assertThat(visible.getContent()).extracting(Announcement::getTitle).containsExactly("Student needle");
        assertThat(announcements.searchAnnouncements(RoleType.ROLE_STUDENT, false, null, "%Lecturer%", Pagination.request(0, 10))).isEmpty();
    }

    @Test void myCouncilsFilterRunsBeforePagination() {
        User other = users.save(User.builder().username("other").fullName("Other").password("unused")
                .email("other@example.com").role(RoleType.ROLE_LECTURER).build());
        for (int i = 1; i <= 13; i++) {
            DefenseCouncil council = councils.save(DefenseCouncil.builder().code("COUNCIL" + i).name("Council " + i).period(period).build());
            councilMembers.save(CouncilMember.builder().council(council).lecturer(i == 13 ? advisor : other)
                    .councilRole(CouncilRole.CHAIRMAN).build());
        }
        resetStatistics();
        var mine = councils.findByPeriodId(period.getId(), advisor.getId(), Pagination.request(0, 10));
        assertThat(mine.getContent()).extracting(DefenseCouncil::getCode).containsExactly("COUNCIL13");
        assertThat(mine.getTotalElements()).isEqualTo(1);
    }

    @Test void groupMemberSearchAndAdvisorScopeRunBeforePagination() {
        User student = users.save(User.builder().username("student").fullName("Student Needle").password("unused")
                .email("student@example.com").role(RoleType.ROLE_STUDENT).build());
        for (int i = 1; i <= 13; i++) {
            Topic topic = topic(period, i);
            StudentGroup group = StudentGroup.builder().code("GROUP" + i).name("Group " + i).period(period).topic(topic).build();
            if (i == 13) group.getMembers().add(GroupMember.builder().group(group).student(student).groupRole(GroupRole.LEADER).build());
            groups.save(group);
        }
        resetStatistics();
        var matching = groups.filterGroups(period.getId(), null, "%needle%", advisor.getId(), Pagination.request(0, 10));
        assertThat(matching.getContent()).extracting(StudentGroup::getCode).containsExactly("GROUP13");
        assertThat(groups.filterGroups(period.getId(), null, "%needle%", student.getId(), Pagination.request(0, 10))).isEmpty();
    }
}
