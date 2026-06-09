package vn.edu.hutech.lms_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.hutech.lms_api.domain.Enrollment;
import vn.edu.hutech.lms_api.domain.LessonProgress;
import vn.edu.hutech.lms_api.dto.dashboard.CourseProgressStatsDTO;
import vn.edu.hutech.lms_api.dto.dashboard.CourseRatingStatsDTO;
import vn.edu.hutech.lms_api.dto.dashboard.DashboardResponseDTO;
import vn.edu.hutech.lms_api.repository.AttemptRepository;
import vn.edu.hutech.lms_api.repository.CourseRepository;
import vn.edu.hutech.lms_api.repository.EnrollmentRepository;
import vn.edu.hutech.lms_api.repository.LessonProgressRepository;
import vn.edu.hutech.lms_api.repository.ReviewRepository;
import vn.edu.hutech.lms_api.repository.UserRepository;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AttemptRepository attemptRepository;
    private final CourseRepository courseRepository;
    private final ReviewRepository reviewRepository;
    private final LessonProgressRepository lessonProgressRepository;

    public DashboardResponseDTO getSystemDashboard() {
        long totalLearners = userRepository.countByRole("LEARNER");
        long totalEnrollments = enrollmentRepository.count();
        long completedCourses = enrollmentRepository.countByStatus("COMPLETED");

        double completionRate = 0.0;
        if (totalEnrollments > 0) {
            completionRate = ((double) completedCourses / totalEnrollments) * 100;
            completionRate = Math.round(completionRate * 100.0) / 100.0;
        }

        double averageScore = attemptRepository.getAverageQuizScore();
        averageScore = Math.round(averageScore * 100.0) / 100.0;

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

    public List<CourseProgressStatsDTO> getCourseProgressStats() {
        return courseRepository.findAll().stream().map(course -> {
            long learnerCount = enrollmentRepository.countByCourseId(course.getId());
            List<Enrollment> enrollments = enrollmentRepository.findByCourseId(course.getId());

            double avgProgress = enrollments.stream()
                    .mapToDouble(Enrollment::getProgress)
                    .average().orElse(0.0);
            avgProgress = Math.round(avgProgress * 100.0) / 100.0;

            long completedCount = enrollments.stream().filter(e -> "COMPLETED".equalsIgnoreCase(e.getStatus())).count();

            // average learning time in hours: use latest lesson completed time minus enrollment createdAt
            double totalHours = 0.0;
            int timeCount = 0;
            for (Enrollment e : enrollments) {
                Optional<LessonProgress> lp = lessonProgressRepository.findTopByEnrollmentIdAndIsCompletedTrueOrderByCompletedAtDesc(e.getId());
                if (lp.isPresent() && e.getCreatedAt() != null && lp.get().getCompletedAt() != null) {
                    Duration dur = Duration.between(e.getCreatedAt(), lp.get().getCompletedAt());
                    totalHours += (double) dur.toMinutes() / 60.0;
                    timeCount++;
                }
            }
            double avgLearningHours = timeCount == 0 ? 0.0 : Math.round((totalHours / timeCount) * 100.0) / 100.0;

            long less25 = enrollments.stream().filter(en -> en.getProgress() < 25).count();
            long p25_49 = enrollments.stream().filter(en -> en.getProgress() >= 25 && en.getProgress() < 50).count();
            long p50_74 = enrollments.stream().filter(en -> en.getProgress() >= 50 && en.getProgress() < 75).count();
            long p75_99 = enrollments.stream().filter(en -> en.getProgress() >= 75 && en.getProgress() < 100).count();
            long p100 = enrollments.stream().filter(en -> en.getProgress() >= 100).count();

            double total = enrollments.size() == 0 ? 1 : enrollments.size();
            double pctLess25 = Math.round((less25 / total * 100.0) * 100.0) / 100.0;
            double pct25_49 = Math.round((p25_49 / total * 100.0) * 100.0) / 100.0;
            double pct50_74 = Math.round((p50_74 / total * 100.0) * 100.0) / 100.0;
            double pct75_99 = Math.round((p75_99 / total * 100.0) * 100.0) / 100.0;
            double pct100 = Math.round((p100 / total * 100.0) * 100.0) / 100.0;

            return CourseProgressStatsDTO.builder()
                    .courseId(course.getId())
                    .courseTitle(course.getTitle())
                    .learnerCount(learnerCount)
                    .averageProgress(avgProgress)
                    .averageLearningHours(avgLearningHours)
                    .completedCount(completedCount)
                    .pctLess25(pctLess25)
                    .pct25to49(pct25_49)
                    .pct50to74(pct50_74)
                    .pct75to99(pct75_99)
                    .pct100(pct100)
                    .build();
        }).toList();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
