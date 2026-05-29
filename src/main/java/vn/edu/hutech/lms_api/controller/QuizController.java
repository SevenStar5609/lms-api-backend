package vn.edu.hutech.lms_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.hutech.lms_api.dto.question.QuestionForQuizResponseDTO;
import vn.edu.hutech.lms_api.dto.quiz.QuizRequestDTO;
import vn.edu.hutech.lms_api.dto.quiz.QuizResponseDTO;
import vn.edu.hutech.lms_api.dto.quiz.QuizResultResponseDTO;
import vn.edu.hutech.lms_api.dto.quiz.QuizSubmitRequestDTO;
import vn.edu.hutech.lms_api.service.QuizService;

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
    public ResponseEntity<Page<QuizResponseDTO>> getQuizzesByCourse(@PathVariable Long courseId, Pageable pageable) {
        return ResponseEntity.ok(quizService.getQuizzesByCourse(courseId, pageable));
    }

    @GetMapping("/module/{moduleId}")
    public ResponseEntity<Page<QuizResponseDTO>> getQuizzesByModule(@PathVariable Long moduleId, Pageable pageable) {
        return ResponseEntity.ok(quizService.getQuizzesByModule(moduleId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizResponseDTO> getQuizById(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getQuizById(id));
    }

    @GetMapping("/{id}/questions")
    public ResponseEntity<Page<QuestionForQuizResponseDTO>> getQuestionsForQuiz(@PathVariable Long id, Pageable pageable) {
        return ResponseEntity.ok(quizService.getQuestionsForQuiz(id, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuizResponseDTO> updateQuiz(@PathVariable Long id, @Valid @RequestBody QuizRequestDTO requestDTO) {
        return ResponseEntity.ok(quizService.updateQuiz(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.ok("Da xoa thanh cong bai kiem tra co ID: " + id);
    }

    @PostMapping("/submit")
    public ResponseEntity<QuizResultResponseDTO> submitQuiz(@Valid @RequestBody QuizSubmitRequestDTO requestDTO) {
        return ResponseEntity.ok(quizService.submitQuiz(requestDTO));
    }

    @GetMapping("/attempts/{attemptId}")
    public ResponseEntity<QuizResultResponseDTO> getAttemptResult(@PathVariable Long attemptId) {
        return ResponseEntity.ok(quizService.getAttemptResult(attemptId));
    }
}
