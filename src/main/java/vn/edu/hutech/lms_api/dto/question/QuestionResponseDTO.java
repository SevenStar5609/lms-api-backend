package vn.edu.hutech.lms_api.dto.question;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class QuestionResponseDTO {
    private Long id;
    private String content;
    private Map<String, String> options;
    private String correctAnswer;
    private Long quizId;
    private LocalDateTime createdAt;
}