package vn.edu.hutech.lms_api.dto.question;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Map;

@Data
public class QuestionRequestDTO {
    @NotBlank(message = "Nội dung câu hỏi không được để trống")
    private String content;

    @NotNull(message = "Danh sách đáp án không được để trống")
    private Map<String, String> options;

    @NotBlank(message = "Đáp án đúng không được để trống")
    private String correctAnswer;

    @NotNull(message = "ID Bài kiểm tra (Quiz) không được để trống")
    private Long quizId;
}