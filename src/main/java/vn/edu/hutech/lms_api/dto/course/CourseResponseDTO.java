package vn.edu.hutech.lms_api.dto.course;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class CourseResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private Double price;
    private String status;
    private String instructorName; // Chỉ trả về tên người dạy, giấu ID và Email đi
    private LocalDateTime createdAt;
}
