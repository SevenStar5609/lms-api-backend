package vn.edu.hutech.lms_api.dto.enrollment;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnrollmentRequestDTO {

    @NotNull(message = "ID khóa học không được để trống")
    private Long courseId;

    // Đã loại bỏ trường userId tại đây vì hệ thống tự nhận diện qua Token
}