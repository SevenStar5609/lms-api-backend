package vn.edu.hutech.lms_api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.edu.hutech.lms_api.dto.review.ReviewRequestDTO;
import vn.edu.hutech.lms_api.dto.review.ReviewResponseDTO;

public interface ReviewService {
    ReviewResponseDTO createOrUpdateReview(ReviewRequestDTO requestDTO);

    Page<ReviewResponseDTO> getCourseReviews(Long courseId, Pageable pageable);

    ReviewResponseDTO getMyReview(Long courseId);

    void deleteReview(Long id);
}
