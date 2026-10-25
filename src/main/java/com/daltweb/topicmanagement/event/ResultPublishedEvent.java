package com.daltweb.topicmanagement.event;

import java.util.List;

public record ResultPublishedEvent(Long resultId, String periodName, String topicTitle,
        double finalScore, String letterGrade, List<String> recipientEmails) {
    public ResultPublishedEvent {
        recipientEmails = List.copyOf(recipientEmails);
    }
}
