package com.daltweb.topicmanagement.controller;

import com.daltweb.topicmanagement.dto.announcement.AnnouncementDto;
import com.daltweb.topicmanagement.dto.announcement.AnnouncementRequest;
import com.daltweb.topicmanagement.dto.common.ApiResponse;
import com.daltweb.topicmanagement.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@Tag(name = "10. Official Announcements", description = "Đăng tải và quản lý các thông báo chính thức của nhà trường và khoa CNTT")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách thông báo chính thức")
    public ResponseEntity<ApiResponse<List<AnnouncementDto>>> getAnnouncements() {
        List<AnnouncementDto> list = announcementService.getAnnouncements();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết thông báo theo ID")
    public ResponseEntity<ApiResponse<AnnouncementDto>> getAnnouncementById(@PathVariable Long id) {
        AnnouncementDto dto = announcementService.getAnnouncementById(id);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEAN')")
    @Operation(summary = "Đăng thông báo mới (Admin / Trưởng khoa)")
    public ResponseEntity<ApiResponse<AnnouncementDto>> createAnnouncement(@Valid @RequestBody AnnouncementRequest request) {
        AnnouncementDto created = announcementService.createAnnouncement(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Đăng thông báo thành công!", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEAN')")
    @Operation(summary = "Cập nhật thông báo")
    public ResponseEntity<ApiResponse<AnnouncementDto>> updateAnnouncement(
            @PathVariable Long id,
            @Valid @RequestBody AnnouncementRequest request) {
        AnnouncementDto updated = announcementService.updateAnnouncement(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thông báo thành công!", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEAN')")
    @Operation(summary = "Xóa thông báo")
    public ResponseEntity<ApiResponse<Void>> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa thông báo thành công!", null));
    }
}
