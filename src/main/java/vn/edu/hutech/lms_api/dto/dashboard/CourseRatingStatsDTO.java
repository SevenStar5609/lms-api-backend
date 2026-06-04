package vn.edu.hutech.lms_api.dto.dashboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseRatingStatsDTO {
    private Long courseId;
    private String courseTitle;
    private Double averageRating;
    private Long reviewCount;
}
