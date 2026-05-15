package vn.edu.hutech.lms_api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// Import Domain
import vn.edu.hutech.lms_api.domain.Course;
import vn.edu.hutech.lms_api.domain.Enrollment;
import vn.edu.hutech.lms_api.domain.Lesson;
import vn.edu.hutech.lms_api.domain.LessonProgress;
import vn.edu.hutech.lms_api.domain.User;

// Import DTO
import vn.edu.hutech.lms_api.dto.enrollment.EnrollmentRequestDTO;
import vn.edu.hutech.lms_api.dto.enrollment.EnrollmentResponseDTO;
import vn.edu.hutech.lms_api.dto.enrollment.EnrollmentUpdateRequestDTO;

// Import Repository
import vn.edu.hutech.lms_api.repository.CourseRepository;
import vn.edu.hutech.lms_api.repository.EnrollmentRepository;
import vn.edu.hutech.lms_api.repository.UserRepository;
import vn.edu.hutech.lms_api.repository.LessonRepository;
import vn.edu.hutech.lms_api.repository.LessonProgressRepository;

import vn.edu.hutech.lms_api.service.EnrollmentService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    // KHOẢNG TRỐNG NÀY LÀ NƠI GÂY RA LỖI NẾU BỊ THIẾU
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    // Đã khai báo thêm 2 Repository này để hết lỗi
    private final LessonRepository lessonRepository;
    private final LessonProgressRepository lessonProgressRepository;

    @Override
    public EnrollmentResponseDTO enrollCourse(EnrollmentRequestDTO requestDTO) {
        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Học viên với ID: " + requestDTO.getUserId()));

        Course course = courseRepository.findById(requestDTO.getCourseId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Khóa học với ID: " + requestDTO.getCourseId()));

        Optional<Enrollment> existingEnrollment = enrollmentRepository.findByUserIdAndCourseId(user.getId(), course.getId());
        if (existingEnrollment.isPresent()) {
            throw new RuntimeException("Học viên này đã đăng ký khóa học rồi!");
        }

        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .course(course)
                .status("IN_PROGRESS")
                .progressPercentage(0.0)
                .build();

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return mapToResponseDTO(savedEnrollment);
    }

    @Override
    public List<EnrollmentResponseDTO> getUserEnrollments(Long userId) {
        List<Enrollment> enrollments = enrollmentRepository.findByUserId(userId);
        return enrollments.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EnrollmentResponseDTO updateEnrollment(Long id, EnrollmentUpdateRequestDTO requestDTO) {
        Enrollment existingEnrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Phiếu ghi danh với ID: " + id));

        if (requestDTO.getProgressPercentage() != null) {
            existingEnrollment.setProgressPercentage(requestDTO.getProgressPercentage());
        }
        if (requestDTO.getStatus() != null) {
            existingEnrollment.setStatus(requestDTO.getStatus());
        }

        Enrollment updatedEnrollment = enrollmentRepository.save(existingEnrollment);
        return mapToResponseDTO(updatedEnrollment);
    }

    @Override
    public void deleteEnrollment(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Phiếu ghi danh với ID: " + id));
        enrollmentRepository.delete(enrollment);
    }

    // --- LOGIC TÍNH TOÁN PHẦN TRĂM TIẾN ĐỘ ---
    @Override
    public Double markLessonAsCompleted(Long enrollmentId, Long lessonId) {
        // 1. Kiểm tra phiếu ghi danh và bài học
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Phiếu ghi danh"));

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Bài học"));

        // 2. Cập nhật hoặc tạo mới LessonProgress
        LessonProgress progress = lessonProgressRepository.findByEnrollmentIdAndLessonId(enrollmentId, lessonId)
                .orElse(LessonProgress.builder()
                        .enrollment(enrollment)
                        .lesson(lesson)
                        .build());

        progress.setIsCompleted(true);
        progress.setCompletedAt(java.time.LocalDateTime.now());
        lessonProgressRepository.save(progress);

        // 3. Tính toán lại phần trăm tiến độ
        Long courseId = enrollment.getCourse().getId();
        long totalLessons = lessonRepository.countByModule_Course_Id(courseId);
        long completedLessons = lessonProgressRepository.countByEnrollmentIdAndIsCompletedTrue(enrollmentId);

        double progressPercentage = 0.0;
        if (totalLessons > 0) {
            progressPercentage = ((double) completedLessons / totalLessons) * 100;
            progressPercentage = Math.round(progressPercentage * 100.0) / 100.0; // Làm tròn 2 chữ số
        }

        enrollment.setProgressPercentage(progressPercentage);

        // 4. Cập nhật trạng thái nếu đạt 100%
        if (progressPercentage >= 100.0) {
            enrollment.setStatus("COMPLETED");
        }

        enrollmentRepository.save(enrollment);

        return progressPercentage;
    }

    private EnrollmentResponseDTO mapToResponseDTO(Enrollment enrollment) {
        return EnrollmentResponseDTO.builder()
                .id(enrollment.getId())
                .userId(enrollment.getUser().getId())
                .studentName(enrollment.getUser().getFullName())
                .courseId(enrollment.getCourse().getId())
                .courseTitle(enrollment.getCourse().getTitle())
                .enrollmentDate(enrollment.getEnrollmentDate())
                .status(enrollment.getStatus())
                .progressPercentage(enrollment.getProgressPercentage())
                .build();
    }
}