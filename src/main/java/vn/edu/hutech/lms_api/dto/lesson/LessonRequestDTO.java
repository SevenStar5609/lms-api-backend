package vn.edu.hutech.lms_api.dto.lesson;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LessonRequestDTO {

    @NotBlank(message = "Tên bài học không được để trống")
    private String title;

    @NotBlank(message = "Loại nội dung không được để trống (VD: VIDEO, DOC, QUIZ)")
    private String contentType;

    private String contentUrl; // Link video hoặc file PDF (có thể để trống nếu chỉ có text)

    private String contentBody; // Nội dung văn bản (có thể để trống nếu là video)

    @NotNull(message = "Số thứ tự không được để trống")
    private Integer orderIndex;

    @NotNull(message = "ID Chương học không được để trống")
    private Long moduleId;
}