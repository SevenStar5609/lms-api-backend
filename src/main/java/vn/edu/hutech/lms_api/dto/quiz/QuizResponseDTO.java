package vn.edu.hutech.lms_api.dto.quiz;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class QuizResponseDTO {
    private Long id;
    private String title;
    private Integer passingScore;
    private Integer timeLimitMinutes;
    private Long courseId;
    private String courseTitle;
    private Long moduleId;
    private String moduleTitle;
    private LocalDateTime createdAt;
}
