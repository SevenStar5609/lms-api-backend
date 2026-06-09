package vn.edu.hutech.lms_api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.hutech.lms_api.domain.Course;
import vn.edu.hutech.lms_api.domain.Enrollment;
import vn.edu.hutech.lms_api.domain.Lesson;
import vn.edu.hutech.lms_api.domain.LessonProgress;
import vn.edu.hutech.lms_api.domain.User;
import vn.edu.hutech.lms_api.dto.enrollment.EnrollmentRequestDTO;
import vn.edu.hutech.lms_api.dto.enrollment.EnrollmentResponseDTO;
import vn.edu.hutech.lms_api.dto.enrollment.EnrollmentUpdateRequestDTO;
import vn.edu.hutech.lms_api.exception.ForbiddenOperationException;
import vn.edu.hutech.lms_api.repository.CourseRepository;
import vn.edu.hutech.lms_api.repository.EnrollmentRepository;
import vn.edu.hutech.lms_api.repository.LessonProgressRepository;
import vn.edu.hutech.lms_api.repository.LessonRepository;
import vn.edu.hutech.lms_api.repository.UserRepository;
import vn.edu.hutech.lms_api.service.EnrollmentService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final LessonProgressRepository lessonProgressRepository;

    @Override
    @Transactional
    public EnrollmentResponseDTO enrollCourse(EnrollmentRequestDTO requestDTO) {
        User currentUser = getCurrentUser();

        Course course = courseRepository.findById(requestDTO.getCourseId())
                .orElseThrow(() -> new RuntimeException("Khong tim thay khoa hoc voi ID: " + requestDTO.getCourseId()));

        if (enrollmentRepository.findByUserIdAndCourseId(currentUser.getId(), course.getId()).isPresent()) {
            throw new RuntimeException("Ban da ghi danh khoa hoc nay truoc do roi!");
        }

        Enrollment enrollment = Enrollment.builder()
                .user(currentUser)
                .course(course)
                .progress(0.0)
                .status("ACTIVE")
                .build();

        return mapToResponseDTO(enrollmentRepository.save(enrollment));
    }

    @Override
    public Page<EnrollmentResponseDTO> getMyEnrollments(Pageable pageable) {
        User currentUser = getCurrentUser();
        return enrollmentRepository.findByUserId(currentUser.getId(), pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    public EnrollmentResponseDTO getEnrollmentById(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay du lieu ghi danh voi ID: " + id));
        return mapToResponseDTO(enrollment);
    }

    @Override
    public Page<EnrollmentResponseDTO> getUserEnrollments(Long userId, Pageable pageable) {
        requireAdminOrInstructor();
        return enrollmentRepository.findByUserId(userId, pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    public EnrollmentResponseDTO updateEnrollment(Long id, EnrollmentUpdateRequestDTO requestDTO) {
        requireAdminOrInstructor();
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay du lieu ghi danh!"));

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
    @Transactional
    public Double markLessonAsCompleted(Long enrollmentId, Long lessonId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Khong tim thay du lieu ghi danh!"));

        User currentUser = getCurrentUser();
        if (!enrollment.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Ban khong co quyen cap nhat tien do cua ghi danh nay!");
        }

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Khong tim thay bai hoc voi ID: " + lessonId));

        if (!lesson.getModule().getCourse().getId().equals(enrollment.getCourse().getId())) {
            throw new RuntimeException("Bai hoc khong thuoc khoa hoc da ghi danh!");
        }

        LessonProgress lessonProgress = lessonProgressRepository.findByEnrollmentIdAndLessonId(enrollmentId, lessonId)
                .orElseGet(() -> LessonProgress.builder()
                        .enrollment(enrollment)
                        .lesson(lesson)
                        .build());
        lessonProgress.setIsCompleted(true);
        lessonProgress.setCompletedAt(LocalDateTime.now());
        lessonProgressRepository.save(lessonProgress);

        long totalLessons = lessonRepository.countByModule_Course_Id(enrollment.getCourse().getId());
        long completedLessons = lessonProgressRepository.countByEnrollmentIdAndIsCompletedTrue(enrollmentId);
        double progress = totalLessons == 0 ? 0.0 : ((double) completedLessons / totalLessons) * 100.0;
        progress = Math.round(progress * 100.0) / 100.0;

        if (progress >= 100.0) {
            progress = 100.0;
            enrollment.setStatus("COMPLETED");
        }

        enrollment.setProgress(progress);
        enrollmentRepository.save(enrollment);
        return progress;
    }

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

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new RuntimeException("Ban can dang nhap de thuc hien thao tac nay!");
        }

        String currentUserEmail = authentication.getName();
        return userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Khong tim thay thong tin tai khoan!"));
    }

    private void requireAdminOrInstructor() {
        User currentUser = getCurrentUser();
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole()) && !"INSTRUCTOR".equalsIgnoreCase(currentUser.getRole())) {
            throw new ForbiddenOperationException("Chi admin hoac giang vien moi duoc thuc hien chuc nang nay!");
        }
    }
}
