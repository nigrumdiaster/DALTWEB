package com.daltweb.topicmanagement.dto.period;

import com.daltweb.topicmanagement.constant.PeriodStatus;
import com.daltweb.topicmanagement.constant.PeriodType;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class RegistrationPeriodDto {
    private Long id;
    private String name;
    private PeriodType type;
    private String semester;
    private String academicYear;
    private LocalDateTime lecturerRegStart;
    private LocalDateTime lecturerRegEnd;
    private LocalDateTime studentRegStart;
    private LocalDateTime studentRegEnd;
    private LocalDate reviewerDeadline;
    private LocalDate defenseDate;
    private PeriodStatus status;
    private String description;
    private Long createdById;
    private String createdByName;
    private LocalDateTime createdAt;
    private Long topicCount;
    private Long groupCount;

    public RegistrationPeriodDto() {}

    public RegistrationPeriodDto(Long id, String name, PeriodType type, String semester, String academicYear, LocalDateTime lecturerRegStart, LocalDateTime lecturerRegEnd, LocalDateTime studentRegStart, LocalDateTime studentRegEnd, LocalDate reviewerDeadline, LocalDate defenseDate, PeriodStatus status, String description, Long createdById, String createdByName, LocalDateTime createdAt, Long topicCount, Long groupCount) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.semester = semester;
        this.academicYear = academicYear;
        this.lecturerRegStart = lecturerRegStart;
        this.lecturerRegEnd = lecturerRegEnd;
        this.studentRegStart = studentRegStart;
        this.studentRegEnd = studentRegEnd;
        this.reviewerDeadline = reviewerDeadline;
        this.defenseDate = defenseDate;
        this.status = status;
        this.description = description;
        this.createdById = createdById;
        this.createdByName = createdByName;
        this.createdAt = createdAt;
        this.topicCount = topicCount;
        this.groupCount = groupCount;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String name;
        private PeriodType type;
        private String semester;
        private String academicYear;
        private LocalDateTime lecturerRegStart;
        private LocalDateTime lecturerRegEnd;
        private LocalDateTime studentRegStart;
        private LocalDateTime studentRegEnd;
        private LocalDate reviewerDeadline;
        private LocalDate defenseDate;
        private PeriodStatus status;
        private String description;
        private Long createdById;
        private String createdByName;
        private LocalDateTime createdAt;
        private Long topicCount;
        private Long groupCount;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder type(PeriodType type) { this.type = type; return this; }
        public Builder semester(String semester) { this.semester = semester; return this; }
        public Builder academicYear(String academicYear) { this.academicYear = academicYear; return this; }
        public Builder lecturerRegStart(LocalDateTime lecturerRegStart) { this.lecturerRegStart = lecturerRegStart; return this; }
        public Builder lecturerRegEnd(LocalDateTime lecturerRegEnd) { this.lecturerRegEnd = lecturerRegEnd; return this; }
        public Builder studentRegStart(LocalDateTime studentRegStart) { this.studentRegStart = studentRegStart; return this; }
        public Builder studentRegEnd(LocalDateTime studentRegEnd) { this.studentRegEnd = studentRegEnd; return this; }
        public Builder reviewerDeadline(LocalDate reviewerDeadline) { this.reviewerDeadline = reviewerDeadline; return this; }
        public Builder defenseDate(LocalDate defenseDate) { this.defenseDate = defenseDate; return this; }
        public Builder status(PeriodStatus status) { this.status = status; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder createdById(Long createdById) { this.createdById = createdById; return this; }
        public Builder createdByName(String createdByName) { this.createdByName = createdByName; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder topicCount(Long topicCount) { this.topicCount = topicCount; return this; }
        public Builder groupCount(Long groupCount) { this.groupCount = groupCount; return this; }
        public RegistrationPeriodDto build() {
            return new RegistrationPeriodDto(id, name, type, semester, academicYear, lecturerRegStart, lecturerRegEnd, studentRegStart, studentRegEnd, reviewerDeadline, defenseDate, status, description, createdById, createdByName, createdAt, topicCount, groupCount);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public PeriodStatus getStatus() { return status; }
    public void setStatus(PeriodStatus status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getCreatedById() { return createdById; }
    public void setCreatedById(Long createdById) { this.createdById = createdById; }
    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Long getTopicCount() { return topicCount; }
    public void setTopicCount(Long topicCount) { this.topicCount = topicCount; }
    public Long getGroupCount() { return groupCount; }
    public void setGroupCount(Long groupCount) { this.groupCount = groupCount; }
}
