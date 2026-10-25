package com.daltweb.topicmanagement.service;

import com.daltweb.topicmanagement.config.AnnouncementMailConfig;
import com.daltweb.topicmanagement.constant.RoleType;
import com.daltweb.topicmanagement.dto.announcement.AnnouncementRequest;
import com.daltweb.topicmanagement.entity.User;
import com.daltweb.topicmanagement.repository.AnnouncementRepository;
import com.daltweb.topicmanagement.repository.UserRepository;
import com.daltweb.topicmanagement.security.CustomUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.transaction.TestTransaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DataJpaTest(properties = {
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "app.mail.enabled=true",
        "app.mail.from=system@example.com"
})
@Import({AnnouncementService.class, AnnouncementEmailService.class, AnnouncementMailConfig.class})
class AnnouncementEmailIntegrationTest {
    @Autowired AnnouncementService service;
    @Autowired UserRepository users;
    @Autowired AnnouncementRepository announcements;
    @Autowired @Qualifier("announcementMailExecutor") TaskExecutor executor;
    @MockBean JavaMailSender sender;

    @BeforeEach
    void setUp() {
        // A previous test may have committed its fixture to verify AFTER_COMMIT.
        announcements.deleteAll();
        users.deleteAll();
        users.flush();
        User author = user("dean", RoleType.ROLE_DEAN, true);
        user("student", RoleType.ROLE_STUDENT, true);
        user("lecturer", RoleType.ROLE_LECTURER, true);
        user("inactive", RoleType.ROLE_STUDENT, false);
        CustomUserDetails principal = CustomUserDetails.build(author);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void sendsOnlyAfterCommitToActiveUsersOfTheTargetRole() throws Exception {
        service.createAnnouncement(request(RoleType.ROLE_STUDENT));
        verifyNoInteractions(sender);
        commitAndWaitForDelivery();

        ArgumentCaptor<SimpleMailMessage> message = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(sender).send(message.capture());
        assertThat(message.getValue().getTo()).containsExactly("student@example.com");
    }

    @Test
    void nullTargetRoleReachesAllActiveRoles() throws Exception {
        service.createAnnouncement(request(null));
        commitAndWaitForDelivery();

        ArgumentCaptor<SimpleMailMessage> messages = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(sender, times(3)).send(messages.capture());
        assertThat(messages.getAllValues()).extracting(message -> message.getTo()[0])
                .containsExactlyInAnyOrder("dean@example.com", "student@example.com", "lecturer@example.com");
    }

    @Test
    void rollbackDoesNotSendEmail() throws Exception {
        service.createAnnouncement(request(null));
        TestTransaction.flagForRollback();
        TestTransaction.end();
        waitForDelivery();
        verifyNoInteractions(sender);
    }

    @Test
    void optingOutAndEditingDoNotSendEmail() throws Exception {
        AnnouncementRequest request = request(null);
        request.setSendEmail(false);
        var created = service.createAnnouncement(request);
        request.setSendEmail(true);
        request.setTitle("Thông báo đã sửa");
        service.updateAnnouncement(created.getId(), request);
        commitAndWaitForDelivery();
        verifyNoInteractions(sender);
    }

    private User user(String username, RoleType role, boolean active) {
        return users.saveAndFlush(User.builder().username(username).password("unused")
                .fullName(username).email(username + "@example.com").role(role).active(active).build());
    }

    private AnnouncementRequest request(RoleType role) {
        AnnouncementRequest request = new AnnouncementRequest();
        request.setTitle("Thông báo mới");
        request.setContent("Nội dung tiếng Việt");
        request.setTargetRole(role);
        return request;
    }

    private void commitAndWaitForDelivery() throws Exception {
        TestTransaction.flagForCommit();
        TestTransaction.end();
        waitForDelivery();
    }

    private void waitForDelivery() throws Exception {
        // With one worker and a FIFO queue, this barrier runs after delivery.
        var finished = new java.util.concurrent.CountDownLatch(1);
        executor.execute(finished::countDown);
        assertThat(finished.await(10, java.util.concurrent.TimeUnit.SECONDS)).isTrue();
    }
}
