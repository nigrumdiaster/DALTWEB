package com.daltweb.topicmanagement.dto.period;

import com.daltweb.topicmanagement.constant.PeriodStatus;
import jakarta.validation.constraints.NotNull;

public class PeriodStatusRequest {
    @NotNull(message = "Trạng thái không được để trống")
    private PeriodStatus status;

    public PeriodStatusRequest() {}
    public PeriodStatusRequest(PeriodStatus status) { this.status = status; }

    public PeriodStatus getStatus() { return status; }
    public void setStatus(PeriodStatus status) { this.status = status; }
}
