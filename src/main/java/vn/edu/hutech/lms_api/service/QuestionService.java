package vn.edu.hutech.lms_api.service;

import vn.edu.hutech.lms_api.dto.question.QuestionRequestDTO;
import vn.edu.hutech.lms_api.dto.question.QuestionResponseDTO;
import java.util.List;

public interface QuestionService {
    QuestionResponseDTO createQuestion(QuestionRequestDTO requestDTO);
    List<QuestionResponseDTO> getQuestionsByQuiz(Long quizId); // Đã sửa dòng này
    QuestionResponseDTO getQuestionById(Long id);
    QuestionResponseDTO updateQuestion(Long id, QuestionRequestDTO requestDTO);
    void deleteQuestion(Long id);
}