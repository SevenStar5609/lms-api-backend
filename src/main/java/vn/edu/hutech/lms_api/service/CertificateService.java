package vn.edu.hutech.lms_api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.edu.hutech.lms_api.dto.certificate.CertificateResponseDTO;

public interface CertificateService {
    CertificateResponseDTO issueCertificate(Long courseId);
    Page<CertificateResponseDTO> getMyCertificates(Pageable pageable);
    Page<CertificateResponseDTO> getUserCertificates(Long userId, Pageable pageable);
    CertificateResponseDTO getCertificateById(Long id);
    void revokeCertificate(Long id);
}
