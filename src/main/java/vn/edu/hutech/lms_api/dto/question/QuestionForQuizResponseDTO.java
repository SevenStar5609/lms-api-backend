package vn.edu.hutech.lms_api.dto.question;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class QuestionForQuizResponseDTO {
    private Long id;
    private String content;
    private Map<String, String> options;
    private Long quizId;
}
