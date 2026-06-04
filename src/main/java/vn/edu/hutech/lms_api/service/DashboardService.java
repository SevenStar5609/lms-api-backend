package vn.edu.hutech.lms_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.hutech.lms_api.dto.dashboard.CourseRatingStatsDTO;
import vn.edu.hutech.lms_api.dto.dashboard.DashboardResponseDTO;
import vn.edu.hutech.lms_api.repository.AttemptRepository;
import vn.edu.hutech.lms_api.repository.CourseRepository;
import vn.edu.hutech.lms_api.repository.EnrollmentRepository;
import vn.edu.hutech.lms_api.repository.ReviewRepository;
import vn.edu.hutech.lms_api.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AttemptRepository attemptRepository;
    private final CourseRepository courseRepository;
    private final ReviewRepository reviewRepository;

    public DashboardResponseDTO getSystemDashboard() {
        // 1. Đếm tổng học viên
        long totalLearners = userRepository.countByRole("LEARNER");

        // 2. Đếm số lượt ghi danh & hoàn thành
        long totalEnrollments = enrollmentRepository.count();
        long completedCourses = enrollmentRepository.countByStatus("COMPLETED");

        // 3. Tính tỉ lệ hoàn thành (%)
        double completionRate = 0.0;
        if (totalEnrollments > 0) {
            completionRate = ((double) completedCourses / totalEnrollments) * 100;
            completionRate = Math.round(completionRate * 100.0) / 100.0; // Làm tròn 2 chữ số
        }

        // 4. Lấy điểm quiz trung bình
        double averageScore = attemptRepository.getAverageQuizScore();
        averageScore = Math.round(averageScore * 100.0) / 100.0; // Làm tròn

        // 5. Đóng gói trả về
        return DashboardResponseDTO.builder()
                .totalLearners(totalLearners)
                .totalEnrollments(totalEnrollments)
                .completedCourses(completedCourses)
                .completionRate(completionRate)
                .averageQuizScore(averageScore)
                .build();
    }

    public List<CourseRatingStatsDTO> getCourseRatingStats() {
        return courseRepository.findAll().stream()
                .map(course -> CourseRatingStatsDTO.builder()
                        .courseId(course.getId())
                        .courseTitle(course.getTitle())
                        .averageRating(round(reviewRepository.getAverageRatingByCourseId(course.getId())))
                        .reviewCount(reviewRepository.countByCourseId(course.getId()))
                        .build())
                .toList();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
