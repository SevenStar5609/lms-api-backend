package vn.edu.hutech.lms_api.service;

import vn.edu.hutech.lms_api.dto.quiz.QuizRequestDTO;
import vn.edu.hutech.lms_api.dto.quiz.QuizResponseDTO;
import vn.edu.hutech.lms_api.dto.quiz.QuizResultResponseDTO;
import vn.edu.hutech.lms_api.dto.quiz.QuizSubmitRequestDTO;

import java.util.List;

public interface QuizService {
    QuizResponseDTO createQuiz(QuizRequestDTO requestDTO);
    List<QuizResponseDTO> getQuizzesByCourse(Long courseId);
    QuizResponseDTO getQuizById(Long id);
    QuizResponseDTO updateQuiz(Long id, QuizRequestDTO requestDTO);
    void deleteQuiz(Long id);
    QuizResultResponseDTO submitQuiz(QuizSubmitRequestDTO requestDTO);
}