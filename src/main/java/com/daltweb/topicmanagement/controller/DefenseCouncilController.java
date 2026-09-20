package com.daltweb.topicmanagement.controller;

import com.daltweb.topicmanagement.dto.common.ApiResponse;
import com.daltweb.topicmanagement.dto.council.*;
import com.daltweb.topicmanagement.service.DefenseCouncilService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/councils")
@Tag(name = "8. Defense Councils Management", description = "Quản lý hội đồng bảo vệ/phản biện (3-5 GV, 1 Chủ tịch, 1 Thư ký), phân công đề tài và thành viên chấm điểm")
public class DefenseCouncilController {

    private final DefenseCouncilService councilService;

    public DefenseCouncilController(DefenseCouncilService councilService) {
        this.councilService = councilService;
    }

    @GetMapping("/period/{periodId}")
    @Operation(summary = "Danh sách các hội đồng bảo vệ trong đợt")
    public ResponseEntity<ApiResponse<List<DefenseCouncilDto>>> getCouncilsByPeriod(@PathVariable Long periodId) {
        List<DefenseCouncilDto> list = councilService.getCouncilsByPeriod(periodId);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết hội đồng theo ID")
    public ResponseEntity<ApiResponse<DefenseCouncilDto>> getCouncilById(@PathVariable Long id) {
        DefenseCouncilDto dto = councilService.getCouncilById(id);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEAN')")
    @Operation(summary = "Khoa thành lập hội đồng phản biện/bảo vệ")
    public ResponseEntity<ApiResponse<DefenseCouncilDto>> createCouncil(@Valid @RequestBody CouncilCreateRequest request) {
        DefenseCouncilDto created = councilService.createCouncil(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Thành lập hội đồng thành công!", created));
    }

    @PostMapping("/{id}/assign-topics")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEAN')")
    @Operation(summary = "Phân công danh sách đề tài cho hội đồng bảo vệ")
    public ResponseEntity<ApiResponse<DefenseCouncilDto>> assignTopics(
            @PathVariable Long id,
            @Valid @RequestBody AssignTopicsToCouncilRequest request) {
        DefenseCouncilDto updated = councilService.assignTopicsToCouncil(id, request);
        return ResponseEntity.ok(ApiResponse.success("Phân công đề tài cho hội đồng thành công!", updated));
    }

    @PostMapping("/{id}/grade")
    @PreAuthorize("hasAnyRole('LECTURER', 'DEPARTMENT_HEAD', 'ADMIN', 'DEAN')")
    @Operation(summary = "Thành viên hội đồng nhập điểm và nhận xét cho đề tài")
    public ResponseEntity<ApiResponse<Void>> gradeTopic(
            @PathVariable Long id,
            @Valid @RequestBody CouncilGradingRequest request) {
        councilService.gradeTopicInCouncil(id, request);
        return ResponseEntity.ok(ApiResponse.success("Nhập điểm hội đồng thành công!", null));
    }
}
