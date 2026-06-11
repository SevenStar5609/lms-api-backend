package vn.edu.hutech.lms_api.dto.dashboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseProgressStatsDTO {
    private Long courseId;
    private String courseTitle;
    private Long learnerCount;
    private Double averageProgress; // percent
    private Double averageLearningHours; // hours
    private Long completedCount;

    // distribution percentages
    private Double pctLess25;
    private Double pct25to49;
    private Double pct50to74;
    private Double pct75to99;
    private Double pct100;
}
