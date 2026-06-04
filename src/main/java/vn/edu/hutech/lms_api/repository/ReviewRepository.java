package vn.edu.hutech.lms_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.edu.hutech.lms_api.domain.Review;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByUserIdAndCourseId(Long userId, Long courseId);

    Page<Review> findByCourseId(Long courseId, Pageable pageable);

    long countByCourseId(Long courseId);

    @Query("select coalesce(avg(r.rating), 0) from Review r where r.course.id = :courseId")
    double getAverageRatingByCourseId(Long courseId);
}
