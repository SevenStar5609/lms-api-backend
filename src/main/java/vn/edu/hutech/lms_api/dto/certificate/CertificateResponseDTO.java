package vn.edu.hutech.lms_api.dto.certificate;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CertificateResponseDTO {
    private Long id;
    private Long userId;
    private String studentName;
    private Long courseId;
    private String courseTitle;
    private String certificateCode;
    private String pdfUrl;
    private LocalDateTime issuedAt;
}