package com.daltweb.topicmanagement.dto.group;

import jakarta.validation.constraints.NotBlank;

public class SubmitReportRequest {

    @NotBlank(message = "Đường dẫn báo cáo không được để trống")
    private String reportUrl;

    private String reportNote;

    public SubmitReportRequest() {}

    public String getReportUrl() { return reportUrl; }
    public void setReportUrl(String reportUrl) { this.reportUrl = reportUrl; }
    public String getReportNote() { return reportNote; }
    public void setReportNote(String reportNote) { this.reportNote = reportNote; }
}
