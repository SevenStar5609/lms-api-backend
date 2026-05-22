package vn.edu.hutech.lms_api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.edu.hutech.lms_api.dto.enrollment.EnrollmentRequestDTO;
import vn.edu.hutech.lms_api.dto.enrollment.EnrollmentResponseDTO;
import vn.edu.hutech.lms_api.dto.enrollment.EnrollmentUpdateRequestDTO;

public interface EnrollmentService {
    EnrollmentResponseDTO enrollCourse(EnrollmentRequestDTO requestDTO);
    Page<EnrollmentResponseDTO> getMyEnrollments(Pageable pageable);
    EnrollmentResponseDTO getEnrollmentById(Long id);
    Page<EnrollmentResponseDTO> getUserEnrollments(Long userId, Pageable pageable);
    EnrollmentResponseDTO updateEnrollment(Long id, EnrollmentUpdateRequestDTO requestDTO);
    void deleteEnrollment(Long id);
    Double markLessonAsCompleted(Long enrollmentId, Long lessonId);
}
