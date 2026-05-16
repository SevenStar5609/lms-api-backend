package vn.edu.hutech.lms_api.service;

import vn.edu.hutech.lms_api.dto.certificate.CertificateResponseDTO;
import java.util.List;

public interface CertificateService {
    // Chỉ cần ID khóa học, không cần RequestDTO lằng nhằng nữa
    CertificateResponseDTO issueCertificate(Long courseId);

    List<CertificateResponseDTO> getMyCertificates();

    List<CertificateResponseDTO> getUserCertificates(Long userId);

    CertificateResponseDTO getCertificateById(Long id);

    void revokeCertificate(Long id);
}
