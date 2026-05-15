package vn.edu.hutech.lms_api.dto.progress;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LessonProgressRequestDTO {
    @NotNull(message = "ID Phiếu ghi danh không được để trống")
    private Long enrollmentId;

    @NotNull(message = "ID Bài học không được để trống")
    private Long lessonId;
}