package vn.edu.hutech.lms_api.dto.quiz;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class QuizResultResponseDTO {
    private Long attemptId;
    private Integer totalQuestions;
    private Integer correctCount;
    private Integer score; // Phần trăm điểm
    private String status; // PASSED / FAILED
    private LocalDateTime submittedAt;
}