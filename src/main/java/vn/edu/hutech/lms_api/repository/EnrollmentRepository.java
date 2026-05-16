package vn.edu.hutech.lms_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.hutech.lms_api.domain.Enrollment;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    // Tìm phiếu ghi danh theo User ID và Course ID (để check xem đã đăng ký chưa)
    Optional<Enrollment> findByUserIdAndCourseId(Long userId, Long courseId);

    // Lấy danh sách các khóa học mà một user đã đăng ký
    List<Enrollment> findByUserId(Long userId);

    // Đếm tổng số lượt ghi danh có trạng thái cụ thể (VD: COMPLETED)
    long countByStatus(String status);
}