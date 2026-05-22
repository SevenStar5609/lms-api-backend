package vn.edu.hutech.lms_api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.edu.hutech.lms_api.dto.question.QuestionRequestDTO;
import vn.edu.hutech.lms_api.dto.question.QuestionResponseDTO;

public interface QuestionService {
    QuestionResponseDTO createQuestion(QuestionRequestDTO requestDTO);
    Page<QuestionResponseDTO> getQuestionsByQuiz(Long quizId, Pageable pageable);
    QuestionResponseDTO getQuestionById(Long id);
    QuestionResponseDTO updateQuestion(Long id, QuestionRequestDTO requestDTO);
    void deleteQuestion(Long id);
}
