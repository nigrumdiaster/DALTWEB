package com.daltweb.topicmanagement.dto.topic;

import com.daltweb.topicmanagement.constant.TopicApprovalStatus;
import jakarta.validation.constraints.NotNull;

public class TopicApprovalRequest {

    @NotNull(message = "Trạng thái phê duyệt không được để trống")
    private TopicApprovalStatus approvalStatus;

    private String rejectionReason;

    public TopicApprovalRequest() {}

    public TopicApprovalStatus getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(TopicApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}
