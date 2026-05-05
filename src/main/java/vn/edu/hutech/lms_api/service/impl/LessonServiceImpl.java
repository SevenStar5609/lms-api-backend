package vn.edu.hutech.lms_api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.hutech.lms_api.domain.Lesson;
import vn.edu.hutech.lms_api.domain.Module;
import vn.edu.hutech.lms_api.dto.lesson.LessonRequestDTO;
import vn.edu.hutech.lms_api.dto.lesson.LessonResponseDTO;
import vn.edu.hutech.lms_api.repository.LessonRepository;
import vn.edu.hutech.lms_api.repository.ModuleRepository;
import vn.edu.hutech.lms_api.service.LessonService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;

    @Override
    public LessonResponseDTO createLesson(LessonRequestDTO requestDTO) {
        // 1. Kiểm tra xem Chương học (Module) có tồn tại không
        Module module = moduleRepository.findById(requestDTO.getModuleId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Chương học với ID: " + requestDTO.getModuleId()));

        // 2. Chuyển đổi dữ liệu
        Lesson lesson = Lesson.builder()
                .title(requestDTO.getTitle())
                .contentType(requestDTO.getContentType())
                .contentUrl(requestDTO.getContentUrl())
                .contentBody(requestDTO.getContentBody())
                .orderIndex(requestDTO.getOrderIndex())
                .module(module)
                .build();

        // 3. Lưu xuống DB
        Lesson savedLesson = lessonRepository.save(lesson);
        return mapToResponseDTO(savedLesson);
    }

    @Override
    public List<LessonResponseDTO> getLessonsByModule(Long moduleId) {
        List<Lesson> lessons = lessonRepository.findByModuleIdOrderByOrderIndexAsc(moduleId);
        return lessons.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private LessonResponseDTO mapToResponseDTO(Lesson lesson) {
        return LessonResponseDTO.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .contentType(lesson.getContentType())
                .contentUrl(lesson.getContentUrl())
                .contentBody(lesson.getContentBody())
                .orderIndex(lesson.getOrderIndex())
                .moduleId(lesson.getModule().getId())
                .moduleTitle(lesson.getModule().getTitle())
                .createdAt(lesson.getCreatedAt())
                .build();
    }
}