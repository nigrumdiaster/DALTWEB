package com.daltweb.topicmanagement.service;

import com.daltweb.topicmanagement.event.AnnouncementCreatedEvent;
import com.daltweb.topicmanagement.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@ConditionalOnProperty(name = "app.mail.enabled", havingValue = "true")
public class AnnouncementEmailService {
    private static final Logger log = LoggerFactory.getLogger(AnnouncementEmailService.class);

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final TaskExecutor executor;
    private final String from;

    public AnnouncementEmailService(UserRepository userRepository, JavaMailSender mailSender,
                                    @Qualifier("announcementMailExecutor") TaskExecutor executor,
                                    @Value("${app.mail.from}") String from) {
        Assert.hasText(from, "MAIL_FROM or MAIL_USERNAME must be set when MAIL_ENABLED=true");
        this.userRepository = userRepository;
        this.mailSender = mailSender;
        this.executor = executor;
        this.from = from;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAnnouncementCreated(AnnouncementCreatedEvent event) {
        try {
            executor.execute(() -> deliver(event));
        } catch (TaskRejectedException ex) {
            log.error("Email queue unavailable for announcement {}", event.id(), ex);
        }
    }

    private void deliver(AnnouncementCreatedEvent event) {
        List<String> recipients;
        try {
            recipients = userRepository.findAnnouncementRecipientEmails(event.targetRole());
        } catch (RuntimeException ex) {
            log.error("Cannot load email recipients for announcement {}", event.id(), ex);
            return;
        }

        String text = "THÔNG BÁO MỚI\n\n" + event.title() + "\n\n" + event.content()
                + "\n\nNgười đăng: " + event.authorName();
        if (StringUtils.hasText(event.fileUrl())) {
            text += "\nTệp đính kèm: " + event.fileUrl();
        }
        text += "\n\nVui lòng đăng nhập hệ thống quản lý đề tài để xem chi tiết.";

        int sent = 0;
        for (String recipient : recipients) {
            // Each message has one recipient to keep other users' addresses private.
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(recipient.trim());
            message.setSubject("[Quản lý đề tài] " + event.title().replaceAll("[\\r\\n]+", " "));
            message.setText(text);
            try {
                mailSender.send(message);
                sent++;
            } catch (MailException | IllegalArgumentException ex) {
                log.warn("Email delivery failed for announcement {} ({})", event.id(),
                        ex.getClass().getSimpleName());
            }
        }
        log.info("Announcement {}: sent {} of {} email notifications", event.id(), sent, recipients.size());
    }
}
