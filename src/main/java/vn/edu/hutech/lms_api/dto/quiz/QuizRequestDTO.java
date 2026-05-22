package vn.edu.hutech.lms_api.dto.quiz;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuizRequestDTO {
    @NotBlank(message = "Tieu de bai kiem tra khong duoc de trong")
    private String title;

    @NotNull(message = "Diem dat khong duoc de trong")
    private Integer passingScore;

    private Integer timeLimitMinutes;

    @NotNull(message = "ID chuong hoc khong duoc de trong")
    private Long moduleId;
}
