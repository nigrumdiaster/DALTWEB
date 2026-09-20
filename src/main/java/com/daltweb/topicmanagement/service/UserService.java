package com.daltweb.topicmanagement.service;

import com.daltweb.topicmanagement.constant.RoleType;
import com.daltweb.topicmanagement.dto.auth.UpdateUserRequest;
import com.daltweb.topicmanagement.dto.auth.UserDto;
import com.daltweb.topicmanagement.entity.Department;
import com.daltweb.topicmanagement.entity.User;
import com.daltweb.topicmanagement.exception.ConflictException;
import com.daltweb.topicmanagement.exception.ResourceNotFoundException;
import com.daltweb.topicmanagement.repository.DepartmentRepository;
import com.daltweb.topicmanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final AuthService authService;

    public UserService(UserRepository userRepository, DepartmentRepository departmentRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.authService = authService;
    }

    @Transactional(readOnly = true)
    public List<UserDto> getUsers(RoleType role, Long departmentId, String keyword) {
        return userRepository.searchUsers(role, departmentId, keyword)
                .stream()
                .map(authService::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + id));
        return authService.mapToUserDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getLecturersByDepartment(Long departmentId) {
        return userRepository.findByRoleAndDepartmentId(RoleType.ROLE_LECTURER, departmentId)
                .stream()
                .map(authService::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + id));

        if (!user.getEmail().equalsIgnoreCase(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email đã được sử dụng bởi tài khoản khác!");
        }

        if (request.getCode() != null && !request.getCode().equalsIgnoreCase(user.getCode())
                && userRepository.existsByCode(request.getCode())) {
            throw new ConflictException("Mã số đã được sử dụng bởi người dùng khác!");
        }

        if (request.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bộ môn"));
            user.setDepartment(dept);
        } else if (request.getRole() == RoleType.ROLE_STUDENT) {
            user.setDepartment(null);
        }

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setCode(request.getCode());
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getStudentClass() != null) {
            user.setStudentClass(request.getStudentClass());
        }
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }

        User updatedUser = userRepository.save(user);
        return authService.mapToUserDto(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + id));
        user.setActive(false);
        userRepository.save(user);
    }
}
