package vn.edu.hutech.lms_api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.hutech.lms_api.domain.Course;
import vn.edu.hutech.lms_api.domain.Enrollment;
import vn.edu.hutech.lms_api.domain.Review;
import vn.edu.hutech.lms_api.domain.User;
import vn.edu.hutech.lms_api.dto.review.ReviewRequestDTO;
import vn.edu.hutech.lms_api.dto.review.ReviewResponseDTO;
import vn.edu.hutech.lms_api.repository.CourseRepository;
import vn.edu.hutech.lms_api.repository.EnrollmentRepository;
import vn.edu.hutech.lms_api.repository.ReviewRepository;
import vn.edu.hutech.lms_api.repository.UserRepository;
import vn.edu.hutech.lms_api.service.ReviewService;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ReviewResponseDTO createOrUpdateReview(ReviewRequestDTO requestDTO) {
        User currentUser = getCurrentUser();
        Course course = courseRepository.findById(requestDTO.getCourseId())
                .orElseThrow(() -> new RuntimeException("Khong tim thay khoa hoc voi ID: " + requestDTO.getCourseId()));

        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(currentUser.getId(), course.getId())
                .orElseThrow(() -> new RuntimeException("Ban chua ghi danh khoa hoc nay!"));

        if (!"COMPLETED".equalsIgnoreCase(enrollment.getStatus()) && enrollment.getProgress() < 100.0) {
            throw new RuntimeException("Ban can hoan thanh 100% tien do khoa hoc truoc khi danh gia!");
        }

        Review review = reviewRepository.findByUserIdAndCourseId(currentUser.getId(), course.getId())
                .orElseGet(() -> Review.builder()
                        .user(currentUser)
                        .course(course)
                        .build());

        review.setRating(requestDTO.getRating());
        review.setComment(requestDTO.getComment());

        return mapToResponseDTO(reviewRepository.save(review));
    }

    @Override
    public Page<ReviewResponseDTO> getCourseReviews(Long courseId, Pageable pageable) {
        if (!courseRepository.existsById(courseId)) {
            throw new RuntimeException("Khong tim thay khoa hoc voi ID: " + courseId);
        }

        return reviewRepository.findByCourseId(courseId, pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    public ReviewResponseDTO getMyReview(Long courseId) {
        User currentUser = getCurrentUser();
        Review review = reviewRepository.findByUserIdAndCourseId(currentUser.getId(), courseId)
                .orElseThrow(() -> new RuntimeException("Ban chua danh gia khoa hoc nay!"));
        return mapToResponseDTO(review);
    }

    private ReviewResponseDTO mapToResponseDTO(Review review) {
        return ReviewResponseDTO.builder()
                .id(review.getId())
                .courseId(review.getCourse().getId())
                .courseTitle(review.getCourse().getTitle())
                .userId(review.getUser().getId())
                .userFullName(review.getUser().getFullName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new RuntimeException("Ban can dang nhap de thuc hien thao tac nay!");
        }

        String currentUserEmail = authentication.getName();
        return userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Khong tim thay thong tin tai khoan!"));
    }
}
