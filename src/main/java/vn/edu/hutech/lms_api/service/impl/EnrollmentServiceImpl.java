package vn.edu.hutech.lms_api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.hutech.lms_api.domain.Course;
import vn.edu.hutech.lms_api.domain.Enrollment;
import vn.edu.hutech.lms_api.domain.User;
import vn.edu.hutech.lms_api.dto.enrollment.EnrollmentRequestDTO;
import vn.edu.hutech.lms_api.dto.enrollment.EnrollmentResponseDTO;
import vn.edu.hutech.lms_api.dto.enrollment.EnrollmentUpdateRequestDTO;
import vn.edu.hutech.lms_api.repository.CourseRepository;
import vn.edu.hutech.lms_api.repository.EnrollmentRepository;
import vn.edu.hutech.lms_api.repository.UserRepository;
import vn.edu.hutech.lms_api.service.EnrollmentService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Override
    public EnrollmentResponseDTO enrollCourse(EnrollmentRequestDTO requestDTO) {
        // 1. Kiểm tra User có tồn tại không
        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Học viên với ID: " + requestDTO.getUserId()));

        // 2. Kiểm tra Course có tồn tại không
        Course course = courseRepository.findById(requestDTO.getCourseId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Khóa học với ID: " + requestDTO.getCourseId()));

        // 3. Kiểm tra xem học viên đã đăng ký khóa này chưa
        Optional<Enrollment> existingEnrollment = enrollmentRepository.findByUserIdAndCourseId(user.getId(), course.getId());
        if (existingEnrollment.isPresent()) {
            throw new RuntimeException("Học viên này đã đăng ký khóa học rồi!");
        }

        // 4. Tạo phiếu ghi danh mới
        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .course(course)
                .status("IN_PROGRESS")
                .progressPercentage(0.0)
                // Lưu ý: enrollmentDate đã được tự động tạo nếu bạn dùng @CreationTimestamp ở Entity
                // Nếu chưa có trong Entity Enrollment, hệ thống DB PostgreSQL vẫn set mặc định nhờ DEFAULT CURRENT_TIMESTAMP
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

    @Override
    public EnrollmentResponseDTO updateEnrollment(Long id, EnrollmentUpdateRequestDTO requestDTO) {
        // 1. Tìm phiếu ghi danh
        Enrollment existingEnrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Phiếu ghi danh với ID: " + id));

        // 2. Cập nhật các trường được phép (nếu client có truyền lên)
        if (requestDTO.getProgressPercentage() != null) {
            existingEnrollment.setProgressPercentage(requestDTO.getProgressPercentage());
        }
        if (requestDTO.getStatus() != null) {
            existingEnrollment.setStatus(requestDTO.getStatus());
        }

        // 3. Lưu xuống DB
        Enrollment updatedEnrollment = enrollmentRepository.save(existingEnrollment);
        return mapToResponseDTO(updatedEnrollment);
    }

    @Override
    public void deleteEnrollment(Long id) {
        // Tìm và xóa phiếu ghi danh (Hủy đăng ký khóa học)
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Phiếu ghi danh với ID: " + id));
        enrollmentRepository.delete(enrollment);
    }
}