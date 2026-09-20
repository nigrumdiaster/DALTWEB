package com.daltweb.topicmanagement.controller;

import com.daltweb.topicmanagement.constant.TopicApprovalStatus;
import com.daltweb.topicmanagement.dto.common.ApiResponse;
import com.daltweb.topicmanagement.dto.topic.TopicApprovalRequest;
import com.daltweb.topicmanagement.dto.topic.TopicCreateRequest;
import com.daltweb.topicmanagement.dto.topic.TopicDto;
import com.daltweb.topicmanagement.dto.topic.TopicUpdateRequest;
import com.daltweb.topicmanagement.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
@Tag(name = "5. Topics Management", description = "Quản lý đề tài theo bộ môn, giảng viên đề xuất, trưởng bộ môn phê duyệt và công bố danh sách đề tài")
public class TopicController {

    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách đề tài")
    public ResponseEntity<ApiResponse<List<TopicDto>>> getTopics(
            @RequestParam(required = false) Long periodId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) TopicApprovalStatus status,
            @RequestParam(required = false) Boolean isAssigned,
            @RequestParam(required = false) String keyword) {
        List<TopicDto> list = topicService.getTopics(periodId, departmentId, status, isAssigned, keyword);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/published")
    @Operation(summary = "Danh sách đề tài đã được phê duyệt & công bố")
    public ResponseEntity<ApiResponse<List<TopicDto>>> getPublishedTopics(
            @RequestParam(required = false) Long periodId,
            @RequestParam(required = false) Long departmentId) {
        List<TopicDto> list = topicService.getApprovedPublishedTopics(periodId, departmentId);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết đề tài theo ID")
    public ResponseEntity<ApiResponse<TopicDto>> getTopicById(@PathVariable Long id) {
        TopicDto dto = topicService.getTopicById(id);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('LECTURER', 'DEPARTMENT_HEAD', 'ADMIN', 'DEAN')")
    @Operation(summary = "Giảng viên đề xuất đề tài mới (Giai đoạn 1)")
    public ResponseEntity<ApiResponse<TopicDto>> createTopic(@Valid @RequestBody TopicCreateRequest request) {
        TopicDto created = topicService.createTopic(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Đề xuất đề tài thành công!", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('LECTURER', 'DEPARTMENT_HEAD', 'ADMIN', 'DEAN')")
    @Operation(summary = "Chỉnh sửa thông tin đề tài đã tạo (khi chưa có nhóm đăng ký)")
    public ResponseEntity<ApiResponse<TopicDto>> updateTopic(
            @PathVariable Long id,
            @Valid @RequestBody TopicUpdateRequest request) {
        TopicDto updated = topicService.updateTopic(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật đề tài thành công!", updated));
    }

    @PatchMapping("/{id}/approval")
    @PreAuthorize("hasAnyRole('DEPARTMENT_HEAD', 'ADMIN', 'DEAN')")
    @Operation(summary = "Trưởng bộ môn phê duyệt đề tài")
    public ResponseEntity<ApiResponse<TopicDto>> approveTopic(
            @PathVariable Long id,
            @Valid @RequestBody TopicApprovalRequest request) {
        TopicDto result = topicService.approveTopic(id, request);
        return ResponseEntity.ok(ApiResponse.success("Phê duyệt đề tài thành công!", result));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('LECTURER', 'DEPARTMENT_HEAD', 'ADMIN', 'DEAN')")
    @Operation(summary = "Xóa đề tài (khi chưa có nhóm nhận)")
    public ResponseEntity<ApiResponse<Void>> deleteTopic(@PathVariable Long id) {
        topicService.deleteTopic(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa đề tài thành công!", null));
    }
}
