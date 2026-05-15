package vn.edu.hutech.lms_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.hutech.lms_api.domain.LessonProgress;

import java.util.Optional;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {

    // Tìm tiến độ của một bài học cụ thể trong một phiếu ghi danh
    Optional<LessonProgress> findByEnrollmentIdAndLessonId(Long enrollmentId, Long lessonId);

    // Đếm số lượng bài học đã hoàn thành của một phiếu ghi danh
    long countByEnrollmentIdAndIsCompletedTrue(Long enrollmentId);
}