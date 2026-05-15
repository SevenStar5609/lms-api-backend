package vn.edu.hutech.lms_api.dto.certificate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CertificateRequestDTO {
    @NotNull(message = "ID Học viên không được để trống")
    private Long userId;

    @NotNull(message = "ID Khóa học không được để trống")
    private Long courseId;

    @NotBlank(message = "Đường dẫn file PDF không được để trống")
    private String pdfUrl;
}