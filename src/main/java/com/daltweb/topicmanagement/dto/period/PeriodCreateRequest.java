package com.daltweb.topicmanagement.dto.period;

import com.daltweb.topicmanagement.constant.PeriodType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PeriodCreateRequest {

    @NotBlank(message = "Tên đợt đăng ký không được để trống")
    private String name;

    @NotNull(message = "Loại đợt đăng ký không được để trống")
    private PeriodType type;

    private String semester;
    private String academicYear;

    @NotNull(message = "Thời gian bắt đầu GV đăng ký đề tài không được để trống")
    private LocalDateTime lecturerRegStart;

    @NotNull(message = "Thời gian kết thúc GV đăng ký đề tài không được để trống")
    private LocalDateTime lecturerRegEnd;

    @NotNull(message = "Thời gian bắt đầu SV đăng ký đề tài không được để trống")
    private LocalDateTime studentRegStart;

    @NotNull(message = "Thời gian kết thúc SV đăng ký đề tài không được để trống")
    private LocalDateTime studentRegEnd;

    private LocalDate reviewerDeadline;
    private LocalDate defenseDate;
    private String description;

    public PeriodCreateRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public PeriodType getType() { return type; }
    public void setType(PeriodType type) { this.type = type; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    public LocalDateTime getLecturerRegStart() { return lecturerRegStart; }
    public void setLecturerRegStart(LocalDateTime lecturerRegStart) { this.lecturerRegStart = lecturerRegStart; }
    public LocalDateTime getLecturerRegEnd() { return lecturerRegEnd; }
    public void setLecturerRegEnd(LocalDateTime lecturerRegEnd) { this.lecturerRegEnd = lecturerRegEnd; }
    public LocalDateTime getStudentRegStart() { return studentRegStart; }
    public void setStudentRegStart(LocalDateTime studentRegStart) { this.studentRegStart = studentRegStart; }
    public LocalDateTime getStudentRegEnd() { return studentRegEnd; }
    public void setStudentRegEnd(LocalDateTime studentRegEnd) { this.studentRegEnd = studentRegEnd; }
    public LocalDate getReviewerDeadline() { return reviewerDeadline; }
    public void setReviewerDeadline(LocalDate reviewerDeadline) { this.reviewerDeadline = reviewerDeadline; }
    public LocalDate getDefenseDate() { return defenseDate; }
    public void setDefenseDate(LocalDate defenseDate) { this.defenseDate = defenseDate; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
