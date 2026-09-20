package com.daltweb.topicmanagement.controller;

import com.daltweb.topicmanagement.constant.PeriodStatus;
import com.daltweb.topicmanagement.constant.PeriodType;
import com.daltweb.topicmanagement.dto.common.ApiResponse;
import com.daltweb.topicmanagement.dto.period.PeriodCreateRequest;
import com.daltweb.topicmanagement.dto.period.PeriodStatusRequest;
import com.daltweb.topicmanagement.dto.period.PeriodUpdateRequest;
import com.daltweb.topicmanagement.dto.period.RegistrationPeriodDto;
import com.daltweb.topicmanagement.service.RegistrationPeriodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registration-periods")
@Tag(name = "4. Registration Periods Management", description = "Quản lý các đợt đăng ký đề tài (Môn học/NCKH/TLCN/KLTN) theo từng giai đoạn")
public class RegistrationPeriodController {

    private final RegistrationPeriodService periodService;

    public RegistrationPeriodController(RegistrationPeriodService periodService) {
        this.periodService = periodService;
    }

    @GetMapping
    @Operation(summary = "Danh sách các đợt đăng ký", description = "Lọc theo loại (Môn học/NCKH/TLCN/KLTN) và trạng thái")
    public ResponseEntity<ApiResponse<List<RegistrationPeriodDto>>> getPeriods(
            @RequestParam(required = false) PeriodType type,
            @RequestParam(required = false) PeriodStatus status) {
        List<RegistrationPeriodDto> list = periodService.getAllPeriods(type, status);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết đợt đăng ký theo ID")
    public ResponseEntity<ApiResponse<RegistrationPeriodDto>> getPeriodById(@PathVariable Long id) {
        RegistrationPeriodDto dto = periodService.getPeriodById(id);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEAN')")
    @Operation(summary = "Trưởng khoa tạo đợt đăng ký mới")
    public ResponseEntity<ApiResponse<RegistrationPeriodDto>> createPeriod(@Valid @RequestBody PeriodCreateRequest request) {
        RegistrationPeriodDto created = periodService.createPeriod(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo đợt đăng ký thành công!", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEAN')")
    @Operation(summary = "Cập nhật thông tin đợt đăng ký")
    public ResponseEntity<ApiResponse<RegistrationPeriodDto>> updatePeriod(
            @PathVariable Long id,
            @Valid @RequestBody PeriodUpdateRequest request) {
        RegistrationPeriodDto updated = periodService.updatePeriod(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật đợt đăng ký thành công!", updated));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEAN')")
    @Operation(summary = "Chuyển trạng thái đợt đăng ký")
    public ResponseEntity<ApiResponse<RegistrationPeriodDto>> updatePeriodStatus(
            @PathVariable Long id,
            @Valid @RequestBody PeriodStatusRequest request) {
        RegistrationPeriodDto updated = periodService.updatePeriodStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Chuyển trạng thái đợt thành công!", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEAN')")
    @Operation(summary = "Xóa đợt đăng ký")
    public ResponseEntity<ApiResponse<Void>> deletePeriod(@PathVariable Long id) {
        periodService.deletePeriod(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa đợt đăng ký thành công!", null));
    }
}
