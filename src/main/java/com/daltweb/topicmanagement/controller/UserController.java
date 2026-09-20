package com.daltweb.topicmanagement.controller;

import com.daltweb.topicmanagement.constant.RoleType;
import com.daltweb.topicmanagement.dto.auth.RegisterRequest;
import com.daltweb.topicmanagement.dto.auth.UpdateUserRequest;
import com.daltweb.topicmanagement.dto.auth.UserDto;
import com.daltweb.topicmanagement.dto.common.ApiResponse;
import com.daltweb.topicmanagement.service.AuthService;
import com.daltweb.topicmanagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "2. User Management", description = "Quản lý người dùng, phân quyền, danh sách giảng viên & sinh viên")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEAN', 'DEPARTMENT_HEAD', 'LECTURER')")
    @Operation(summary = "Danh sách người dùng", description = "Lọc người dùng theo vai trò, bộ môn hoặc từ khóa")
    public ResponseEntity<ApiResponse<List<UserDto>>> getUsers(
            @RequestParam(required = false) RoleType role,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String keyword) {
        List<UserDto> users = userService.getUsers(role, departmentId, keyword);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết người dùng theo ID")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable Long id) {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @GetMapping("/lecturers/department/{departmentId}")
    @Operation(summary = "Danh sách giảng viên theo bộ môn")
    public ResponseEntity<ApiResponse<List<UserDto>>> getLecturersByDepartment(@PathVariable Long departmentId) {
        List<UserDto> lecturers = userService.getLecturersByDepartment(departmentId);
        return ResponseEntity.ok(ApiResponse.success(lecturers));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEAN')")
    @Operation(summary = "Tạo người dùng mới (Dành cho Admin/Trưởng khoa)")
    public ResponseEntity<ApiResponse<UserDto>> createUser(@Valid @RequestBody RegisterRequest request) {
        UserDto created = authService.createUserByAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo người dùng thành công!", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEAN')")
    @Operation(summary = "Cập nhật thông tin người dùng")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        UserDto updated = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thông tin thành công!", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEAN')")
    @Operation(summary = "Khóa/Xóa tài khoản người dùng")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("Vô hiệu hóa tài khoản thành công!", null));
    }
}
