package vn.edu.hutech.lms_api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// Đảm bảo import ĐÚNG các Entity trong thư mục domain của dự án
import vn.edu.hutech.lms_api.domain.Attempt;
import vn.edu.hutech.lms_api.domain.Course;
import vn.edu.hutech.lms_api.domain.Question;
import vn.edu.hutech.lms_api.domain.Quiz;
import vn.edu.hutech.lms_api.domain.User;

import vn.edu.hutech.lms_api.dto.quiz.QuizRequestDTO;
import vn.edu.hutech.lms_api.dto.quiz.QuizResponseDTO;
import vn.edu.hutech.lms_api.dto.quiz.QuizResultResponseDTO;
import vn.edu.hutech.lms_api.dto.quiz.QuizSubmitRequestDTO;

// Import ĐẦY ĐỦ các công cụ tương tác Database
import vn.edu.hutech.lms_api.repository.AttemptRepository;
import vn.edu.hutech.lms_api.repository.CourseRepository;
import vn.edu.hutech.lms_api.repository.QuestionRepository;
import vn.edu.hutech.lms_api.repository.QuizRepository;
import vn.edu.hutech.lms_api.repository.UserRepository;

import vn.edu.hutech.lms_api.service.QuizService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    // Khai báo ĐẦY ĐỦ 5 Repository cần thiết cho nghiệp vụ này
    private final QuizRepository quizRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AttemptRepository attemptRepository;

    @Override
    public QuizResponseDTO createQuiz(QuizRequestDTO requestDTO) {
        Course course = courseRepository.findById(requestDTO.getCourseId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Khóa học với ID: " + requestDTO.getCourseId()));

        Quiz quiz = Quiz.builder()
                .title(requestDTO.getTitle())
                .passingScore(requestDTO.getPassingScore())
                .timeLimitMinutes(requestDTO.getTimeLimitMinutes())
                .course(course)
                .build();

        return mapToResponseDTO(quizRepository.save(quiz));
    }

    @Override
    public List<QuizResponseDTO> getQuizzesByCourse(Long courseId) {
        return quizRepository.findByCourseId(courseId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public QuizResponseDTO getQuizById(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Bài kiểm tra với ID: " + id));
        return mapToResponseDTO(quiz);
    }

    @Override
    public QuizResponseDTO updateQuiz(Long id, QuizRequestDTO requestDTO) {
        Quiz existingQuiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Bài kiểm tra với ID: " + id));

        Course course = courseRepository.findById(requestDTO.getCourseId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Khóa học với ID: " + requestDTO.getCourseId()));

        existingQuiz.setTitle(requestDTO.getTitle());
        existingQuiz.setPassingScore(requestDTO.getPassingScore());
        existingQuiz.setTimeLimitMinutes(requestDTO.getTimeLimitMinutes());
        existingQuiz.setCourse(course);

        return mapToResponseDTO(quizRepository.save(existingQuiz));
    }

    @Override
    public void deleteQuiz(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Bài kiểm tra với ID: " + id));
        quizRepository.delete(quiz);
    }

    // --- LOGIC CHẤM ĐIỂM (ATTEMPT) ---
    @Override
    public QuizResultResponseDTO submitQuiz(QuizSubmitRequestDTO requestDTO) {
        // 1. Lấy thông tin Quiz và User
        Quiz quiz = quizRepository.findById(requestDTO.getQuizId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Bài kiểm tra với ID: " + requestDTO.getQuizId()));

        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User với ID: " + requestDTO.getUserId()));

        // 2. Lấy danh sách câu hỏi để đối chiếu
        List<Question> questions = questionRepository.findByQuizId(quiz.getId());
        int totalQuestions = questions.size();
        int correctCount = 0;

        // 3. Chấm điểm từng câu
        for (Question q : questions) {
            String userAnswer = requestDTO.getAnswers().get(q.getId());
            // Dùng equalsIgnoreCase để A hay a đều tính là đúng
            if (userAnswer != null && userAnswer.equalsIgnoreCase(q.getCorrectAnswer())) {
                correctCount++;
            }
        }

        // 4. Tính toán kết quả
        int scorePercentage = (totalQuestions == 0) ? 0 : (int) (((double) correctCount / totalQuestions) * 100);
        String status = (scorePercentage >= quiz.getPassingScore()) ? "PASSED" : "FAILED";

        // 5. Lưu kết quả xuống Database
        Attempt attempt = Attempt.builder()
                .user(user)
                .quiz(quiz)
                .score(scorePercentage)
                .userAnswers(requestDTO.getAnswers())
                .status(status)
                .build();

        Attempt savedAttempt = attemptRepository.save(attempt);

        // 6. Trả về kết quả hiển thị
        return QuizResultResponseDTO.builder()
                .attemptId(savedAttempt.getId())
                .totalQuestions(totalQuestions)
                .correctCount(correctCount)
                .score(scorePercentage)
                .status(status)
                .submittedAt(savedAttempt.getSubmittedAt())
                .build();
    }

    // --- HÀM PHỤ TRỢ ---
    private QuizResponseDTO mapToResponseDTO(Quiz quiz) {
        return QuizResponseDTO.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .passingScore(quiz.getPassingScore())
                .timeLimitMinutes(quiz.getTimeLimitMinutes())
                .courseId(quiz.getCourse().getId())
                .courseTitle(quiz.getCourse().getTitle())
                .createdAt(quiz.getCreatedAt())
                .build();
    }
}