package vn.edu.hutech.lms_api.dto.quiz;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuizRequestDTO {
    @NotBlank(message = "Tiêu đề bài kiểm tra không được để trống")
    private String title;

    @NotNull(message = "Điểm qua môn không được để trống")
    private Integer passingScore;

    // Thời gian làm bài (có thể null nếu bài thi không giới hạn thời gian)
    private Integer timeLimitMinutes;

    @NotNull(message = "ID Khóa học không được để trống")
    private Long courseId;
}