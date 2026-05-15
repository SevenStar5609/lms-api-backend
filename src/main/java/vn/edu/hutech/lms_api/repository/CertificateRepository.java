package vn.edu.hutech.lms_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.hutech.lms_api.domain.Certificate;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    // Tìm chứng chỉ của một học viên trong một khóa học (để tránh cấp trùng)
    Optional<Certificate> findByUserIdAndCourseId(Long userId, Long courseId);

    // Lấy tất cả chứng chỉ của một học viên
    List<Certificate> findByUserId(Long userId);
}