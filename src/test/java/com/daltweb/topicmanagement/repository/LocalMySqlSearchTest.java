package com.daltweb.topicmanagement.repository;

import com.daltweb.topicmanagement.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

// Explicit opt-in: reads existing local data without schema changes or fixtures.
@EnabledIfEnvironmentVariable(named = "RUN_LOCAL_MYSQL_TESTS", matches = "true")
@DataJpaTest(properties = {
        "spring.profiles.active=default",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect",
        "spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver",
        "spring.sql.init.mode=never",
        "spring.jpa.show-sql=false"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional(readOnly = true)
class LocalMySqlSearchTest {
    @Autowired UserRepository users;
    @Autowired TopicRepository topics;
    @Autowired DataSource dataSource;

    @Test
    void connectedToRealMySql() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            assertThat(connection.getMetaData().getDatabaseProductName()).isEqualTo("MySQL");
        }
    }

    @Test
    void userSearchHandlesNullEmptyAndCaseInsensitiveKeyword() {
        var baseline = users.findAll();
        assertThat(baseline).as("Local database must contain users to verify matching").isNotEmpty();
        var ids = baseline.stream().map(User::getId).toList();
        assertThat(users.searchUsers(null, null, null, org.springframework.data.domain.Pageable.unpaged())).extracting(User::getId)
                .containsExactlyInAnyOrderElementsOf(ids);
        assertThat(users.searchUsers(null, null, "", org.springframework.data.domain.Pageable.unpaged())).extracting(User::getId)
                .containsExactlyInAnyOrderElementsOf(ids);
        var sample = baseline.get(0);
        assertThat(users.searchUsers(null, null, sample.getUsername().toUpperCase(Locale.ROOT), org.springframework.data.domain.Pageable.unpaged()))
                .extracting(User::getId).contains(sample.getId());
        assertThat(users.searchUsers(sample.getRole(), null, null, org.springframework.data.domain.Pageable.unpaged()))
                .allMatch(user -> user.getRole() == sample.getRole());
        assertThat(users.searchUsers(null, null, "__no_match_785cf791__", org.springframework.data.domain.Pageable.unpaged())).isEmpty();
    }

    @Test
    void topicSearchHandlesNullEmptyAndPresentKeyword() {
        var baseline = topics.findAll();
        var all = topics.filterTopics(null, null, null, null, null, null, org.springframework.data.domain.Pageable.unpaged());
        assertThat(all).hasSize(baseline.size());
        assertThat(topics.filterTopics(null, null, null, null, "", null, org.springframework.data.domain.Pageable.unpaged())).hasSize(baseline.size());
        if (!baseline.isEmpty()) {
            var sample = baseline.get(0);
            assertThat(topics.filterTopics(null, null, null, null, sample.getTitleVi(), null, org.springframework.data.domain.Pageable.unpaged()))
                    .anyMatch(topic -> topic.getId().equals(sample.getId()));
        }
        assertThat(topics.filterTopics(null, null, null, null, "__no_match_785cf791__", null, org.springframework.data.domain.Pageable.unpaged())).isEmpty();
    }
}
