package vn.edu.hutech.lms_api.dto.enrollment;

import lombok.Data;

@Data
public class EnrollmentUpdateRequestDTO {
    // Không bắt buộc phải truyền lên, người dùng muốn cập nhật cái nào thì truyền cái đó
    private String status;
    private Double progress;
}