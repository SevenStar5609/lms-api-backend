package vn.edu.hutech.lms_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.hutech.lms_api.domain.Quiz;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Page<Quiz> findByCourseId(Long courseId, Pageable pageable);
    Page<Quiz> findByModuleId(Long moduleId, Pageable pageable);
}
