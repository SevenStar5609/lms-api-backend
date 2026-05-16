package vn.edu.hutech.lms_api.dto.certificate;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class CertificateResponseDTO {
    private Long id;
    private String certificateCode;
    private String pdfUrl;
    private Long userId;
    private String userFullName;
    private Long courseId;
    private String courseTitle;
    private LocalDateTime createdAt;
}