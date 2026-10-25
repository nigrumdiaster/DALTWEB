package com.daltweb.topicmanagement.service;

import com.daltweb.topicmanagement.event.ResultPublishedEvent;
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

@Service
@ConditionalOnProperty(name = "app.mail.enabled", havingValue = "true")
public class ResultEmailService {
    private static final Logger log = LoggerFactory.getLogger(ResultEmailService.class);
    private final JavaMailSender sender;
    private final TaskExecutor executor;
    private final String from;

    public ResultEmailService(JavaMailSender sender,
            @Qualifier("announcementMailExecutor") TaskExecutor executor, @Value("${app.mail.from}") String from) {
        Assert.hasText(from, "MAIL_FROM or MAIL_USERNAME must be set when MAIL_ENABLED=true");
        this.sender = sender;
        this.executor = executor;
        this.from = from;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onResultPublished(ResultPublishedEvent event) {
        try {
            executor.execute(() -> deliver(event));
        } catch (TaskRejectedException ex) {
            log.error("Email queue unavailable for result {}", event.resultId(), ex);
        }
    }

    private void deliver(ResultPublishedEvent event) {
        int sent = 0;
        for (String recipient : event.recipientEmails()) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(from);
                message.setTo(recipient);
                message.setSubject("[Quản lý đề tài] Công bố kết quả đề tài");
                message.setText("KẾT QUẢ ĐỀ TÀI\n\nĐợt: " + event.periodName()
                        + "\nĐề tài: " + event.topicTitle() + "\nĐiểm tổng kết: " + event.finalScore()
                        + "\nĐiểm chữ: " + event.letterGrade()
                        + "\n\nVui lòng đăng nhập hệ thống để tra cứu điểm chi tiết.");
                sender.send(message);
                sent++;
            } catch (MailException | IllegalArgumentException ex) {
                log.warn("Email delivery failed for result {} ({})", event.resultId(), ex.getClass().getSimpleName());
            }
        }
        log.info("Result {}: sent {} of {} result emails", event.resultId(), sent, event.recipientEmails().size());
    }
}
