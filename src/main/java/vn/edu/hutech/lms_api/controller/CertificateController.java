package vn.edu.hutech.lms_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.hutech.lms_api.dto.certificate.CertificateResponseDTO;
import vn.edu.hutech.lms_api.service.CertificateService;

@RestController
@RequestMapping("/api/v1/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @PostMapping("/issue/{courseId}")
    public ResponseEntity<CertificateResponseDTO> issueCertificate(@PathVariable Long courseId) {
        return ResponseEntity.ok(certificateService.issueCertificate(courseId));
    }

    @GetMapping("/me")
    public ResponseEntity<Page<CertificateResponseDTO>> getMyCertificates(Pageable pageable) {
        return ResponseEntity.ok(certificateService.getMyCertificates(pageable));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<CertificateResponseDTO>> getUserCertificates(@PathVariable Long userId, Pageable pageable) {
        return ResponseEntity.ok(certificateService.getUserCertificates(userId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CertificateResponseDTO> getCertificateById(@PathVariable Long id) {
        return ResponseEntity.ok(certificateService.getCertificateById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> revokeCertificate(@PathVariable Long id) {
        certificateService.revokeCertificate(id);
        return ResponseEntity.ok("Da thu hoi chung chi thanh cong.");
    }
}
