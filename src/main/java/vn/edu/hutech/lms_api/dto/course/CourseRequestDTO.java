package vn.edu.hutech.lms_api.dto.course;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CourseRequestDTO {

    // @NotBlank là validation của Spring Boot, đảm bảo title không được để trống
    @NotBlank(message = "Tên khóa học không được để trống")
    private String title;

    private String description;

    private String thumbnailUrl;

    private BigDecimal price;

    private String status;

    // Khi tạo khóa học, chúng ta chỉ cần truyền lên ID của người dạy,
    // không cần truyền nguyên một object User cồng kềnh.
    private Long instructorId;

    private String duration;

    private Integer sessionCount;
}
