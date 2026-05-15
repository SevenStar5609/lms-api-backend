package vn.edu.hutech.lms_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.hutech.lms_api.dto.certificate.CertificateRequestDTO;
import vn.edu.hutech.lms_api.dto.certificate.CertificateResponseDTO;
import vn.edu.hutech.lms_api.service.CertificateService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    // API Cấp chứng chỉ mới
    @PostMapping("/issue")
    public ResponseEntity<CertificateResponseDTO> issueCertificate(@Valid @RequestBody CertificateRequestDTO requestDTO) {
        return new ResponseEntity<>(certificateService.issueCertificate(requestDTO), HttpStatus.CREATED);
    }

    // API Lấy danh sách chứng chỉ của 1 học viên
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CertificateResponseDTO>> getUserCertificates(@PathVariable Long userId) {
        return ResponseEntity.ok(certificateService.getUserCertificates(userId));
    }

    // API Xem chi tiết 1 chứng chỉ (Để verify/xác thực)
    @GetMapping("/{id}")
    public ResponseEntity<CertificateResponseDTO> getCertificateById(@PathVariable Long id) {
        return ResponseEntity.ok(certificateService.getCertificateById(id));
    }

    // API Thu hồi chứng chỉ
    @DeleteMapping("/{id}")
    public ResponseEntity<String> revokeCertificate(@PathVariable Long id) {
        certificateService.revokeCertificate(id);
        return ResponseEntity.ok("Đã thu hồi chứng chỉ thành công.");
    }
}