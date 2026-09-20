package com.daltweb.topicmanagement.constant;

public enum PeriodStatus {
    DRAFT,
    PHASE1_LECTURER_REGISTRATION, // Giai đoạn 1: GV đăng ký đề tài
    PHASE1_REVIEWING,             // Bộ môn duyệt danh sách đề tài
    PHASE2_STUDENT_REGISTRATION,  // Giai đoạn 2: SV đăng ký đề tài đã công bố
    IN_PROGRESS,                  // Đang thực hiện đề tài & nộp báo cáo
    REVIEWING,                    // Phản biện (GVPB chấm điểm)
    DEFENSE,                      // Báo cáo hội đồng
    COMPLETED,                    // Hoàn thành & công bố kết quả
    CANCELLED
}
