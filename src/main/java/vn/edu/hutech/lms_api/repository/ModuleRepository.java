package vn.edu.hutech.lms_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.hutech.lms_api.domain.Module;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    Page<Module> findByCourseIdOrderByOrderIndexAsc(Long courseId, Pageable pageable);
}
