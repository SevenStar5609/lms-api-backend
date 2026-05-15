package vn.edu.hutech.lms_api.dto.quiz;

import lombok.Data;
import java.util.Map;

@Data
public class QuizSubmitRequestDTO {
    private Long userId;
    private Long quizId;
    // Map chứa: id câu hỏi -> đáp án học viên chọn (A, B, C...)
    private Map<Long, String> answers;
}