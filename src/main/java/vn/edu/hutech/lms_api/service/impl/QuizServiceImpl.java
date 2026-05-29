package vn.edu.hutech.lms_api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.edu.hutech.lms_api.domain.Attempt;
import vn.edu.hutech.lms_api.domain.Module;
import vn.edu.hutech.lms_api.domain.Question;
import vn.edu.hutech.lms_api.domain.Quiz;
import vn.edu.hutech.lms_api.domain.User;
import vn.edu.hutech.lms_api.dto.question.QuestionForQuizResponseDTO;
import vn.edu.hutech.lms_api.dto.quiz.QuizRequestDTO;
import vn.edu.hutech.lms_api.dto.quiz.QuizResponseDTO;
import vn.edu.hutech.lms_api.dto.quiz.QuizResultResponseDTO;
import vn.edu.hutech.lms_api.dto.quiz.QuizSubmitRequestDTO;
import vn.edu.hutech.lms_api.repository.AttemptRepository;
import vn.edu.hutech.lms_api.repository.ModuleRepository;
import vn.edu.hutech.lms_api.repository.QuestionRepository;
import vn.edu.hutech.lms_api.repository.QuizRepository;
import vn.edu.hutech.lms_api.repository.UserRepository;
import vn.edu.hutech.lms_api.service.QuizService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final ModuleRepository moduleRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AttemptRepository attemptRepository;

    @Override
    public QuizResponseDTO createQuiz(QuizRequestDTO requestDTO) {
        Module module = moduleRepository.findById(requestDTO.getModuleId())
                .orElseThrow(() -> new RuntimeException("Khong tim thay chuong hoc voi ID: " + requestDTO.getModuleId()));

        Quiz quiz = Quiz.builder()
                .title(requestDTO.getTitle())
                .passingScore(requestDTO.getPassingScore())
                .timeLimitMinutes(requestDTO.getTimeLimitMinutes())
                .module(module)
                .course(module.getCourse())
                .build();

        return mapToResponseDTO(quizRepository.save(quiz));
    }

    @Override
    public Page<QuizResponseDTO> getQuizzesByCourse(Long courseId, Pageable pageable) {
        return quizRepository.findByCourseId(courseId, pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    public Page<QuizResponseDTO> getQuizzesByModule(Long moduleId, Pageable pageable) {
        return quizRepository.findByModuleId(moduleId, pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    public QuizResponseDTO getQuizById(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay bai kiem tra voi ID: " + id));
        return mapToResponseDTO(quiz);
    }

    @Override
    public QuizResponseDTO updateQuiz(Long id, QuizRequestDTO requestDTO) {
        Quiz existingQuiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay bai kiem tra voi ID: " + id));

        Module module = moduleRepository.findById(requestDTO.getModuleId())
                .orElseThrow(() -> new RuntimeException("Khong tim thay chuong hoc voi ID: " + requestDTO.getModuleId()));

        existingQuiz.setTitle(requestDTO.getTitle());
        existingQuiz.setPassingScore(requestDTO.getPassingScore());
        existingQuiz.setTimeLimitMinutes(requestDTO.getTimeLimitMinutes());
        existingQuiz.setModule(module);
        existingQuiz.setCourse(module.getCourse());

        return mapToResponseDTO(quizRepository.save(existingQuiz));
    }

    @Override
    public void deleteQuiz(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay bai kiem tra voi ID: " + id));
        quizRepository.delete(quiz);
    }

    @Override
    public QuizResultResponseDTO submitQuiz(QuizSubmitRequestDTO requestDTO) {
        Quiz quiz = quizRepository.findById(requestDTO.getQuizId())
                .orElseThrow(() -> new RuntimeException("Khong tim thay bai kiem tra voi ID: " + requestDTO.getQuizId()));

        User user = getCurrentUser();
        List<Question> questions = questionRepository.findByQuizId(quiz.getId(), Pageable.unpaged()).getContent();
        int totalQuestions = questions.size();
        int correctCount = 0;

        for (Question question : questions) {
            String userAnswer = requestDTO.getAnswers().get(question.getId());
            if (userAnswer != null && userAnswer.equalsIgnoreCase(question.getCorrectAnswer())) {
                correctCount++;
            }
        }

        int scorePercentage = totalQuestions == 0 ? 0 : (int) (((double) correctCount / totalQuestions) * 100);
        String status = scorePercentage >= quiz.getPassingScore() ? "PASSED" : "FAILED";

        Attempt attempt = Attempt.builder()
                .user(user)
                .quiz(quiz)
                .score(scorePercentage)
                .userAnswers(requestDTO.getAnswers())
                .status(status)
                .build();

        return mapAttemptToResult(attemptRepository.save(attempt), totalQuestions, correctCount);
    }

    @Override
    public QuizResultResponseDTO getAttemptResult(Long attemptId) {
        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Khong tim thay ket qua lam bai voi ID: " + attemptId));
        User currentUser = getCurrentUser();

        if (!attempt.getUser().getId().equals(currentUser.getId())
                && !"INSTRUCTOR".equalsIgnoreCase(currentUser.getRole())
                && !"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            throw new RuntimeException("Ban khong co quyen xem ket qua nay!");
        }

        List<Question> questions = questionRepository.findByQuizId(attempt.getQuiz().getId(), Pageable.unpaged()).getContent();
        int correctCount = 0;
        for (Question question : questions) {
            String userAnswer = attempt.getUserAnswers().get(question.getId());
            if (userAnswer != null && userAnswer.equalsIgnoreCase(question.getCorrectAnswer())) {
                correctCount++;
            }
        }

        return mapAttemptToResult(attempt, questions.size(), correctCount);
    }

    @Override
    public Page<QuestionForQuizResponseDTO> getQuestionsForQuiz(Long quizId, Pageable pageable) {
        if (!quizRepository.existsById(quizId)) {
            throw new RuntimeException("Khong tim thay bai kiem tra voi ID: " + quizId);
        }

        return questionRepository.findByQuizId(quizId, pageable)
                .map(this::mapToQuestionForQuizResponseDTO);
    }

    private QuizResponseDTO mapToResponseDTO(Quiz quiz) {
        Module module = quiz.getModule();
        return QuizResponseDTO.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .passingScore(quiz.getPassingScore())
                .timeLimitMinutes(quiz.getTimeLimitMinutes())
                .courseId(quiz.getCourse().getId())
                .courseTitle(quiz.getCourse().getTitle())
                .moduleId(module != null ? module.getId() : null)
                .moduleTitle(module != null ? module.getTitle() : null)
                .createdAt(quiz.getCreatedAt())
                .build();
    }

    private QuestionForQuizResponseDTO mapToQuestionForQuizResponseDTO(Question question) {
        return QuestionForQuizResponseDTO.builder()
                .id(question.getId())
                .content(question.getContent())
                .options(question.getOptions())
                .quizId(question.getQuiz().getId())
                .build();
    }

    private QuizResultResponseDTO mapAttemptToResult(Attempt attempt, int totalQuestions, int correctCount) {
        return QuizResultResponseDTO.builder()
                .attemptId(attempt.getId())
                .totalQuestions(totalQuestions)
                .correctCount(correctCount)
                .score(attempt.getScore())
                .status(attempt.getStatus())
                .submittedAt(attempt.getSubmittedAt())
                .build();
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new RuntimeException("Ban can dang nhap de thuc hien thao tac nay!");
        }

        String currentUserEmail = authentication.getName();
        return userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Khong tim thay thong tin tai khoan!"));
    }
}
