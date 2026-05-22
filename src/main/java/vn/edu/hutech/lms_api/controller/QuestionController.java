package vn.edu.hutech.lms_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.hutech.lms_api.dto.question.QuestionRequestDTO;
import vn.edu.hutech.lms_api.dto.question.QuestionResponseDTO;
import vn.edu.hutech.lms_api.service.QuestionService;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    public ResponseEntity<QuestionResponseDTO> createQuestion(@Valid @RequestBody QuestionRequestDTO requestDTO) {
        return new ResponseEntity<>(questionService.createQuestion(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<Page<QuestionResponseDTO>> getQuestionsByQuiz(@PathVariable Long quizId, Pageable pageable) {
        return ResponseEntity.ok(questionService.getQuestionsByQuiz(quizId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponseDTO> getQuestionById(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.getQuestionById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuestionResponseDTO> updateQuestion(
            @PathVariable Long id,
            @Valid @RequestBody QuestionRequestDTO requestDTO) {
        return ResponseEntity.ok(questionService.updateQuestion(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.ok("Da xoa thanh cong cau hoi co ID: " + id);
    }
}
