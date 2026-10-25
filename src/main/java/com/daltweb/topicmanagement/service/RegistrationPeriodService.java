package com.daltweb.topicmanagement.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import com.daltweb.topicmanagement.constant.PeriodStatus;
import com.daltweb.topicmanagement.constant.PeriodType;
import com.daltweb.topicmanagement.constant.TopicApprovalStatus;
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
import com.daltweb.topicmanagement.repository.PeriodCount;
import com.daltweb.topicmanagement.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RegistrationPeriodService {

    private final RegistrationPeriodRepository periodRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final StudentGroupRepository groupRepository;
    private final ResultValidationService resultValidation;

    public RegistrationPeriodService(RegistrationPeriodRepository periodRepository, UserRepository userRepository, TopicRepository topicRepository, StudentGroupRepository groupRepository,
            ResultValidationService resultValidation) {
        this.periodRepository = periodRepository;
        this.userRepository = userRepository;
        this.topicRepository = topicRepository;
        this.groupRepository = groupRepository;
        this.resultValidation = resultValidation;
    }

    @Transactional(readOnly = true)
    public Page<RegistrationPeriodDto> getAllPeriods(PeriodType type, PeriodStatus status, String keyword, boolean excludeDraft, Pageable pageable) {
        return mapPage(periodRepository.filterPeriods(type, status, "%" + (keyword == null ? "" : keyword.toLowerCase(java.util.Locale.ROOT)) + "%", excludeDraft, pageable));
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

        if (period.getStatus() != PeriodStatus.DRAFT && period.getType() != request.getType()) {
            throw new BadRequestException("Không được đổi loại đợt sau khi đã mở đăng ký!");
        }

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
        RegistrationPeriod period = periodRepository.findForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đợt đăng ký với ID: " + id));

        PeriodStatus current = period.getStatus();
        PeriodStatus target = request.getStatus();
        if (target == null) throw new BadRequestException("Trạng thái không được để trống!");
        if (current == PeriodStatus.COMPLETED || current == PeriodStatus.CANCELLED) {
            throw new BadRequestException("Đợt đã kết thúc, không thể đổi trạng thái!");
        }
        PeriodStatus next = switch (current) {
            case DRAFT -> PeriodStatus.PHASE1_LECTURER_REGISTRATION;
            case PHASE1_LECTURER_REGISTRATION -> PeriodStatus.PHASE1_REVIEWING;
            case PHASE1_REVIEWING -> PeriodStatus.PHASE2_STUDENT_REGISTRATION;
            case PHASE2_STUDENT_REGISTRATION -> PeriodStatus.IN_PROGRESS;
            case IN_PROGRESS -> PeriodStatus.REVIEWING;
            case REVIEWING -> period.getType() == PeriodType.GRADUATION_THESIS
                    ? PeriodStatus.DEFENSE : PeriodStatus.COMPLETED;
            case DEFENSE -> PeriodStatus.COMPLETED;
            default -> null;
        };
        if (target != PeriodStatus.CANCELLED && target != next) {
            throw new BadRequestException("Không được bỏ qua hoặc quay lại giai đoạn. Trạng thái tiếp theo: " + next);
        }
        if (target == PeriodStatus.PHASE2_STUDENT_REGISTRATION) {
            var topics = topicRepository.findByPeriodId(id);
            if (topics.stream().noneMatch(t -> t.getApprovalStatus() == TopicApprovalStatus.APPROVED)
                    || topics.stream().anyMatch(t -> t.getApprovalStatus() == TopicApprovalStatus.PENDING)) {
                throw new BadRequestException("Phải duyệt xong danh sách đề tài và có ít nhất một đề tài được duyệt trước khi mở GĐ2!");
            }
        }
        if (target == PeriodStatus.COMPLETED) resultValidation.validatePeriodResults(period);
        period.setStatus(target);
        RegistrationPeriod updated = periodRepository.save(period);
        return mapToDto(updated);
    }

    @Transactional
    public void deletePeriod(Long id) {
        if (!periodRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy đợt đăng ký với ID: " + id);
        }
        if (topicRepository.existsByPeriodId(id) || groupRepository.existsByPeriodId(id)) {
            throw new BadRequestException("Đợt đăng ký này đã phát sinh đề tài hoặc nhóm sinh viên, không thể xóa!");
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
        if (!lecStart.isBefore(lecEnd)) {
            throw new BadRequestException("Thời gian bắt đầu đăng ký của GV phải trước thời gian kết thúc của GV");
        }
        if (!stuStart.isBefore(stuEnd)) {
            throw new BadRequestException("Thời gian bắt đầu đăng ký của SV phải trước thời gian kết thúc của SV");
        }
        if (lecEnd.isAfter(stuStart)) {
            throw new BadRequestException("Giai đoạn 1 phải kết thúc trước hoặc bằng thời gian bắt đầu Giai đoạn 2!");
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
        long topicCount = topicRepository.countByPeriodId(period.getId());
        long groupCount = groupRepository.countByPeriodId(period.getId());
        return mapToDto(period, topicCount, groupCount);
    }

    public Page<RegistrationPeriodDto> mapPage(Page<RegistrationPeriod> periods) {
        if (periods.isEmpty()) return periods.map(p -> mapToDto(p, 0, 0));
        List<Long> ids = periods.map(RegistrationPeriod::getId).getContent();
        Map<Long, Long> topics = topicRepository.countForPeriods(ids).stream()
                .collect(Collectors.toMap(PeriodCount::getPeriodId, PeriodCount::getTotal));
        Map<Long, Long> groups = groupRepository.countForPeriods(ids).stream()
                .collect(Collectors.toMap(PeriodCount::getPeriodId, PeriodCount::getTotal));
        return periods.map(p -> mapToDto(p, topics.getOrDefault(p.getId(), 0L), groups.getOrDefault(p.getId(), 0L)));
    }

    private RegistrationPeriodDto mapToDto(RegistrationPeriod period, long topicCount, long groupCount) {

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
