package vn.edu.hutech.lms_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.hutech.lms_api.domain.Quiz;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    // Lấy danh sách các bài kiểm tra của một khóa học
    List<Quiz> findByCourseId(Long courseId);
}