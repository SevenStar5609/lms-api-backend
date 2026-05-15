package vn.edu.hutech.lms_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.hutech.lms_api.dto.enrollment.EnrollmentRequestDTO;
import vn.edu.hutech.lms_api.dto.enrollment.EnrollmentResponseDTO;
import vn.edu.hutech.lms_api.dto.enrollment.EnrollmentUpdateRequestDTO;
import vn.edu.hutech.lms_api.service.EnrollmentService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    // API Đăng ký khóa học
    @PostMapping
    public ResponseEntity<EnrollmentResponseDTO> enrollCourse(@Valid @RequestBody EnrollmentRequestDTO requestDTO) {
        EnrollmentResponseDTO response = enrollmentService.enrollCourse(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // API Lấy danh sách khóa học mà một user đã đăng ký
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EnrollmentResponseDTO>> getUserEnrollments(@PathVariable Long userId) {
        return ResponseEntity.ok(enrollmentService.getUserEnrollments(userId));
    }

    // API Cập nhật tiến độ học tập
    @PutMapping("/{id}")
    public ResponseEntity<EnrollmentResponseDTO> updateEnrollment(
            @PathVariable Long id,
            @RequestBody EnrollmentUpdateRequestDTO requestDTO) {
        return ResponseEntity.ok(enrollmentService.updateEnrollment(id, requestDTO));
    }

    // API Hủy đăng ký khóa học
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEnrollment(@PathVariable Long id) {
        enrollmentService.deleteEnrollment(id);
        return ResponseEntity.ok("Đã hủy ghi danh thành công ID: " + id);
    }

    // API Đánh dấu hoàn thành bài học
    @PostMapping("/{enrollmentId}/lessons/{lessonId}/complete")
    public ResponseEntity<String> markLessonAsCompleted(
            @PathVariable Long enrollmentId,
            @PathVariable Long lessonId) {

        Double newProgress = enrollmentService.markLessonAsCompleted(enrollmentId, lessonId);
        return ResponseEntity.ok("Đã hoàn thành bài học. Tiến độ hiện tại: " + newProgress + "%");
    }
}