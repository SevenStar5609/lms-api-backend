package vn.edu.hutech.lms_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.hutech.lms_api.domain.User;

import java.util.Optional; // Nhớ import Optional

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Thêm dòng này: Spring Data JPA sẽ tự động dịch nó thành câu lệnh SQL "SELECT * FROM users WHERE email = ?"
    Optional<User> findByEmail(String email);
    // Đếm tổng số lượng user có role cụ thể (VD: LEARNER)
    long countByRole(String role);
}