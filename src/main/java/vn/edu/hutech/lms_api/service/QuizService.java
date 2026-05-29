package vn.edu.hutech.lms_api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.edu.hutech.lms_api.dto.quiz.QuizRequestDTO;
import vn.edu.hutech.lms_api.dto.quiz.QuizResponseDTO;
import vn.edu.hutech.lms_api.dto.quiz.QuizResultResponseDTO;
import vn.edu.hutech.lms_api.dto.quiz.QuizSubmitRequestDTO;
import vn.edu.hutech.lms_api.dto.question.QuestionForQuizResponseDTO;

public interface QuizService {
    QuizResponseDTO createQuiz(QuizRequestDTO requestDTO);
    Page<QuizResponseDTO> getQuizzesByCourse(Long courseId, Pageable pageable);
    Page<QuizResponseDTO> getQuizzesByModule(Long moduleId, Pageable pageable);
    QuizResponseDTO getQuizById(Long id);
    QuizResponseDTO updateQuiz(Long id, QuizRequestDTO requestDTO);
    void deleteQuiz(Long id);
    QuizResultResponseDTO submitQuiz(QuizSubmitRequestDTO requestDTO);
    QuizResultResponseDTO getAttemptResult(Long attemptId);
    Page<QuestionForQuizResponseDTO> getQuestionsForQuiz(Long quizId, Pageable pageable);
}
