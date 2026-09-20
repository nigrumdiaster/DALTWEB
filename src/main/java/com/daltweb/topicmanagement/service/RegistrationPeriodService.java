package com.daltweb.topicmanagement.service;

import com.daltweb.topicmanagement.constant.PeriodStatus;
import com.daltweb.topicmanagement.constant.PeriodType;
import com.daltweb.topicmanagement.dto.period.PeriodCreateRequest;
import com.daltweb.topicmanagement.dto.period.PeriodStatusRequest;
import com.daltweb.topicmanagement.dto.period.PeriodUpdateRequest;
import com.daltweb.topicmanagement.dto.period.RegistrationPeriodDto;
import com.daltweb.topicmanagement.entity.RegistrationPeriod;
import com.daltweb.topicmanagement.entity.User;
import com.daltweb.topicmanagement.exception.BadRequestException;
import com.daltweb.topicmanagement.exception.ResourceNotFoundException;
import com.daltweb.topicmanagement.repository.RegistrationPeriodRepository;
import com.daltweb.topicmanagement.repository.StudentGroupRepository;
import com.daltweb.topicmanagement.repository.TopicRepository;
import com.daltweb.topicmanagement.repository.UserRepository;
import com.daltweb.topicmanagement.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegistrationPeriodService {

    private final RegistrationPeriodRepository periodRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final StudentGroupRepository groupRepository;

    public RegistrationPeriodService(RegistrationPeriodRepository periodRepository, UserRepository userRepository, TopicRepository topicRepository, StudentGroupRepository groupRepository) {
        this.periodRepository = periodRepository;
        this.userRepository = userRepository;
        this.topicRepository = topicRepository;
        this.groupRepository = groupRepository;
    }

    @Transactional(readOnly = true)
    public List<RegistrationPeriodDto> getAllPeriods(PeriodType type, PeriodStatus status) {
        return periodRepository.filterPeriods(type, status).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RegistrationPeriodDto getPeriodById(Long id) {
        RegistrationPeriod period = periodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đợt đăng ký với ID: " + id));
        return mapToDto(period);
    }

    @Transactional
    public RegistrationPeriodDto createPeriod(PeriodCreateRequest request) {
        validatePeriodDates(request.getType(), request.getLecturerRegStart(), request.getLecturerRegEnd(),
                request.getStudentRegStart(), request.getStudentRegEnd(),
                request.getReviewerDeadline(), request.getDefenseDate());

        Long currentUserId = SecurityUtils.getCurrentUserId();
        User currentUser = currentUserId != null ? userRepository.findById(currentUserId).orElse(null) : null;

        RegistrationPeriod period = RegistrationPeriod.builder()
                .name(request.getName())
                .type(request.getType())
                .semester(request.getSemester())
                .academicYear(request.getAcademicYear())
                .lecturerRegStart(request.getLecturerRegStart())
                .lecturerRegEnd(request.getLecturerRegEnd())
                .studentRegStart(request.getStudentRegStart())
                .studentRegEnd(request.getStudentRegEnd())
                .reviewerDeadline(request.getReviewerDeadline())
                .defenseDate(request.getDefenseDate())
                .status(PeriodStatus.DRAFT)
                .description(request.getDescription())
                .createdBy(currentUser)
                .build();

        RegistrationPeriod saved = periodRepository.save(period);
        return mapToDto(saved);
    }

    @Transactional
    public RegistrationPeriodDto updatePeriod(Long id, PeriodUpdateRequest request) {
        RegistrationPeriod period = periodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đợt đăng ký với ID: " + id));

        validatePeriodDates(request.getType(), request.getLecturerRegStart(), request.getLecturerRegEnd(),
                request.getStudentRegStart(), request.getStudentRegEnd(),
                request.getReviewerDeadline(), request.getDefenseDate());

        period.setName(request.getName());
        period.setType(request.getType());
        period.setSemester(request.getSemester());
        period.setAcademicYear(request.getAcademicYear());
        period.setLecturerRegStart(request.getLecturerRegStart());
        period.setLecturerRegEnd(request.getLecturerRegEnd());
        period.setStudentRegStart(request.getStudentRegStart());
        period.setStudentRegEnd(request.getStudentRegEnd());
        period.setReviewerDeadline(request.getReviewerDeadline());
        period.setDefenseDate(request.getDefenseDate());
        period.setDescription(request.getDescription());

        RegistrationPeriod updated = periodRepository.save(period);
        return mapToDto(updated);
    }

    @Transactional
    public RegistrationPeriodDto updatePeriodStatus(Long id, PeriodStatusRequest request) {
        RegistrationPeriod period = periodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đợt đăng ký với ID: " + id));

        period.setStatus(request.getStatus());
        RegistrationPeriod updated = periodRepository.save(period);
        return mapToDto(updated);
    }

    @Transactional
    public void deletePeriod(Long id) {
        if (!periodRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy đợt đăng ký với ID: " + id);
        }
        periodRepository.deleteById(id);
    }

    private void validatePeriodDates(PeriodType type,
                                    java.time.LocalDateTime lecStart,
                                    java.time.LocalDateTime lecEnd,
                                    java.time.LocalDateTime stuStart,
                                    java.time.LocalDateTime stuEnd,
                                    java.time.LocalDate reviewerDeadline,
                                    java.time.LocalDate defenseDate) {
        if (lecStart.isAfter(lecEnd)) {
            throw new BadRequestException("Thời gian bắt đầu đăng ký của GV phải trước thời gian kết thúc của GV");
        }
        if (stuStart.isAfter(stuEnd)) {
            throw new BadRequestException("Thời gian bắt đầu đăng ký của SV phải trước thời gian kết thúc của SV");
        }
        if (lecStart.isAfter(stuStart)) {
            throw new BadRequestException("Giai đoạn 1 (GV đăng ký) phải bắt đầu trước hoặc bằng Giai đoạn 2 (SV đăng ký)");
        }

        if (type == PeriodType.SPECIALIZED_PROJECT || type == PeriodType.GRADUATION_THESIS) {
            if (reviewerDeadline == null) {
                throw new BadRequestException("Đợt đăng ký loại Tiểu luận chuyên ngành (TLCN) hoặc Khóa luận tốt nghiệp (KLTN) bắt buộc phải thiết lập hạn chót GVPB nộp điểm!");
            }
        }

        if (type == PeriodType.GRADUATION_THESIS) {
            if (defenseDate == null) {
                throw new BadRequestException("Đợt đăng ký loại Khóa luận tốt nghiệp (KLTN) bắt buộc phải thiết lập ngày báo cáo hội đồng!");
            }
        }
    }

    public RegistrationPeriodDto mapToDto(RegistrationPeriod period) {
        long topicCount = topicRepository.findByPeriodId(period.getId()).size();
        long groupCount = groupRepository.findByPeriodId(period.getId()).size();

        return RegistrationPeriodDto.builder()
                .id(period.getId())
                .name(period.getName())
                .type(period.getType())
                .semester(period.getSemester())
                .academicYear(period.getAcademicYear())
                .lecturerRegStart(period.getLecturerRegStart())
                .lecturerRegEnd(period.getLecturerRegEnd())
                .studentRegStart(period.getStudentRegStart())
                .studentRegEnd(period.getStudentRegEnd())
                .reviewerDeadline(period.getReviewerDeadline())
                .defenseDate(period.getDefenseDate())
                .status(period.getStatus())
                .description(period.getDescription())
                .createdById(period.getCreatedBy() != null ? period.getCreatedBy().getId() : null)
                .createdByName(period.getCreatedBy() != null ? period.getCreatedBy().getFullName() : null)
                .createdAt(period.getCreatedAt())
                .topicCount(topicCount)
                .groupCount(groupCount)
                .build();
    }
}
