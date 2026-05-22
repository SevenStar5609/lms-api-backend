package vn.edu.hutech.lms_api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.edu.hutech.lms_api.domain.Question;
import vn.edu.hutech.lms_api.domain.Quiz;
import vn.edu.hutech.lms_api.dto.question.QuestionRequestDTO;
import vn.edu.hutech.lms_api.dto.question.QuestionResponseDTO;
import vn.edu.hutech.lms_api.repository.QuestionRepository;
import vn.edu.hutech.lms_api.repository.QuizRepository;
import vn.edu.hutech.lms_api.service.QuestionService;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;

    @Override
    public QuestionResponseDTO createQuestion(QuestionRequestDTO requestDTO) {
        // 1. Tìm Quiz (Bài kiểm tra)
        Quiz quiz = quizRepository.findById(requestDTO.getQuizId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Bài kiểm tra với ID: " + requestDTO.getQuizId()));

        // 2. Tạo Câu hỏi mới
        Question question = Question.builder()
                .content(requestDTO.getContent())
                .options(requestDTO.getOptions())
                .correctAnswer(requestDTO.getCorrectAnswer())
                .quiz(quiz)
                .build();

        return mapToResponseDTO(questionRepository.save(question));
    }

    @Override
    public Page<QuestionResponseDTO> getQuestionsByQuiz(Long quizId, Pageable pageable) {
        return questionRepository.findByQuizId(quizId, pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    public QuestionResponseDTO getQuestionById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Câu hỏi với ID: " + id));
        return mapToResponseDTO(question);
    }

    @Override
    public QuestionResponseDTO updateQuestion(Long id, QuestionRequestDTO requestDTO) {
        Question existingQuestion = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Câu hỏi với ID: " + id));

        Quiz quiz = quizRepository.findById(requestDTO.getQuizId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Bài kiểm tra với ID: " + requestDTO.getQuizId()));

        existingQuestion.setContent(requestDTO.getContent());
        existingQuestion.setOptions(requestDTO.getOptions());
        existingQuestion.setCorrectAnswer(requestDTO.getCorrectAnswer());
        existingQuestion.setQuiz(quiz);

        return mapToResponseDTO(questionRepository.save(existingQuestion));
    }

    @Override
    public void deleteQuestion(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Câu hỏi với ID: " + id));
        questionRepository.delete(question);
    }

    // Hàm phụ trợ dùng chung để map Entity -> DTO
    private QuestionResponseDTO mapToResponseDTO(Question question) {
        return QuestionResponseDTO.builder()
                .id(question.getId())
                .content(question.getContent())
                .options(question.getOptions())
                .correctAnswer(question.getCorrectAnswer())
                .quizId(question.getQuiz().getId())
                .createdAt(question.getCreatedAt())
                .build();
    }
}
