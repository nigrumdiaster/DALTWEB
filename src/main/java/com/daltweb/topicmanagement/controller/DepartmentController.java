package com.daltweb.topicmanagement.controller;

import com.daltweb.topicmanagement.dto.common.ApiResponse;
import com.daltweb.topicmanagement.dto.department.DepartmentDto;
import com.daltweb.topicmanagement.dto.department.DepartmentRequest;
import com.daltweb.topicmanagement.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@Tag(name = "3. Departments Management", description = "Quản lý danh sách các bộ môn trong khoa CNTT")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả các bộ môn")
    public ResponseEntity<ApiResponse<List<DepartmentDto>>> getAllDepartments() {
        List<DepartmentDto> list = departmentService.getAllDepartments();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết bộ môn theo ID")
    public ResponseEntity<ApiResponse<DepartmentDto>> getDepartmentById(@PathVariable Long id) {
        DepartmentDto dto = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEAN')")
    @Operation(summary = "Tạo bộ môn mới (Admin / Trưởng khoa)")
    public ResponseEntity<ApiResponse<DepartmentDto>> createDepartment(@Valid @RequestBody DepartmentRequest request) {
        DepartmentDto created = departmentService.createDepartment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo bộ môn thành công!", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEAN')")
    @Operation(summary = "Cập nhật thông tin bộ môn / Gán Trưởng bộ môn")
    public ResponseEntity<ApiResponse<DepartmentDto>> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentRequest request) {
        DepartmentDto updated = departmentService.updateDepartment(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật bộ môn thành công!", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEAN')")
    @Operation(summary = "Xóa bộ môn")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa bộ môn thành công!", null));
    }
}
