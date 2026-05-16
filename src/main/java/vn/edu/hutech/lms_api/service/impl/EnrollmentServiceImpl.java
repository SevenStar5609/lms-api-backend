package vn.edu.hutech.lms_api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.hutech.lms_api.domain.Course;
import vn.edu.hutech.lms_api.domain.Enrollment;
import vn.edu.hutech.lms_api.domain.User;
import vn.edu.hutech.lms_api.dto.enrollment.EnrollmentRequestDTO;
import vn.edu.hutech.lms_api.dto.enrollment.EnrollmentResponseDTO;
import vn.edu.hutech.lms_api.repository.CourseRepository;
import vn.edu.hutech.lms_api.repository.EnrollmentRepository;
import vn.edu.hutech.lms_api.repository.UserRepository;
import vn.edu.hutech.lms_api.service.EnrollmentService;
import vn.edu.hutech.lms_api.dto.enrollment.EnrollmentUpdateRequestDTO;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public EnrollmentResponseDTO enrollCourse(EnrollmentRequestDTO requestDTO) {
        // 1. Tự động lấy Email của Học viên đang đăng nhập từ JWT Token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new RuntimeException("Bạn cần đăng nhập để thực hiện ghi danh khóa học!");
        }

        String currentUserEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin tài khoản người dùng!"));

        // 2. Kiểm tra Khóa học có tồn tại hay không
        Course course = courseRepository.findById(requestDTO.getCourseId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Khóa học với ID: " + requestDTO.getCourseId()));

        // 3. Kiểm tra Học viên đã ghi danh khóa học này chưa (Tránh trùng lặp dữ liệu)
        if (enrollmentRepository.findByUserIdAndCourseId(currentUser.getId(), course.getId()).isPresent()) {
            throw new RuntimeException("Bạn đã ghi danh khóa học này trước đó rồi!");
        }

        // 4. Khởi tạo bản ghi Ghi danh mới (Tiến độ ban đầu = 0%)
        Enrollment enrollment = Enrollment.builder()
                .user(currentUser)
                .course(course)
                .progress(0.0)
                .status("ACTIVE")
                .build();

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        return mapToResponseDTO(savedEnrollment);
    }

    @Override
    public List<EnrollmentResponseDTO> getMyEnrollments() {
        // Lấy danh sách khóa học của chính học viên đang đăng nhập
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản người dùng!"));

        return enrollmentRepository.findByUserId(currentUser.getId()).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EnrollmentResponseDTO getEnrollmentById(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dữ liệu ghi danh với ID: " + id));
        return mapToResponseDTO(enrollment);
    }


    // Nhớ import EnrollmentUpdateRequestDTO ở trên cùng file nhé:
    // import vn.edu.hutech.lms_api.dto.enrollment.EnrollmentUpdateRequestDTO;

    // =========================================================
    // 4 HÀM BỔ SUNG CHO CONTROLLER (QUẢN TRỊ & CẬP NHẬT TIẾN ĐỘ)
    // =========================================================

    @Override
    public List<EnrollmentResponseDTO> getUserEnrollments(Long userId) {
        return enrollmentRepository.findByUserId(userId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EnrollmentResponseDTO updateEnrollment(Long id, EnrollmentUpdateRequestDTO requestDTO) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dữ liệu ghi danh!"));

        if (requestDTO.getStatus() != null) {
            enrollment.setStatus(requestDTO.getStatus());
        }
        if (requestDTO.getProgress() != null) {
            enrollment.setProgress(requestDTO.getProgress());
        }

        return mapToResponseDTO(enrollmentRepository.save(enrollment));
    }

    @Override
    public void deleteEnrollment(Long id) {
        enrollmentRepository.deleteById(id);
    }

    @Override
    public Double markLessonAsCompleted(Long enrollmentId, Long lessonId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dữ liệu ghi danh!"));

        double currentProgress = enrollment.getProgress() != null ? enrollment.getProgress() : 0.0;
        currentProgress += 10.0; // Tạm thời tăng 10% mỗi khi hoàn thành 1 bài

        if (currentProgress >= 100.0) {
            currentProgress = 100.0;
            enrollment.setStatus("COMPLETED");
        }

        enrollment.setProgress(currentProgress);
        enrollmentRepository.save(enrollment); // Lưu vào database

        // Chỉ trả về con số phần trăm tiến độ (Double) thay vì trả về cả DTO
        return currentProgress;
    }
    // Hàm chuyển đổi từ Entity sang DTO để trả về Client
    private EnrollmentResponseDTO mapToResponseDTO(Enrollment enrollment) {
        return EnrollmentResponseDTO.builder()
                .id(enrollment.getId())
                .userId(enrollment.getUser().getId())
                .userFullName(enrollment.getUser().getFullName())
                .courseId(enrollment.getCourse().getId())
                .courseTitle(enrollment.getCourse().getTitle())
                .progress(enrollment.getProgress())
                .status(enrollment.getStatus())
                .createdAt(enrollment.getCreatedAt())
                .build();
    }
}