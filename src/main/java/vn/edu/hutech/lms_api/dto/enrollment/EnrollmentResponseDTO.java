package vn.edu.hutech.lms_api.dto.enrollment;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EnrollmentResponseDTO {
    private Long id;
    private Long userId;
    private String userFullName;
    private Long courseId;
    private String courseTitle;
    private Double progress;
    private String status;
    private LocalDateTime createdAt;
}