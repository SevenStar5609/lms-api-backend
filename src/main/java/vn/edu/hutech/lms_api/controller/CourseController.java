package vn.edu.hutech.lms_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.hutech.lms_api.dto.course.CourseRequestDTO;
import vn.edu.hutech.lms_api.dto.course.CourseResponseDTO;
import vn.edu.hutech.lms_api.service.CourseService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses") // Đây là đường dẫn gốc của API này
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    // API Tạo khóa học mới: POST http://localhost:8080/api/v1/courses
    @PostMapping
    public ResponseEntity<CourseResponseDTO> createCourse(@Valid @RequestBody CourseRequestDTO requestDTO) {
        CourseResponseDTO response = courseService.createCourse(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // API Lấy danh sách khóa học: GET http://localhost:8080/api/v1/courses
    @GetMapping
    public ResponseEntity<List<CourseResponseDTO>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }
}