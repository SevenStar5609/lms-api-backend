package vn.edu.hutech.lms_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.hutech.lms_api.dto.review.ReviewRequestDTO;
import vn.edu.hutech.lms_api.dto.review.ReviewResponseDTO;
import vn.edu.hutech.lms_api.service.ReviewService;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponseDTO> createOrUpdateReview(@Valid @RequestBody ReviewRequestDTO requestDTO) {
        return ResponseEntity.ok(reviewService.createOrUpdateReview(requestDTO));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<Page<ReviewResponseDTO>> getCourseReviews(@PathVariable Long courseId, Pageable pageable) {
        return ResponseEntity.ok(reviewService.getCourseReviews(courseId, pageable));
    }

    @GetMapping("/me/course/{courseId}")
    public ResponseEntity<ReviewResponseDTO> getMyReview(@PathVariable Long courseId) {
        return ResponseEntity.ok(reviewService.getMyReview(courseId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok("Da xoa danh gia thanh cong.");
    }
}
