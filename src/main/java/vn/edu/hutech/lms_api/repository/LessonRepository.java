package vn.edu.hutech.lms_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.hutech.lms_api.domain.Lesson;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    Page<Lesson> findByModuleIdOrderByOrderIndexAsc(Long moduleId, Pageable pageable);
    long countByModule_Course_Id(Long courseId);
}
