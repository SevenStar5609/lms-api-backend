package vn.edu.hutech.lms_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.hutech.lms_api.domain.Attempt;

import java.util.List;

@Repository
public interface AttemptRepository extends JpaRepository<Attempt, Long> {
    List<Attempt> findByUserIdAndQuizId(Long userId, Long quizId);
}