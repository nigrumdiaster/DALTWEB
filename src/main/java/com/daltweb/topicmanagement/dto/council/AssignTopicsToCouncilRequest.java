package com.daltweb.topicmanagement.dto.council;

import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

public class AssignTopicsToCouncilRequest {

    @NotEmpty(message = "Danh sách ID đề tài không được để trống")
    private List<Long> topicIds;

    private LocalDateTime defenseTime;
    private String notes;

    public AssignTopicsToCouncilRequest() {}

    public List<Long> getTopicIds() { return topicIds; }
    public void setTopicIds(List<Long> topicIds) { this.topicIds = topicIds; }
    public LocalDateTime getDefenseTime() { return defenseTime; }
    public void setDefenseTime(LocalDateTime defenseTime) { this.defenseTime = defenseTime; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
