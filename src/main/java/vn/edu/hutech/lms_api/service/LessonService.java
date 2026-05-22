package vn.edu.hutech.lms_api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.edu.hutech.lms_api.dto.lesson.LessonRequestDTO;
import vn.edu.hutech.lms_api.dto.lesson.LessonResponseDTO;

public interface LessonService {
    LessonResponseDTO createLesson(LessonRequestDTO requestDTO);
    Page<LessonResponseDTO> getLessonsByModule(Long moduleId, Pageable pageable);
    LessonResponseDTO getLessonById(Long id);
    LessonResponseDTO updateLesson(Long id, LessonRequestDTO requestDTO);
    void deleteLesson(Long id);
}
