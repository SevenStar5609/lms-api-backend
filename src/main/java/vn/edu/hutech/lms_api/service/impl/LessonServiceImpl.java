package vn.edu.hutech.lms_api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.edu.hutech.lms_api.domain.Enrollment;
import vn.edu.hutech.lms_api.domain.Lesson;
import vn.edu.hutech.lms_api.domain.Module;
import vn.edu.hutech.lms_api.domain.User;
import vn.edu.hutech.lms_api.dto.lesson.LessonRequestDTO;
import vn.edu.hutech.lms_api.dto.lesson.LessonResponseDTO;
import vn.edu.hutech.lms_api.exception.ForbiddenOperationException;
import vn.edu.hutech.lms_api.repository.EnrollmentRepository;
import vn.edu.hutech.lms_api.repository.LessonRepository;
import vn.edu.hutech.lms_api.repository.ModuleRepository;
import vn.edu.hutech.lms_api.repository.UserRepository;
import vn.edu.hutech.lms_api.service.LessonService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;

    // TIÊM THÊM 2 REPOSITORY ĐỂ KIỂM TRA QUYỀN
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public LessonResponseDTO createLesson(LessonRequestDTO requestDTO) {
        Module module = moduleRepository.findById(requestDTO.getModuleId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Chương học"));

        Lesson lesson = Lesson.builder()
                .title(requestDTO.getTitle())
                .contentUrl(requestDTO.getContentUrl())
                .contentBody(requestDTO.getContentBody())
                .contentType(requestDTO.getContentType())
                .orderIndex(requestDTO.getOrderIndex())
                .module(module)
                .build();

        return mapToResponseDTO(lessonRepository.save(lesson));
    }

    @Override
    public Page<LessonResponseDTO> getLessonsByModule(Long moduleId, Pageable pageable) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Chương học"));

        // KIỂM TRA BẢO MẬT TRƯỚC KHI TRẢ VỀ DANH SÁCH BÀI HỌC
        checkUserAccessToCourse(module.getCourse().getId());

        return lessonRepository.findByModuleIdOrderByOrderIndexAsc(moduleId, pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    public LessonResponseDTO getLessonById(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Bài học với ID: " + id));

        // KIỂM TRA BẢO MẬT TRƯỚC KHI CHO XEM CHI TIẾT BÀI HỌC
        checkUserAccessToCourse(lesson.getModule().getCourse().getId());

        return mapToResponseDTO(lesson);
    }

    @Override
    public LessonResponseDTO updateLesson(Long id, LessonRequestDTO requestDTO) {
        Lesson existingLesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Bài học"));

        Module module = moduleRepository.findById(requestDTO.getModuleId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Chương học"));

        existingLesson.setTitle(requestDTO.getTitle());
        existingLesson.setContentUrl(requestDTO.getContentUrl());
        existingLesson.setContentBody(requestDTO.getContentBody());
        existingLesson.setContentType(requestDTO.getContentType());
        existingLesson.setOrderIndex(requestDTO.getOrderIndex());
        existingLesson.setModule(module);

        return mapToResponseDTO(lessonRepository.save(existingLesson));
    }

    @Override
    public void deleteLesson(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Bài học"));
        lessonRepository.delete(lesson);
    }

    // ==========================================
    // HÀM PHỤ TRỢ: KIỂM TRA QUYỀN TRUY CẬP (CONTENT PROTECTION)
    // ==========================================
    private void checkUserAccessToCourse(Long courseId) {
        // 1. Lấy email của người dùng đang gọi API từ JWT Token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new RuntimeException("Bạn cần đăng nhập để xem nội dung này!");
        }

        String currentUserEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin tài khoản!"));

        // 2. Nếu là Giảng viên hoặc Admin thì bỏ qua kiểm tra, cho xem luôn
        if (currentUser.getRole().equalsIgnoreCase("INSTRUCTOR") || currentUser.getRole().equalsIgnoreCase("ADMIN")) {
            return;
        }

        // 3. Nếu là Học viên (LEARNER), bắt buộc phải có Enrollment
        Optional<Enrollment> enrollment = enrollmentRepository.findByUserIdAndCourseId(currentUser.getId(), courseId);
        if (enrollment.isEmpty()) {
            throw new ForbiddenOperationException("Ban chua ghi danh khoa hoc nay nen khong the xem noi dung bai hoc!");
        }
    }

    private LessonResponseDTO mapToResponseDTO(Lesson lesson) {
        return LessonResponseDTO.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .contentUrl(lesson.getContentUrl())
                .contentBody(lesson.getContentBody())
                .contentType(lesson.getContentType())
                .orderIndex(lesson.getOrderIndex())
                .moduleId(lesson.getModule().getId())
                .moduleTitle(lesson.getModule().getTitle())
                .createdAt(lesson.getCreatedAt())
                .build();
    }
}
