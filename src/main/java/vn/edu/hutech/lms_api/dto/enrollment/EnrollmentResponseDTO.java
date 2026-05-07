package vn.edu.hutech.lms_api.dto.enrollment;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class EnrollmentResponseDTO {
    private Long id;
    private Long userId;
    private String studentName; // Tên học viên
    private Long courseId;
    private String courseTitle; // Tên khóa học
    private LocalDateTime enrollmentDate;
    private String status;
    private Double progressPercentage;
}