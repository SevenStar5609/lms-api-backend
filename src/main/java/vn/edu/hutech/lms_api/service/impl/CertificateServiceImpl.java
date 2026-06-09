package vn.edu.hutech.lms_api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.hutech.lms_api.domain.Certificate;
import vn.edu.hutech.lms_api.domain.Course;
import vn.edu.hutech.lms_api.domain.Enrollment;
import vn.edu.hutech.lms_api.domain.User;
import vn.edu.hutech.lms_api.dto.certificate.CertificateResponseDTO;
import vn.edu.hutech.lms_api.exception.ForbiddenOperationException;
import vn.edu.hutech.lms_api.repository.CertificateRepository;
import vn.edu.hutech.lms_api.repository.CourseRepository;
import vn.edu.hutech.lms_api.repository.EnrollmentRepository;
import vn.edu.hutech.lms_api.repository.UserRepository;
import vn.edu.hutech.lms_api.service.CertificateService;
import vn.edu.hutech.lms_api.service.CloudStorageService;
import vn.edu.hutech.lms_api.service.PdfGenerationService;

import java.nio.file.Path;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CertificateServiceImpl implements CertificateService {

    private final CertificateRepository certificateRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PdfGenerationService pdfGenerationService;
    private final CloudStorageService cloudStorageService;

    @Override
    @Transactional
    public CertificateResponseDTO issueCertificate(Long courseId) {
        User currentUser = getCurrentUser();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Khong tim thay khoa hoc voi ID: " + courseId));

        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(currentUser.getId(), courseId)
                .orElseThrow(() -> new RuntimeException("Ban chua ghi danh khoa hoc nay!"));

        if (!"COMPLETED".equalsIgnoreCase(enrollment.getStatus()) && enrollment.getProgress() < 100.0) {
            throw new RuntimeException("Ban chua hoan thanh 100% tien do khoa hoc de nhan chung chi!");
        }

        if (certificateRepository.findByUserIdAndCourseId(currentUser.getId(), courseId).isPresent()) {
            throw new RuntimeException("Chung chi cho khoa hoc nay da duoc cap cho ban truoc do!");
        }

        String certCode = "YOOT-CERT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String pdfFileName = pdfGenerationService.generateCertificatePdf(
                currentUser.getFullName(),
                course.getTitle(),
                certCode
        );
        Path pdfPath = pdfGenerationService.getCertificatePath(pdfFileName);
        String cloudUrl = cloudStorageService.uploadCertificatePdf(pdfPath, certCode);

        Certificate certificate = Certificate.builder()
                .certificateCode(certCode)
                .pdfUrl(cloudUrl != null ? cloudUrl : "http://localhost:8080/api/v1/files/download/" + pdfFileName)
                .user(currentUser)
                .course(course)
                .build();

        return mapToResponseDTO(certificateRepository.save(certificate));
    }

    @Override
    public Page<CertificateResponseDTO> getMyCertificates(Pageable pageable) {
        User currentUser = getCurrentUser();
        return certificateRepository.findByUserId(currentUser.getId(), pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    public CertificateResponseDTO getCertificateById(Long id) {
        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay du lieu chung chi voi ID: " + id));
        User currentUser = getCurrentUser();
        if (!canManageCertificates(currentUser) && !certificate.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenOperationException("Ban khong co quyen xem chung chi cua nguoi dung khac!");
        }
        return mapToResponseDTO(certificate);
    }

    @Override
    public Page<CertificateResponseDTO> getUserCertificates(Long userId, Pageable pageable) {
        requireAdminOrInstructor();
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Khong tim thay thong tin tai khoan voi ID: " + userId);
        }

        return certificateRepository.findByUserId(userId, pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    @Transactional
    public void revokeCertificate(Long id) {
        requireAdminOrInstructor();
        if (!certificateRepository.existsById(id)) {
            throw new RuntimeException("Khong tim thay du lieu chung chi voi ID: " + id);
        }

        certificateRepository.deleteById(id);
    }

    private CertificateResponseDTO mapToResponseDTO(Certificate certificate) {
        return CertificateResponseDTO.builder()
                .id(certificate.getId())
                .certificateCode(certificate.getCertificateCode())
                .pdfUrl(certificate.getPdfUrl())
                .userId(certificate.getUser().getId())
                .userFullName(certificate.getUser().getFullName())
                .courseId(certificate.getCourse().getId())
                .courseTitle(certificate.getCourse().getTitle())
                .createdAt(certificate.getCreatedAt())
                .build();
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new RuntimeException("Ban can dang nhap de thuc hien thao tac nay!");
        }

        String currentUserEmail = authentication.getName();
        return userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Khong tim thay thong tin tai khoan!"));
    }

    private void requireAdminOrInstructor() {
        User currentUser = getCurrentUser();
        if (!canManageCertificates(currentUser)) {
            throw new ForbiddenOperationException("Chi admin hoac giang vien moi duoc thuc hien chuc nang nay!");
        }
    }

    private boolean canManageCertificates(User user) {
        return "ADMIN".equalsIgnoreCase(user.getRole()) || "INSTRUCTOR".equalsIgnoreCase(user.getRole());
    }
}
