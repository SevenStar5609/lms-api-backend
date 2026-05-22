package vn.edu.hutech.lms_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.hutech.lms_api.dto.lesson.LessonRequestDTO;
import vn.edu.hutech.lms_api.dto.lesson.LessonResponseDTO;
import vn.edu.hutech.lms_api.service.LessonService;

@RestController
@RequestMapping("/api/v1/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @PostMapping
    public ResponseEntity<LessonResponseDTO> createLesson(@Valid @RequestBody LessonRequestDTO requestDTO) {
        return new ResponseEntity<>(lessonService.createLesson(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/module/{moduleId}")
    public ResponseEntity<Page<LessonResponseDTO>> getLessonsByModule(@PathVariable Long moduleId, Pageable pageable) {
        return ResponseEntity.ok(lessonService.getLessonsByModule(moduleId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LessonResponseDTO> getLessonById(@PathVariable Long id) {
        return ResponseEntity.ok(lessonService.getLessonById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LessonResponseDTO> updateLesson(@PathVariable Long id, @Valid @RequestBody LessonRequestDTO requestDTO) {
        return ResponseEntity.ok(lessonService.updateLesson(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.ok("Da xoa thanh cong bai hoc co ID: " + id);
    }
}
