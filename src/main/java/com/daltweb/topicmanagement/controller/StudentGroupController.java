package com.daltweb.topicmanagement.controller;

import com.daltweb.topicmanagement.constant.GroupStatus;
import com.daltweb.topicmanagement.dto.common.ApiResponse;
import com.daltweb.topicmanagement.dto.group.*;
import com.daltweb.topicmanagement.service.StudentGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@Tag(name = "6. Student Groups & Topic Registration", description = "Quản lý nhóm sinh viên (tối đa 3 SV), đăng ký đề tài (GĐ2), và nộp báo cáo bởi nhóm trưởng")
public class StudentGroupController {

    private final StudentGroupService groupService;

    public StudentGroupController(StudentGroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping
    @Operation(summary = "Danh sách nhóm sinh viên trong đợt")
    public ResponseEntity<ApiResponse<List<StudentGroupDto>>> getGroups(
            @RequestParam(required = false) Long periodId,
            @RequestParam(required = false) GroupStatus status) {
        List<StudentGroupDto> list = groupService.getGroups(periodId, status);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết nhóm theo ID")
    public ResponseEntity<ApiResponse<StudentGroupDto>> getGroupById(@PathVariable Long id) {
        StudentGroupDto dto = groupService.getGroupById(id);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/my-group")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Xem thông tin nhóm của sinh viên hiện tại trong đợt")
    public ResponseEntity<ApiResponse<StudentGroupDto>> getMyGroup(@RequestParam Long periodId) {
        StudentGroupDto dto = groupService.getMyGroupInPeriod(periodId);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Sinh viên tạo nhóm mới")
    public ResponseEntity<ApiResponse<StudentGroupDto>> createGroup(@Valid @RequestBody GroupCreateRequest request) {
        StudentGroupDto created = groupService.createGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo nhóm sinh viên thành công!", created));
    }

    @PostMapping("/{id}/members")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Nhóm trưởng thêm thành viên vào nhóm (tối đa 3 thành viên)")
    public ResponseEntity<ApiResponse<StudentGroupDto>> addMember(
            @PathVariable Long id,
            @Valid @RequestBody AddMemberRequest request) {
        StudentGroupDto updated = groupService.addMember(id, request);
        return ResponseEntity.ok(ApiResponse.success("Thêm thành viên vào nhóm thành công!", updated));
    }

    @DeleteMapping("/{id}/members/{studentId}")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Nhóm trưởng xóa thành viên khỏi nhóm")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @PathVariable Long id,
            @PathVariable Long studentId) {
        groupService.removeMember(id, studentId);
        return ResponseEntity.ok(ApiResponse.success("Xóa thành viên khỏi nhóm thành công!", null));
    }

    @PostMapping("/{id}/register-topic")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Nhóm trưởng đăng ký đề tài (Giai đoạn 2)")
    public ResponseEntity<ApiResponse<StudentGroupDto>> registerTopic(
            @PathVariable Long id,
            @Valid @RequestBody RegisterTopicRequest request) {
        StudentGroupDto updated = groupService.registerTopic(id, request);
        return ResponseEntity.ok(ApiResponse.success("Đăng ký đề tài thành công!", updated));
    }

    @DeleteMapping("/{id}/cancel-topic")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Nhóm trưởng hủy đăng ký đề tài (trong thời hạn GĐ2)")
    public ResponseEntity<ApiResponse<StudentGroupDto>> cancelTopicRegistration(@PathVariable Long id) {
        StudentGroupDto updated = groupService.cancelTopicRegistration(id);
        return ResponseEntity.ok(ApiResponse.success("Hủy đăng ký đề tài thành công!", updated));
    }

    @PostMapping("/{id}/reports")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Nhóm trưởng nộp báo cáo đề tài")
    public ResponseEntity<ApiResponse<StudentGroupDto>> submitReport(
            @PathVariable Long id,
            @Valid @RequestBody SubmitReportRequest request) {
        StudentGroupDto updated = groupService.submitReport(id, request);
        return ResponseEntity.ok(ApiResponse.success("Nộp báo cáo đề tài thành công!", updated));
    }
}
