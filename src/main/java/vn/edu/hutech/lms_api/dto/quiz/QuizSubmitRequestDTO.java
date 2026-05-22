package vn.edu.hutech.lms_api.dto.quiz;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class QuizSubmitRequestDTO {
    @NotNull(message = "ID bai kiem tra khong duoc de trong")
    private Long quizId;

    @NotNull(message = "Danh sach dap an khong duoc de trong")
    private Map<Long, String> answers;
}
