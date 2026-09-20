package com.daltweb.topicmanagement.controller;

import com.daltweb.topicmanagement.dto.common.ApiResponse;
import com.daltweb.topicmanagement.dto.council.FinalizeCouncilResultRequest;
import com.daltweb.topicmanagement.dto.grade.*;
import com.daltweb.topicmanagement.service.GradeAndResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "9. Grading & Final Results", description = "Chấm điểm GVHD, Chủ tịch hội đồng tổng hợp & chốt kết quả, Khoa công bố điểm và SV tra cứu kết quả")
public class GradeAndResultController {

    private final GradeAndResultService gradeAndResultService;

    public GradeAndResultController(GradeAndResultService gradeAndResultService) {
        this.gradeAndResultService = gradeAndResultService;
    }

    @PostMapping("/grades/advisor")
    @PreAuthorize("hasAnyRole('LECTURER', 'DEPARTMENT_HEAD', 'ADMIN', 'DEAN')")
    @Operation(summary = "Giảng viên hướng dẫn chấm điểm quá trình thực hiện đề tài")
    public ResponseEntity<ApiResponse<GradeDto>> gradeAdvisor(@Valid @RequestBody AdvisorGradeRequest request) {
        GradeDto result = gradeAndResultService.gradeAsAdvisor(request);
        return ResponseEntity.ok(ApiResponse.success("Giảng viên hướng dẫn chấm điểm thành công!", result));
    }

    @GetMapping("/grades/topic/{topicId}")
    @Operation(summary = "Xem tất cả các điểm thành phần của đề tài")
    public ResponseEntity<ApiResponse<List<GradeDto>>> getTopicGrades(@PathVariable Long topicId) {
        List<GradeDto> list = gradeAndResultService.getTopicGrades(topicId);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping("/results/finalize")
    @PreAuthorize("hasAnyRole('LECTURER', 'DEPARTMENT_HEAD', 'ADMIN', 'DEAN')")
    @Operation(summary = "Chủ tịch hội đồng tổng hợp và chốt kết quả phản biện")
    public ResponseEntity<ApiResponse<FinalResultDto>> finalizeTopicResult(@Valid @RequestBody FinalizeCouncilResultRequest request) {
        FinalResultDto finalized = gradeAndResultService.finalizeTopicResult(request);
        return ResponseEntity.ok(ApiResponse.success("Tổng hợp và chốt kết quả đề tài thành công!", finalized));
    }

    @PostMapping("/results/publish")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEAN')")
    @Operation(summary = "Khoa công bố kết quả đánh giá trên hệ thống")
    public ResponseEntity<ApiResponse<Void>> publishResults(@Valid @RequestBody PublishResultRequest request) {
        gradeAndResultService.publishResults(request);
        return ResponseEntity.ok(ApiResponse.success("Công bố kết quả thành công!", null));
    }

    @GetMapping("/results/period/{periodId}")
    @Operation(summary = "Xem bảng kết quả tổng hợp của đợt đăng ký")
    public ResponseEntity<ApiResponse<List<FinalResultDto>>> getResultsByPeriod(@PathVariable Long periodId) {
        List<FinalResultDto> list = gradeAndResultService.getResultsByPeriod(periodId);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/results/topic/{topicId}")
    @Operation(summary = "Xem kết quả chi tiết của 1 đề tài")
    public ResponseEntity<ApiResponse<FinalResultDto>> getTopicResult(@PathVariable Long topicId) {
        FinalResultDto dto = gradeAndResultService.getTopicResult(topicId);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/results/student/my-results")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Sinh viên tra cứu điểm và kết quả đánh giá của đề tài mình tham gia")
    public ResponseEntity<ApiResponse<List<FinalResultDto>>> getMyStudentResults() {
        List<FinalResultDto> list = gradeAndResultService.getMyStudentResults();
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}
