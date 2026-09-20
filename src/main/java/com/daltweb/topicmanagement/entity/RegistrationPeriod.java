package com.daltweb.topicmanagement.entity;

import com.daltweb.topicmanagement.constant.PeriodStatus;
import com.daltweb.topicmanagement.constant.PeriodType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "registration_periods")
public class RegistrationPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PeriodType type;

    @Column(length = 20)
    private String semester;

    @Column(name = "academic_year", length = 30)
    private String academicYear;

    @Column(name = "lecturer_reg_start", nullable = false)
    private LocalDateTime lecturerRegStart;

    @Column(name = "lecturer_reg_end", nullable = false)
    private LocalDateTime lecturerRegEnd;

    @Column(name = "student_reg_start", nullable = false)
    private LocalDateTime studentRegStart;

    @Column(name = "student_reg_end", nullable = false)
    private LocalDateTime studentRegEnd;

    @Column(name = "reviewer_deadline")
    private LocalDate reviewerDeadline;

    @Column(name = "defense_date")
    private LocalDate defenseDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 35)
    private PeriodStatus status = PeriodStatus.DRAFT;

    @Column(length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password"})
    private User createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public RegistrationPeriod() {}

    public RegistrationPeriod(Long id, String name, PeriodType type, String semester, String academicYear, LocalDateTime lecturerRegStart, LocalDateTime lecturerRegEnd, LocalDateTime studentRegStart, LocalDateTime studentRegEnd, LocalDate reviewerDeadline, LocalDate defenseDate, PeriodStatus status, String description, User createdBy, LocalDateTime createdAt) {
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
        this.status = status != null ? status : PeriodStatus.DRAFT;
        this.description = description;
        this.createdBy = createdBy;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
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
        private PeriodStatus status = PeriodStatus.DRAFT;
        private String description;
        private User createdBy;
        private LocalDateTime createdAt = LocalDateTime.now();

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
        public Builder createdBy(User createdBy) { this.createdBy = createdBy; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public RegistrationPeriod build() {
            return new RegistrationPeriod(id, name, type, semester, academicYear, lecturerRegStart, lecturerRegEnd, studentRegStart, studentRegEnd, reviewerDeadline, defenseDate, status, description, createdBy, createdAt);
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
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
