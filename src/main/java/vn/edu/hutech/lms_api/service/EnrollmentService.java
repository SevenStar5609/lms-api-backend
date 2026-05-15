package vn.edu.hutech.lms_api.service;

import vn.edu.hutech.lms_api.dto.enrollment.EnrollmentRequestDTO;
import vn.edu.hutech.lms_api.dto.enrollment.EnrollmentResponseDTO;
import vn.edu.hutech.lms_api.dto.enrollment.EnrollmentUpdateRequestDTO;
import java.util.List;

public interface EnrollmentService {
    EnrollmentResponseDTO enrollCourse(EnrollmentRequestDTO requestDTO);
    List<EnrollmentResponseDTO> getUserEnrollments(Long userId);
    EnrollmentResponseDTO updateEnrollment(Long id, EnrollmentUpdateRequestDTO requestDTO);
    void deleteEnrollment(Long id);
    // Trả về số phần trăm tiến độ mới sau khi cập nhật
    Double markLessonAsCompleted(Long enrollmentId, Long lessonId);
}