package com.daltweb.topicmanagement.constant;

public enum GraderRole {
    ADVISOR,           // GVHD
    @Deprecated
    CO_ADVISOR,        // Legacy database value; no longer used for grading.
    REVIEWER,          // GVPB
    COUNCIL_CHAIRMAN,  // Chủ tịch hội đồng
    COUNCIL_SECRETARY, // Thư ký hội đồng
    COUNCIL_MEMBER     // Ủy viên hội đồng
}
