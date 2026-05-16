package vn.edu.hutech.lms_api.dto.dashboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardResponseDTO {
    private long totalLearners;       // Tổng số học viên
    private long totalEnrollments;    // Tổng số lượt ghi danh
    private long completedCourses;    // Tổng số lượt đã hoàn thành
    private double completionRate;    // Tỉ lệ hoàn thành (%)
    private double averageQuizScore;  // Điểm trung bình bài kiểm tra
}