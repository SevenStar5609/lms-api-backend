package vn.edu.hutech.lms_api.dto.module;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ModuleResponseDTO {
    private Long id;
    private String title;
    private Integer orderIndex;
    private Long courseId;
    private String courseTitle; // Kèm theo tên khóa học cho Frontend dễ hiển thị
    private LocalDateTime createdAt;
}