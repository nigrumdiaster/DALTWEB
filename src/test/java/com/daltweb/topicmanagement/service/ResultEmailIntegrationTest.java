package com.daltweb.topicmanagement.service;

import com.daltweb.topicmanagement.event.ResultPublishedEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.transaction.TestTransaction;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:result-mail-test",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "app.mail.enabled=true", "app.mail.from=system@example.com"
})
@Import({ResultEmailService.class, ResultEmailIntegrationTest.ExecutorConfig.class})
class ResultEmailIntegrationTest {
    @Autowired ApplicationEventPublisher publisher;
    @MockBean JavaMailSender sender;

    @TestConfiguration
    static class ExecutorConfig {
        @Bean TaskExecutor announcementMailExecutor() { return new SyncTaskExecutor(); }
    }

    private ResultPublishedEvent event() {
        return new ResultPublishedEvent(1L, "Đợt tốt nghiệp", "Đề tài tiếng Việt", 8.5, "A",
                List.of("one@example.com", "two@example.com", "three@example.com"));
    }

    @Test void sendsSeparateMessagesOnlyAfterCommit() {
        publisher.publishEvent(event());
        verifyNoInteractions(sender);
        TestTransaction.flagForCommit();
        TestTransaction.end();
        ArgumentCaptor<SimpleMailMessage> messages = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(sender, times(3)).send(messages.capture());
        assertThat(messages.getAllValues()).allSatisfy(message -> {
            assertThat(message.getTo()).hasSize(1);
            assertThat(message.getCc()).isNull();
            assertThat(message.getBcc()).isNull();
            assertThat(message.getText()).contains("Đợt tốt nghiệp", "Đề tài tiếng Việt", "8.5", "A");
        });
        assertThat(messages.getAllValues()).extracting(m -> m.getTo()[0])
                .containsExactly("one@example.com", "two@example.com", "three@example.com");
    }

    @Test void rollbackNeverSendsResults() {
        publisher.publishEvent(event());
        TestTransaction.flagForRollback();
        TestTransaction.end();
        verifyNoInteractions(sender);
    }

    @Test void smtpFailureDoesNotPreventOtherMembersReceivingResults() {
        doThrow(new MailSendException("SMTP unavailable")).doNothing().when(sender).send(any(SimpleMailMessage.class));
        publisher.publishEvent(event());
        TestTransaction.flagForCommit();
        assertThatCode(TestTransaction::end).doesNotThrowAnyException();
        verify(sender, times(3)).send(any(SimpleMailMessage.class));
    }
}
