package vn.edu.hutech.lms_api.dto.enrollment;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnrollmentRequestDTO {

    @NotNull(message = "ID Học viên không được để trống")
    private Long userId;

    @NotNull(message = "ID Khóa học không được để trống")
    private Long courseId;
}