package com.daltweb.topicmanagement.service;

import com.daltweb.topicmanagement.dto.department.DepartmentDto;
import com.daltweb.topicmanagement.dto.department.DepartmentRequest;
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
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    public DepartmentService(DepartmentRepository departmentRepository, UserRepository userRepository) {
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<DepartmentDto> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DepartmentDto getDepartmentById(Long id) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bộ môn với ID: " + id));
        return mapToDto(dept);
    }

    @Transactional
    public DepartmentDto createDepartment(DepartmentRequest request) {
        if (departmentRepository.existsByCode(request.getCode())) {
            throw new ConflictException("Mã bộ môn đã tồn tại: " + request.getCode());
        }

        User headLecturer = null;
        if (request.getHeadLecturerId() != null) {
            headLecturer = userRepository.findById(request.getHeadLecturerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên trưởng bộ môn"));
        }

        Department dept = Department.builder()
                .code(request.getCode().toUpperCase())
                .name(request.getName())
                .description(request.getDescription())
                .headLecturer(headLecturer)
                .build();

        Department saved = departmentRepository.save(dept);
        return mapToDto(saved);
    }

    @Transactional
    public DepartmentDto updateDepartment(Long id, DepartmentRequest request) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bộ môn với ID: " + id));

        if (!dept.getCode().equalsIgnoreCase(request.getCode()) && departmentRepository.existsByCode(request.getCode())) {
            throw new ConflictException("Mã bộ môn đã tồn tại: " + request.getCode());
        }

        User headLecturer = null;
        if (request.getHeadLecturerId() != null) {
            headLecturer = userRepository.findById(request.getHeadLecturerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên trưởng bộ môn"));
        }

        dept.setCode(request.getCode().toUpperCase());
        dept.setName(request.getName());
        dept.setDescription(request.getDescription());
        dept.setHeadLecturer(headLecturer);

        Department updated = departmentRepository.save(dept);
        return mapToDto(updated);
    }

    @Transactional
    public void deleteDepartment(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy bộ môn với ID: " + id);
        }
        departmentRepository.deleteById(id);
    }

    public DepartmentDto mapToDto(Department dept) {
        return DepartmentDto.builder()
                .id(dept.getId())
                .code(dept.getCode())
                .name(dept.getName())
                .description(dept.getDescription())
                .headLecturerId(dept.getHeadLecturer() != null ? dept.getHeadLecturer().getId() : null)
                .headLecturerName(dept.getHeadLecturer() != null ? dept.getHeadLecturer().getFullName() : null)
                .createdAt(dept.getCreatedAt())
                .build();
    }
}
