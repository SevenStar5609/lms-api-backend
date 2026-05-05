package vn.edu.hutech.lms_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.hutech.lms_api.domain.Module;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

    // Phép thuật đây: Spring Boot tự hiểu hàm này là:
    // SELECT * FROM modules WHERE course_id = ? ORDER BY order_index ASC
    List<Module> findByCourseIdOrderByOrderIndexAsc(Long courseId);
}