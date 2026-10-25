package com.daltweb.topicmanagement.service;

import com.daltweb.topicmanagement.constant.RoleType;
import com.daltweb.topicmanagement.event.AnnouncementCreatedEvent;
import com.daltweb.topicmanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AnnouncementEmailServiceTest {
    private final UserRepository users = mock(UserRepository.class);
    private final JavaMailSender sender = mock(JavaMailSender.class);
    private final AnnouncementCreatedEvent event = new AnnouncementCreatedEvent(1L, "Lịch bảo vệ\r\nMới",
            "Sinh viên chuẩn bị báo cáo.", RoleType.ROLE_STUDENT, "Trưởng khoa", "https://example.com/report.pdf");

    @Test
    void sendsVietnameseContentAndAttachmentLinkToEachRecipientPrivately() {
        when(users.findAnnouncementRecipientEmails(RoleType.ROLE_STUDENT))
                .thenReturn(List.of("one@example.com", "two@example.com"));

        service(new SyncTaskExecutor()).onAnnouncementCreated(event);

        ArgumentCaptor<SimpleMailMessage> messages = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(sender, times(2)).send(messages.capture());
        assertThat(messages.getAllValues()).allSatisfy(message -> {
            assertThat(message.getTo()).hasSize(1);
            assertThat(message.getCc()).isNull();
            assertThat(message.getBcc()).isNull();
            assertThat(message.getFrom()).isEqualTo("system@example.com");
            assertThat(message.getSubject()).contains("Lịch bảo vệ").doesNotContain("\r", "\n");
            assertThat(message.getText()).contains("Sinh viên chuẩn bị báo cáo.", "Trưởng khoa", event.fileUrl());
        });
        assertThat(messages.getAllValues()).extracting(message -> message.getTo()[0])
                .containsExactly("one@example.com", "two@example.com");
    }

    @Test
    void continuesWithOtherRecipientsAfterSmtpFailure() {
        when(users.findAnnouncementRecipientEmails(RoleType.ROLE_STUDENT))
                .thenReturn(List.of("one@example.com", "two@example.com"));
        doThrow(new MailSendException("SMTP unavailable")).doNothing().when(sender).send(any(SimpleMailMessage.class));

        assertThatCode(() -> service(new SyncTaskExecutor()).onAnnouncementCreated(event)).doesNotThrowAnyException();
        verify(sender, times(2)).send(any(SimpleMailMessage.class));
    }

    @Test
    void doesNotSendWhenNoRecipientsMatch() {
        when(users.findAnnouncementRecipientEmails(RoleType.ROLE_STUDENT)).thenReturn(List.of());
        service(new SyncTaskExecutor()).onAnnouncementCreated(event);
        verifyNoInteractions(sender);
    }

    @Test
    void queueRejectionDoesNotFailTheAlreadySavedAnnouncement() {
        TaskExecutor executor = task -> { throw new TaskRejectedException("Queue full"); };
        assertThatCode(() -> service(executor).onAnnouncementCreated(event)).doesNotThrowAnyException();
        verifyNoInteractions(users, sender);
    }

    private AnnouncementEmailService service(TaskExecutor executor) {
        return new AnnouncementEmailService(users, sender, executor, "system@example.com");
    }
}
