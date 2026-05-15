package vn.edu.hutech.lms_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.hutech.lms_api.domain.Lesson;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    // Tìm danh sách bài học theo ID chương học, sắp xếp theo thứ tự orderIndex
    List<Lesson> findByModuleIdOrderByOrderIndexAsc(Long moduleId);

    // Đếm tổng số bài học trong một khóa học (thông qua Module)
    long countByModule_Course_Id(Long courseId);
}