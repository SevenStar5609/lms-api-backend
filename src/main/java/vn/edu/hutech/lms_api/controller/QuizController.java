package vn.edu.hutech.lms_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.hutech.lms_api.dto.quiz.QuizRequestDTO;
import vn.edu.hutech.lms_api.dto.quiz.QuizResponseDTO;
import vn.edu.hutech.lms_api.dto.quiz.QuizResultResponseDTO;
import vn.edu.hutech.lms_api.dto.quiz.QuizSubmitRequestDTO;
import vn.edu.hutech.lms_api.service.QuizService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @PostMapping
    public ResponseEntity<QuizResponseDTO> createQuiz(@Valid @RequestBody QuizRequestDTO requestDTO) {
        return new ResponseEntity<>(quizService.createQuiz(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<QuizResponseDTO>> getQuizzesByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(quizService.getQuizzesByCourse(courseId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizResponseDTO> getQuizById(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getQuizById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuizResponseDTO> updateQuiz(@PathVariable Long id, @Valid @RequestBody QuizRequestDTO requestDTO) {
        return ResponseEntity.ok(quizService.updateQuiz(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.ok("Đã xóa thành công Bài kiểm tra có ID: " + id);
    }

    @PostMapping("/submit")
    public ResponseEntity<QuizResultResponseDTO> submitQuiz(@RequestBody QuizSubmitRequestDTO requestDTO) {
        QuizResultResponseDTO result = quizService.submitQuiz(requestDTO);
        return ResponseEntity.ok(result);
    }
}