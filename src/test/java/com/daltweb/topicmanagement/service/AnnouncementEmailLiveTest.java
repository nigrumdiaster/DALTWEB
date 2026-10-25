package com.daltweb.topicmanagement.service;

import com.daltweb.topicmanagement.constant.RoleType;
import com.daltweb.topicmanagement.event.AnnouncementCreatedEvent;
import com.daltweb.topicmanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Explicit opt-in only: looks up the recipient and sends ONE real email.
 * Set RUN_LIVE_EMAIL_TEST=true and MAIL_SMOKE_TEST_RECIPIENT before running this test.
 * Uses .env SMTP/database configuration. Does not create users or announcements.
 */
@EnabledIfEnvironmentVariable(named = "RUN_LIVE_EMAIL_TEST", matches = "true")
class AnnouncementEmailLiveTest {

    @Test
    void sendsOneRealEmailToTheSpecifiedAddress() {
        String recipient = System.getenv("MAIL_SMOKE_TEST_RECIPIENT");
        assertThat(recipient).as("MAIL_SMOKE_TEST_RECIPIENT must name the authorized recipient").isNotBlank();

        SpringApplication app = new SpringApplication(MailProbeConfig.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        try (var context = app.run("--spring.main.banner-mode=off", "--spring.mail.properties.mail.debug=false")) {
            var environment = context.getEnvironment();
            assertThat(environment.getProperty("app.mail.enabled", Boolean.class, false))
                    .as("MAIL_ENABLED must be true").isTrue();

            // Only JDBC and Mail are configured: no JPA schema changes or application seeders.
            var jdbc = context.getBean(JdbcTemplate.class);
            var matches = jdbc.query("SELECT id, role, is_active FROM users WHERE LOWER(TRIM(email)) = LOWER(?)",
                    (row, index) -> new Recipient(row.getLong("id"),
                            RoleType.valueOf(row.getString("role")), row.getBoolean("is_active")), recipient);
            assertThat(matches).as("Email must not belong to multiple users").hasSizeLessThanOrEqualTo(1);
            Recipient user = matches.isEmpty() ? null : matches.get(0);
            RoleType targetRole = user == null ? null : user.role();
            if (user != null) {
                assertThat(user.active()).as("The recipient account must be active").isTrue();
                System.out.printf("Verified active recipient: userId=%d, role=%s%n", user.id(), user.role());
            } else {
                System.out.println("No matching user; testing SMTP for the authorized address without creating an account.");
            }

            // Limit the production delivery service to the single authorized address.
            UserRepository users = mock(UserRepository.class);
            when(users.findAnnouncementRecipientEmails(targetRole)).thenReturn(List.of(recipient));
            JavaMailSender realSender = context.getBean(JavaMailSender.class);
            JavaMailSender sender = mock(JavaMailSender.class);
            AtomicBoolean accepted = new AtomicBoolean();
            AtomicReference<String> failure = new AtomicReference<>("none");
            doAnswer(invocation -> {
                try {
                    realSender.send(invocation.getArgument(0, SimpleMailMessage.class));
                    accepted.set(true);
                    return null;
                } catch (MailException ex) {
                    Throwable cause = ex.getMostSpecificCause();
                    String detail = cause.getClass().getSimpleName() + ": " + cause.getMessage();
                    String password = environment.getProperty("spring.mail.password", "");
                    failure.set(password.isBlank() ? detail : detail.replace(password, "[redacted]"));
                    throw ex;
                }
            }).when(sender).send(any(SimpleMailMessage.class));

            var service = new AnnouncementEmailService(users, sender, new SyncTaskExecutor(),
                    environment.getRequiredProperty("app.mail.from"));
            String time = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).toString();
            service.onAnnouncementCreated(new AnnouncementCreatedEvent(0L,
                    "[KIỂM THỬ] Chức năng thông báo qua email",
                    "Đây là email kiểm thử từ hệ thống quản lý đề tài.\n"
                            + "Địa chỉ nhận: " + recipient + "\nThời gian: " + time
                            + "\nEmail này chỉ dùng để kiểm tra gửi thư. Không cần trả lời.",
                    targetRole, "Hệ thống quản lý đề tài", null));

            verify(sender, times(1)).send(any(SimpleMailMessage.class));
            assertThat(accepted.get()).as("SMTP must accept the message; failure: %s", failure.get()).isTrue();
            System.out.println("SMTP accepted one test email for " + recipient);
        }
    }

    private record Recipient(Long id, RoleType role, boolean active) {}

    @TestConfiguration(proxyBeanMethods = false)
    @ImportAutoConfiguration({DataSourceAutoConfiguration.class, JdbcTemplateAutoConfiguration.class,
            MailSenderAutoConfiguration.class})
    static class MailProbeConfig {}
}
