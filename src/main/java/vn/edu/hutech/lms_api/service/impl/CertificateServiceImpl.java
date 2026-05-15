package vn.edu.hutech.lms_api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.hutech.lms_api.domain.Certificate;
import vn.edu.hutech.lms_api.domain.Course;
import vn.edu.hutech.lms_api.domain.User;
import vn.edu.hutech.lms_api.dto.certificate.CertificateRequestDTO;
import vn.edu.hutech.lms_api.dto.certificate.CertificateResponseDTO;
import vn.edu.hutech.lms_api.repository.CertificateRepository;
import vn.edu.hutech.lms_api.repository.CourseRepository;
import vn.edu.hutech.lms_api.repository.UserRepository;
import vn.edu.hutech.lms_api.service.CertificateService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CertificateServiceImpl implements CertificateService {

    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Override
    public CertificateResponseDTO issueCertificate(CertificateRequestDTO requestDTO) {
        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));

        Course course = courseRepository.findById(requestDTO.getCourseId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Khóa học"));

        // 1. Kiểm tra xem đã được cấp chứng chỉ chưa
        Optional<Certificate> existing = certificateRepository.findByUserIdAndCourseId(user.getId(), course.getId());
        if (existing.isPresent()) {
            throw new RuntimeException("Học viên này đã được cấp chứng chỉ cho khóa học này rồi!");
        }

        // 2. Tự động sinh mã chứng chỉ độc nhất (Ví dụ: YOOT-CERT-xxxxxx)
        String uniqueCode = "YOOT-CERT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 3. Tạo chứng chỉ
        Certificate certificate = Certificate.builder()
                .user(user)
                .course(course)
                .certificateCode(uniqueCode)
                .pdfUrl(requestDTO.getPdfUrl())
                .build();

        return mapToResponseDTO(certificateRepository.save(certificate));
    }

    @Override
    public List<CertificateResponseDTO> getUserCertificates(Long userId) {
        return certificateRepository.findByUserId(userId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CertificateResponseDTO getCertificateById(Long id) {
        Certificate cert = certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Chứng chỉ"));
        return mapToResponseDTO(cert);
    }

    @Override
    public void revokeCertificate(Long id) {
        Certificate cert = certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Chứng chỉ"));
        certificateRepository.delete(cert);
    }

    private CertificateResponseDTO mapToResponseDTO(Certificate cert) {
        return CertificateResponseDTO.builder()
                .id(cert.getId())
                .userId(cert.getUser().getId())
                .studentName(cert.getUser().getFullName())
                .courseId(cert.getCourse().getId())
                .courseTitle(cert.getCourse().getTitle())
                .certificateCode(cert.getCertificateCode())
                .pdfUrl(cert.getPdfUrl())
                .issuedAt(cert.getIssuedAt())
                .build();
    }
}