package com.daltweb.topicmanagement.seeder;

import com.daltweb.topicmanagement.constant.*;
import com.daltweb.topicmanagement.entity.*;
import com.daltweb.topicmanagement.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final RegistrationPeriodRepository periodRepository;
    private final TopicRepository topicRepository;
    private final StudentGroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final AnnouncementRepository announcementRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, DepartmentRepository departmentRepository, RegistrationPeriodRepository periodRepository, TopicRepository topicRepository, StudentGroupRepository groupRepository, GroupMemberRepository groupMemberRepository, AnnouncementRepository announcementRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.periodRepository = periodRepository;
        this.topicRepository = topicRepository;
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.announcementRepository = announcementRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        log.info("--> Bắt đầu khởi tạo dữ liệu mẫu cho hệ thống Quản lý Đề tài Khoa CNTT...");

        String defaultPass = passwordEncoder.encode("password123");

        // 1. Admin & Trưởng khoa
        User admin = userRepository.save(User.builder()
                .username("admin")
                .password(defaultPass)
                .fullName("Quản trị viên Hệ thống")
                .email("admin@fit.edu.vn")
                .phone("0901000001")
                .code("ADMIN01")
                .role(RoleType.ROLE_ADMIN)
                .active(true)
                .build());

        User dean = userRepository.save(User.builder()
                .username("truongkhoa")
                .password(defaultPass)
                .fullName("PGS.TS. Trần Đình Khoa")
                .email("deancntt@fit.edu.vn")
                .phone("0901000002")
                .code("GV001")
                .role(RoleType.ROLE_DEAN)
                .active(true)
                .build());

        // 2. Trưởng bộ môn & Bộ môn
        User headCnpm = userRepository.save(User.builder()
                .username("truongbomon_cnpm")
                .password(defaultPass)
                .fullName("TS. Lê Hoàng Phần Mềm")
                .email("head_cnpm@fit.edu.vn")
                .phone("0901000003")
                .code("GV002")
                .role(RoleType.ROLE_DEPARTMENT_HEAD)
                .active(true)
                .build());

        User headHttt = userRepository.save(User.builder()
                .username("truongbomon_httt")
                .password(defaultPass)
                .fullName("TS. Nguyễn Thị Hệ Thống")
                .email("head_httt@fit.edu.vn")
                .phone("0901000004")
                .code("GV003")
                .role(RoleType.ROLE_DEPARTMENT_HEAD)
                .active(true)
                .build());

        Department deptCnpm = departmentRepository.save(Department.builder()
                .code("CNPM")
                .name("Bộ môn Công nghệ phần mềm")
                .description("Đào tạo và nghiên cứu kỹ nghệ phần mềm, kiến trúc hệ thống, phát triển ứng dụng di động & web")
                .headLecturer(headCnpm)
                .build());

        Department deptHttt = departmentRepository.save(Department.builder()
                .code("HTTT")
                .name("Bộ môn Hệ thống thông tin")
                .description("Đào tạo và nghiên cứu cơ sở dữ liệu lớn, phân tích dữ liệu kinh doanh, ERP và BI")
                .headLecturer(headHttt)
                .build());

        Department deptKhmt = departmentRepository.save(Department.builder()
                .code("KHMT")
                .name("Bộ môn Khoa học máy tính")
                .description("Đào tạo và nghiên cứu trí tuệ nhân tạo (AI), học máy, xử lý ảnh & ngôn ngữ tự nhiên")
                .build());

        Department deptMmt = departmentRepository.save(Department.builder()
                .code("MMT_ANM")
                .name("Bộ môn Mạng máy tính & An toàn thông tin")
                .description("Đào tạo và nghiên cứu an ninh mạng, điện toán đám mây và hệ thống phân tán")
                .build());

        headCnpm.setDepartment(deptCnpm);
        userRepository.save(headCnpm);
        headHttt.setDepartment(deptHttt);
        userRepository.save(headHttt);

        // 3. Giảng viên
        User gv1 = userRepository.save(User.builder()
                .username("gv_nguyenvana")
                .password(defaultPass)
                .fullName("ThS. Nguyễn Văn An")
                .email("an.nv@fit.edu.vn")
                .phone("0902000001")
                .code("GV004")
                .role(RoleType.ROLE_LECTURER)
                .department(deptCnpm)
                .active(true)
                .build());

        User gv2 = userRepository.save(User.builder()
                .username("gv_tranthib")
                .password(defaultPass)
                .fullName("TS. Trần Thị Bình")
                .email("binh.tt@fit.edu.vn")
                .phone("0902000002")
                .code("GV005")
                .role(RoleType.ROLE_LECTURER)
                .department(deptCnpm)
                .active(true)
                .build());

        User gv3 = userRepository.save(User.builder()
                .username("gv_lequangc")
                .password(defaultPass)
                .fullName("ThS. Lê Quang Cường")
                .email("cuong.lq@fit.edu.vn")
                .phone("0902000003")
                .code("GV006")
                .role(RoleType.ROLE_LECTURER)
                .department(deptHttt)
                .active(true)
                .build());

        User gv4 = userRepository.save(User.builder()
                .username("gv_hoangd")
                .password(defaultPass)
                .fullName("TS. Hoàng Đức Dũng")
                .email("dung.hd@fit.edu.vn")
                .phone("0902000004")
                .code("GV007")
                .role(RoleType.ROLE_LECTURER)
                .department(deptKhmt)
                .active(true)
                .build());

        // 4. Sinh viên
        User sv1 = userRepository.save(User.builder()
                .username("sv_nguyen1")
                .password(defaultPass)
                .fullName("Nguyễn Văn Minh")
                .email("20110001@student.edu.vn")
                .phone("0933000001")
                .code("20110001")
                .role(RoleType.ROLE_STUDENT)
                .studentClass("20DTH01")
                .active(true)
                .build());

        User sv2 = userRepository.save(User.builder()
                .username("sv_tran2")
                .password(defaultPass)
                .fullName("Trần Thị Ngọc")
                .email("20110002@student.edu.vn")
                .phone("0933000002")
                .code("20110002")
                .role(RoleType.ROLE_STUDENT)
                .studentClass("20DTH01")
                .active(true)
                .build());

        User sv3 = userRepository.save(User.builder()
                .username("sv_le3")
                .password(defaultPass)
                .fullName("Lê Quang Huy")
                .email("20110003@student.edu.vn")
                .phone("0933000003")
                .code("20110003")
                .role(RoleType.ROLE_STUDENT)
                .studentClass("20DTH02")
                .active(true)
                .build());

        User sv4 = userRepository.save(User.builder()
                .username("sv_pham4")
                .password(defaultPass)
                .fullName("Phạm Đức Thành")
                .email("20110004@student.edu.vn")
                .phone("0933000004")
                .code("20110004")
                .role(RoleType.ROLE_STUDENT)
                .studentClass("20DTH02")
                .active(true)
                .build());

        // 5. Đợt đăng ký mẫu
        LocalDateTime now = LocalDateTime.now();
        RegistrationPeriod periodKltn = periodRepository.save(RegistrationPeriod.builder()
                .name("Đợt Đăng ký Khóa luận Tốt nghiệp Học kỳ 1 (2026-2027)")
                .type(PeriodType.GRADUATION_THESIS)
                .semester("HK1")
                .academicYear("2026-2027")
                .lecturerRegStart(now.minusDays(10))
                .lecturerRegEnd(now.plusDays(5))
                .studentRegStart(now.minusDays(2))
                .studentRegEnd(now.plusDays(15))
                .reviewerDeadline(LocalDate.now().plusMonths(2))
                .defenseDate(LocalDate.now().plusMonths(3))
                .status(PeriodStatus.PHASE2_STUDENT_REGISTRATION)
                .description("Đợt thực hiện khóa luận tốt nghiệp dành cho sinh viên năm cuối khoa CNTT")
                .createdBy(dean)
                .build());

        RegistrationPeriod periodTlcn = periodRepository.save(RegistrationPeriod.builder()
                .name("Đợt Đăng ký Tiểu luận Chuyên ngành Học kỳ 1 (2026-2027)")
                .type(PeriodType.SPECIALIZED_PROJECT)
                .semester("HK1")
                .academicYear("2026-2027")
                .lecturerRegStart(now.minusDays(5))
                .lecturerRegEnd(now.plusDays(10))
                .studentRegStart(now.plusDays(11))
                .studentRegEnd(now.plusDays(25))
                .reviewerDeadline(LocalDate.now().plusMonths(2))
                .status(PeriodStatus.PHASE1_LECTURER_REGISTRATION)
                .description("Đợt đăng ký đề tài tiểu luận chuyên ngành cho sinh viên năm 3")
                .createdBy(dean)
                .build());

        // 6. Đề tài mẫu
        Topic topic1 = topicRepository.save(Topic.builder()
                .code("DT-CNPM-001")
                .titleVi("Xây dựng Hệ thống Quản lý Đề tài Tốt nghiệp trực tuyến với Spring Boot và React")
                .titleEn("Building Online Thesis Management System using Spring Boot and React")
                .description("Hệ thống tự động hóa quy trình đăng ký đề tài, duyệt đề tài, ghép nhóm, phân công GVPB và chấm điểm bảo vệ khóa luận trực tuyến.")
                .requirements("Nắm vững Java Spring Boot, RESTful API, React/TypeScript, cơ sở dữ liệu quan hệ.")
                .maxStudents(3)
                .department(deptCnpm)
                .period(periodKltn)
                .primaryAdvisor(gv1)
                .coAdvisor(gv2)
                .approvalStatus(TopicApprovalStatus.APPROVED)
                .approvedBy(headCnpm)
                .approvedAt(now.minusDays(3))
                .isAssigned(true)
                .build());

        Topic topic2 = topicRepository.save(Topic.builder()
                .code("DT-CNPM-002")
                .titleVi("Ứng dụng Microservices và DevOps trong triển khai Sàn Thương mại Điện tử")
                .titleEn("Microservices and DevOps Implementation in E-Commerce Platform")
                .description("Thiết kế kiến trúc microservices chịu tải cao, tích hợp CI/CD với Docker và Kubernetes.")
                .requirements("Kiến thức về Microservices, Spring Cloud, Docker, Kafka, Redis.")
                .maxStudents(3)
                .department(deptCnpm)
                .period(periodKltn)
                .primaryAdvisor(gv2)
                .approvalStatus(TopicApprovalStatus.APPROVED)
                .approvedBy(headCnpm)
                .approvedAt(now.minusDays(2))
                .isAssigned(false)
                .build());

        Topic topic3 = topicRepository.save(Topic.builder()
                .code("DT-HTTT-001")
                .titleVi("Hệ thống Phân tích Dữ liệu Chuỗi cung ứng Thông minh sử dụng Apache Spark và PowerBI")
                .titleEn("Smart Supply Chain Analytics System using Apache Spark and PowerBI")
                .description("Xây dựng kho dữ liệu (Data Warehouse) và pipeline ETL để phân tích và tối ưu hóa chuỗi cung ứng.")
                .requirements("SQL nâng cao, Data Warehousing, Big Data, PowerBI.")
                .maxStudents(2)
                .department(deptHttt)
                .period(periodKltn)
                .primaryAdvisor(gv3)
                .approvalStatus(TopicApprovalStatus.APPROVED)
                .approvedBy(headHttt)
                .approvedAt(now.minusDays(2))
                .isAssigned(false)
                .build());

        // 7. Nhóm sinh viên & Đăng ký đề tài
        StudentGroup group1 = StudentGroup.builder()
                .code("GRP-1-001")
                .name("Nhóm 01 - CloudTech")
                .period(periodKltn)
                .topic(topic1)
                .status(GroupStatus.REGISTERED)
                .members(new ArrayList<>())
                .build();
        StudentGroup savedGroup1 = groupRepository.save(group1);

        GroupMember leader1 = groupMemberRepository.save(GroupMember.builder()
                .group(savedGroup1)
                .student(sv1)
                .groupRole(GroupRole.LEADER)
                .build());

        GroupMember member2 = groupMemberRepository.save(GroupMember.builder()
                .group(savedGroup1)
                .student(sv2)
                .groupRole(GroupRole.MEMBER)
                .build());

        savedGroup1.getMembers().add(leader1);
        savedGroup1.getMembers().add(member2);

        // 8. Thông báo
        announcementRepository.save(Announcement.builder()
                .title("Thông báo v/v Mở đợt đăng ký Khóa luận tốt nghiệp Học kỳ 1 (2026-2027)")
                .content("Khoa Công nghệ Thông tin thông báo đến toàn thể sinh viên đủ điều kiện làm KLTN: Hệ thống bắt đầu mở đăng ký chọn đề tài từ ngày hôm nay. Đề nghị các nhóm sinh viên hoàn thành ghép nhóm và đăng ký đề tài đúng thời hạn quy định.")
                .isPinned(true)
                .author(dean)
                .build());

        announcementRepository.save(Announcement.builder()
                .title("Quy định và Tiêu chí Đánh giá Khóa luận Tốt nghiệp & TLCN")
                .content("Hướng dẫn chi tiết về cấu trúc quyển báo cáo, slide trình bày và các tiêu chí chấm điểm của Hội đồng phản biện.")
                .isPinned(false)
                .targetRole(RoleType.ROLE_STUDENT)
                .author(admin)
                .build());

        log.info("--> Khởi tạo dữ liệu mẫu hoàn tất thành công!");
        log.info("--> Swagger UI Documentation: http://localhost:8080/swagger-ui.html");
        log.info("--> Tài khoản: admin/password123, truongkhoa/password123, gv_nguyenvana/password123, sv_nguyen1/password123");
    }
}
