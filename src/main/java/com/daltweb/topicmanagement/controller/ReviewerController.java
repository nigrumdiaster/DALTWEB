package com.daltweb.topicmanagement.controller;

import com.daltweb.topicmanagement.dto.common.ApiResponse;
import com.daltweb.topicmanagement.dto.reviewer.AssignReviewerRequest;
import com.daltweb.topicmanagement.dto.reviewer.ReviewerAssignmentDto;
import com.daltweb.topicmanagement.dto.reviewer.ReviewerGradeRequest;
import com.daltweb.topicmanagement.service.ReviewerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviewers")
@Tag(name = "7. Reviewers Management (GVPB)", description = "Phân công Giảng viên phản biện (GVPB) và nộp điểm/nhận xét phản biện")
public class ReviewerController {

    private final ReviewerService reviewerService;

    public ReviewerController(ReviewerService reviewerService) {
        this.reviewerService = reviewerService;
    }

    @GetMapping("/period/{periodId}")
    @Operation(summary = "Danh sách phân công GVPB theo đợt")
    public ResponseEntity<ApiResponse<List<ReviewerAssignmentDto>>> getAssignmentsByPeriod(@PathVariable Long periodId) {
        List<ReviewerAssignmentDto> list = reviewerService.getAssignmentsByPeriod(periodId);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/my-tasks")
    @PreAuthorize("hasAnyRole('LECTURER', 'DEPARTMENT_HEAD', 'ADMIN', 'DEAN')")
    @Operation(summary = "Xem danh sách các đề tài mình được phân công phản biện")
    public ResponseEntity<ApiResponse<List<ReviewerAssignmentDto>>> getMyReviewTasks() {
        List<ReviewerAssignmentDto> list = reviewerService.getMyReviewTasks();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping("/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEAN', 'DEPARTMENT_HEAD')")
    @Operation(summary = "Phân công Giảng viên phản biện cho đề tài")
    public ResponseEntity<ApiResponse<ReviewerAssignmentDto>> assignReviewer(@Valid @RequestBody AssignReviewerRequest request) {
        ReviewerAssignmentDto assigned = reviewerService.assignReviewer(request);
        return ResponseEntity.ok(ApiResponse.success("Phân công GVPB thành công!", assigned));
    }

    @PostMapping("/grade")
    @PreAuthorize("hasAnyRole('LECTURER', 'DEPARTMENT_HEAD', 'ADMIN', 'DEAN')")
    @Operation(summary = "GVPB nhập điểm và nhận xét phản biện")
    public ResponseEntity<ApiResponse<ReviewerAssignmentDto>> gradeReview(@Valid @RequestBody ReviewerGradeRequest request) {
        ReviewerAssignmentDto graded = reviewerService.gradeReview(request);
        return ResponseEntity.ok(ApiResponse.success("Gửi điểm phản biện thành công!", graded));
    }
}
