package vn.edu.hutech.lms_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.hutech.lms_api.dto.enrollment.EnrollmentRequestDTO;
import vn.edu.hutech.lms_api.dto.enrollment.EnrollmentResponseDTO;
import vn.edu.hutech.lms_api.dto.enrollment.EnrollmentUpdateRequestDTO;
import vn.edu.hutech.lms_api.service.EnrollmentService;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<EnrollmentResponseDTO> enrollCourse(@Valid @RequestBody EnrollmentRequestDTO requestDTO) {
        return new ResponseEntity<>(enrollmentService.enrollCourse(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<Page<EnrollmentResponseDTO>> getMyEnrollments(Pageable pageable) {
        return ResponseEntity.ok(enrollmentService.getMyEnrollments(pageable));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<Page<EnrollmentResponseDTO>> getUserEnrollments(@PathVariable Long userId, Pageable pageable) {
        return ResponseEntity.ok(enrollmentService.getUserEnrollments(userId, pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<EnrollmentResponseDTO> updateEnrollment(
            @PathVariable Long id,
            @RequestBody EnrollmentUpdateRequestDTO requestDTO) {
        return ResponseEntity.ok(enrollmentService.updateEnrollment(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<String> deleteEnrollment(@PathVariable Long id) {
        enrollmentService.deleteEnrollment(id);
        return ResponseEntity.ok("Da huy ghi danh thanh cong ID: " + id);
    }

    @PostMapping("/{enrollmentId}/lessons/{lessonId}/complete")
    public ResponseEntity<String> markLessonAsCompleted(
            @PathVariable Long enrollmentId,
            @PathVariable Long lessonId) {
        Double newProgress = enrollmentService.markLessonAsCompleted(enrollmentId, lessonId);
        return ResponseEntity.ok("Da hoan thanh bai hoc. Tien do hien tai: " + newProgress + "%");
    }
}
