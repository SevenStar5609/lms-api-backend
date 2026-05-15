package vn.edu.hutech.lms_api.service;

import vn.edu.hutech.lms_api.dto.certificate.CertificateRequestDTO;
import vn.edu.hutech.lms_api.dto.certificate.CertificateResponseDTO;
import java.util.List;

public interface CertificateService {
    CertificateResponseDTO issueCertificate(CertificateRequestDTO requestDTO);
    List<CertificateResponseDTO> getUserCertificates(Long userId);
    CertificateResponseDTO getCertificateById(Long id);
    void revokeCertificate(Long id); // Thu hồi (Xóa) chứng chỉ
}