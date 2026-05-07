package vn.edu.hutech.lms_api.service;

import vn.edu.hutech.lms_api.dto.lesson.LessonRequestDTO;
import vn.edu.hutech.lms_api.dto.lesson.LessonResponseDTO;
import java.util.List;

public interface LessonService {
    LessonResponseDTO createLesson(LessonRequestDTO requestDTO);
    List<LessonResponseDTO> getLessonsByModule(Long moduleId);

    LessonResponseDTO getLessonById(Long id);
    LessonResponseDTO updateLesson(Long id, LessonRequestDTO requestDTO);
    void deleteLesson(Long id);
}