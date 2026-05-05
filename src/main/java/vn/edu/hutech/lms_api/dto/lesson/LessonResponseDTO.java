package vn.edu.hutech.lms_api.dto.lesson;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class LessonResponseDTO {
    private Long id;
    private String title;
    private String contentType;
    private String contentUrl;
    private String contentBody;
    private Integer orderIndex;
    private Long moduleId;
    private String moduleTitle; // Kèm theo tên chương học cho dễ nhìn
    private LocalDateTime createdAt;
}