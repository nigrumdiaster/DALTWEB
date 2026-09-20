package com.daltweb.topicmanagement.service;

import com.daltweb.topicmanagement.constant.RoleType;
import com.daltweb.topicmanagement.dto.auth.*;
import com.daltweb.topicmanagement.entity.Department;
import com.daltweb.topicmanagement.entity.User;
import com.daltweb.topicmanagement.exception.BadRequestException;
import com.daltweb.topicmanagement.exception.ConflictException;
import com.daltweb.topicmanagement.exception.ResourceNotFoundException;
import com.daltweb.topicmanagement.repository.DepartmentRepository;
import com.daltweb.topicmanagement.repository.UserRepository;
import com.daltweb.topicmanagement.security.CustomUserDetails;
import com.daltweb.topicmanagement.security.JwtTokenProvider;
import com.daltweb.topicmanagement.security.SecurityUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository, DepartmentRepository departmentRepository, PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin tài khoản"));

        return LoginResponse.builder()
                .token(jwt)
                .tokenType("Bearer")
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .code(user.getCode())
                .role(user.getRole())
                .departmentId(user.getDepartment() != null ? user.getDepartment().getId() : null)
                .departmentName(user.getDepartment() != null ? user.getDepartment().getName() : null)
                .studentClass(user.getStudentClass())
                .build();
    }

    @Transactional
    public UserDto register(RegisterRequest request) {
        if (request.getRole() == RoleType.ROLE_ADMIN || request.getRole() == RoleType.ROLE_DEAN || request.getRole() == RoleType.ROLE_DEPARTMENT_HEAD) {
            throw new BadRequestException("Không thể tự đăng ký các vai trò quản lý (ADMIN, DEAN, TRƯỞNG BỘ MÔN). Các tài khoản này chỉ được cấp bởi Quản trị viên!");
        }
        return processUserCreation(request);
    }

    @Transactional
    public UserDto createUserByAdmin(RegisterRequest request) {
        return processUserCreation(request);
    }

    private UserDto processUserCreation(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Tên đăng nhập đã tồn tại trong hệ thống!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email đã được sử dụng!");
        }
        if (request.getCode() != null && !request.getCode().isBlank() && userRepository.existsByCode(request.getCode())) {
            throw new ConflictException("Mã số (MSSV/MSGV) đã tồn tại trong hệ thống!");
        }

        Department department = null;
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bộ môn tương ứng"));
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .code(request.getCode())
                .role(request.getRole())
                .department(department)
                .studentClass(request.getStudentClass())
                .active(true)
                .build();

        User savedUser = userRepository.save(user);
        return mapToUserDto(savedUser);
    }

    @Transactional(readOnly = true)
    public UserDto getCurrentUserProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BadRequestException("Chưa xác thực người dùng");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
        return mapToUserDto(user);
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Mật khẩu hiện tại không chính xác!");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .code(user.getCode())
                .role(user.getRole())
                .departmentId(user.getDepartment() != null ? user.getDepartment().getId() : null)
                .departmentName(user.getDepartment() != null ? user.getDepartment().getName() : null)
                .studentClass(user.getStudentClass())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
