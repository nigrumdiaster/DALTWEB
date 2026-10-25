package com.daltweb.topicmanagement.repository;

import com.daltweb.topicmanagement.constant.RoleType;
import com.daltweb.topicmanagement.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class SearchRepositoryTest {
    @Autowired UserRepository users;
    @Autowired TopicRepository topics;

    @Test
    void optionalKeywordAndCaseInsensitiveSearch() {
        users.saveAndFlush(User.builder().username("demo_admin").password("unused")
                .fullName("Demo User").email("demo@example.com")
                .code("GV001").role(RoleType.ROLE_ADMIN).build());

        assertThat(users.searchUsers(null, null, null, org.springframework.data.domain.Pageable.unpaged())).hasSize(1);
        assertThat(users.searchUsers(null, null, "", org.springframework.data.domain.Pageable.unpaged())).hasSize(1);
        for (String keyword : new String[]{"DEMO USER", "DEMO_ADMIN", "gv001"}) {
            assertThat(users.searchUsers(null, null, keyword, org.springframework.data.domain.Pageable.unpaged())).hasSize(1);
        }
        assertThat(users.searchUsers(null, null, "missing", org.springframework.data.domain.Pageable.unpaged())).isEmpty();
        assertThat(users.searchUsers(RoleType.ROLE_STUDENT, null, null, org.springframework.data.domain.Pageable.unpaged())).isEmpty();
    }

    @Test
    void topicQueryAcceptsAbsentAndPresentKeyword() {
        assertThat(topics.filterTopics(null, null, null, null, null, null, org.springframework.data.domain.Pageable.unpaged())).isEmpty();
        assertThat(topics.filterTopics(null, null, null, null, "Demo", null, org.springframework.data.domain.Pageable.unpaged())).isEmpty();
    }
}
