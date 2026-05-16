package vn.edu.hutech.lms_api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.hutech.lms_api.domain.Certificate;
import vn.edu.hutech.lms_api.domain.Course;
import vn.edu.hutech.lms_api.domain.Enrollment;
import vn.edu.hutech.lms_api.domain.User;
import vn.edu.hutech.lms_api.dto.certificate.CertificateResponseDTO;
import vn.edu.hutech.lms_api.repository.CertificateRepository;
import vn.edu.hutech.lms_api.repository.CourseRepository;
import vn.edu.hutech.lms_api.repository.EnrollmentRepository;
import vn.edu.hutech.lms_api.repository.UserRepository;
import vn.edu.hutech.lms_api.service.CertificateService;
import vn.edu.hutech.lms_api.service.PdfGenerationService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CertificateServiceImpl implements CertificateService {

    private final CertificateRepository certificateRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    // Tiêm dịch vụ tạo file PDF vật lý vào hệ thống
    private final PdfGenerationService pdfGenerationService;

    @Override
    @Transactional
    public CertificateResponseDTO issueCertificate(Long courseId) {
        // 1. Tự động lấy thông tin Học viên đang đăng nhập từ JWT Token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new RuntimeException("Bạn cần đăng nhập để thực hiện thao tác này!");
        }
        String currentUserEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin tài khoản!"));

        // 2. Kiểm tra Khóa học có tồn tại hay không
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Khóa học với ID: " + courseId));

        // 3. Kiểm tra Học viên đã ghi danh và hoàn thành 100% tiến độ chưa
        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(currentUser.getId(), courseId)
                .orElseThrow(() -> new RuntimeException("Bạn chưa ghi danh khóa học này!"));

        if (!"COMPLETED".equalsIgnoreCase(enrollment.getStatus()) && enrollment.getProgress() < 100.0) {
            throw new RuntimeException("Bạn chưa hoàn thành 100% tiến độ khóa học để nhận chứng chỉ!");
        }

        // 4. Kiểm tra xem chứng chỉ đã được cấp trước đó chưa (Tránh cấp trùng)
        if (certificateRepository.findByUserIdAndCourseId(currentUser.getId(), courseId).isPresent()) {
            throw new RuntimeException("Chứng chỉ cho khóa học này đã được cấp cho bạn trước đó!");
        }

        // 5. Sinh mã chứng chỉ độc nhất (Ví dụ: YOOT-CERT-A1B2C3D4)
        String certCode = "YOOT-CERT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 6. Kích hoạt cỗ máy in PDF để tạo file vật lý trong thư mục uploads
        // Loại bỏ dấu tiếng Việt hoặc chuyển sang ký tự không dấu nếu cần để tránh lỗi Font của thư viện PDF
        String pdfFileName = pdfGenerationService.generateCertificatePdf(
                currentUser.getFullName(),
                course.getTitle(),
                certCode
        );

        // 7. Tạo đường dẫn tải file (Sử dụng API download file đã làm)
        String pdfDownloadUrl = "http://localhost:8080/api/v1/files/download/" + pdfFileName;

        // 8. Đóng gói thông tin và lưu vào Cơ sở dữ liệu
        Certificate certificate = Certificate.builder()
                .certificateCode(certCode)
                .pdfUrl(pdfDownloadUrl)
                .user(currentUser)
                .course(course)
                .build();

        Certificate savedCertificate = certificateRepository.save(certificate);

        return mapToResponseDTO(savedCertificate);
    }

    @Override
    public List<CertificateResponseDTO> getMyCertificates() {
        // Lấy danh sách tất cả chứng chỉ của riêng học viên đang đăng nhập
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin tài khoản!"));

        return certificateRepository.findByUserId(currentUser.getId()).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CertificateResponseDTO getCertificateById(Long id) {
        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dữ liệu chứng chỉ với ID: " + id));
        return mapToResponseDTO(certificate);
    }

    @Override
    public List<CertificateResponseDTO> getUserCertificates(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Không tìm thấy thông tin tài khoản với ID: " + userId);
        }

        return certificateRepository.findByUserId(userId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void revokeCertificate(Long id) {
        if (!certificateRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy dữ liệu chứng chỉ với ID: " + id);
        }

        certificateRepository.deleteById(id);
    }

    // Hàm ánh xạ dữ liệu từ Entity sang DTO trả về cho Client
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
}
