package vn.edu.hutech.lms_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.hutech.lms_api.dto.question.QuestionRequestDTO;
import vn.edu.hutech.lms_api.dto.question.QuestionResponseDTO;
import vn.edu.hutech.lms_api.service.QuestionService;

import java.util.List; // Chìa khóa giải quyết lỗi 'Cannot resolve symbol List'

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    // 1. CREATE - Tạo câu hỏi mới
    @PostMapping
    public ResponseEntity<QuestionResponseDTO> createQuestion(@Valid @RequestBody QuestionRequestDTO requestDTO) {
        return new ResponseEntity<>(questionService.createQuestion(requestDTO), HttpStatus.CREATED);
    }

    // 2. READ - Lấy danh sách câu hỏi của 1 bài kiểm tra
    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<QuestionResponseDTO>> getQuestionsByQuiz(@PathVariable Long quizId) {
        return ResponseEntity.ok(questionService.getQuestionsByQuiz(quizId));
    }

    // 3. READ - Lấy chi tiết 1 câu hỏi
    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponseDTO> getQuestionById(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.getQuestionById(id));
    }

    // 4. UPDATE - Cập nhật câu hỏi
    @PutMapping("/{id}")
    public ResponseEntity<QuestionResponseDTO> updateQuestion(
            @PathVariable Long id,
            @Valid @RequestBody QuestionRequestDTO requestDTO) {
        return ResponseEntity.ok(questionService.updateQuestion(id, requestDTO));
    }

    // 5. DELETE - Xóa câu hỏi
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.ok("Đã xóa thành công Câu hỏi có ID: " + id);
    }
}