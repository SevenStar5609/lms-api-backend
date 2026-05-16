package vn.edu.hutech.lms_api.service;

import vn.edu.hutech.lms_api.dto.enrollment.EnrollmentRequestDTO;
import vn.edu.hutech.lms_api.dto.enrollment.EnrollmentResponseDTO;
import vn.edu.hutech.lms_api.dto.enrollment.EnrollmentUpdateRequestDTO; // Thêm import này

import java.util.List;

public interface EnrollmentService {
    EnrollmentResponseDTO enrollCourse(EnrollmentRequestDTO requestDTO);
    List<EnrollmentResponseDTO> getMyEnrollments();
    EnrollmentResponseDTO getEnrollmentById(Long id);

    // --- 4 HÀM BỔ SUNG ĐỂ SỬA LỖI BUILD ---
    List<EnrollmentResponseDTO> getUserEnrollments(Long userId);
    EnrollmentResponseDTO updateEnrollment(Long id, EnrollmentUpdateRequestDTO requestDTO);
    void deleteEnrollment(Long id);
    // Đổi EnrollmentResponseDTO thành Double
    Double markLessonAsCompleted(Long enrollmentId, Long lessonId);
}