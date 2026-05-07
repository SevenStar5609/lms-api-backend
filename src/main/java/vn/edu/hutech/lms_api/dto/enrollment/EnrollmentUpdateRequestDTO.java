package vn.edu.hutech.lms_api.dto.enrollment;

import lombok.Data;

@Data
public class EnrollmentUpdateRequestDTO {
    // Chỉ cho phép cập nhật 2 trường này
    private Double progressPercentage;
    private String status; // Ví dụ truyền vào: "COMPLETED" hoặc "IN_PROGRESS"
}