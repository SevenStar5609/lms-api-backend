package vn.edu.hutech.lms_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.hutech.lms_api.domain.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // Bạn có tin không, chỉ cần extends JpaRepository là bạn đã được "tặng" sẵn
    // hàng chục hàm như findAll(), findById(), save(), deleteById() rồi đó!

    // Thêm một hàm tìm kiếm nâng cao (Ví dụ: Tìm khóa học theo tiêu đề có chứa từ khóa)
    // Spring Boot sẽ tự động dịch tên hàm này thành câu lệnh SQL LIKE!
    Iterable<Course> findByTitleContainingIgnoreCase(String keyword);
}