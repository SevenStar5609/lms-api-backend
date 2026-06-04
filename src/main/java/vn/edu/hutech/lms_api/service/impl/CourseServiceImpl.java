package vn.edu.hutech.lms_api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.hutech.lms_api.domain.Course;
import vn.edu.hutech.lms_api.domain.User;
import vn.edu.hutech.lms_api.dto.course.CourseRequestDTO;
import vn.edu.hutech.lms_api.dto.course.CourseResponseDTO;
import vn.edu.hutech.lms_api.dto.course.CourseSearchCriteria;
import vn.edu.hutech.lms_api.repository.CourseRepository;
import vn.edu.hutech.lms_api.repository.ModuleRepository;
import vn.edu.hutech.lms_api.repository.ReviewRepository;
import vn.edu.hutech.lms_api.repository.UserRepository;
import vn.edu.hutech.lms_api.service.CourseService;
import vn.edu.hutech.lms_api.specification.CourseSpecifications;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public CourseResponseDTO createCourse(CourseRequestDTO requestDTO) {
        User instructor = getCurrentUser();

        Course course = Course.builder()
                .title(requestDTO.getTitle())
                .description(requestDTO.getDescription())
                .thumbnailUrl(requestDTO.getThumbnailUrl())
                .price(requestDTO.getPrice())
                .duration(requestDTO.getDuration())
                .sessionCount(requestDTO.getSessionCount())
                .status(requestDTO.getStatus() != null ? requestDTO.getStatus() : "DRAFT")
                .instructor(instructor)
                .build();

        return mapToResponseDTO(courseRepository.save(course));
    }

    @Override
    public Page<CourseResponseDTO> getAllCourses(CourseSearchCriteria criteria, Pageable pageable) {
        return courseRepository.findAll(CourseSpecifications.byCriteria(criteria), pageable).map(this::mapToResponseDTO);
    }

    @Override
    public CourseResponseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay khoa hoc voi ID: " + id));
        return mapToResponseDTO(course);
    }

    @Override
    public CourseResponseDTO updateCourse(Long id, CourseRequestDTO requestDTO) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay khoa hoc voi ID: " + id));

        User instructor = requestDTO.getInstructorId() != null
                ? userRepository.findById(requestDTO.getInstructorId())
                .orElseThrow(() -> new RuntimeException("Khong tim thay giang vien voi ID: " + requestDTO.getInstructorId()))
                : getCurrentUser();

        existingCourse.setTitle(requestDTO.getTitle());
        existingCourse.setDescription(requestDTO.getDescription());
        existingCourse.setThumbnailUrl(requestDTO.getThumbnailUrl());
        existingCourse.setPrice(requestDTO.getPrice());
        existingCourse.setDuration(requestDTO.getDuration());
        existingCourse.setSessionCount(requestDTO.getSessionCount());
        existingCourse.setStatus(requestDTO.getStatus() != null ? requestDTO.getStatus() : "DRAFT");
        existingCourse.setInstructor(instructor);

        return mapToResponseDTO(courseRepository.save(existingCourse));
    }

    @Override
    @Transactional
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay khoa hoc voi ID: " + id));
        courseRepository.delete(course);
        courseRepository.flush();

        if (moduleRepository.count() == 0) {
            resetSequence("modules_id_seq");
        }

        if (courseRepository.count() == 0) {
            resetSequence("courses_id_seq");
        }
    }

    private CourseResponseDTO mapToResponseDTO(Course course) {
        return CourseResponseDTO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .thumbnailUrl(course.getThumbnailUrl())
                .price(course.getPrice())
                .duration(course.getDuration())
                .sessionCount(course.getSessionCount())
                .status(course.getStatus())
                .instructorName(course.getInstructor() != null ? course.getInstructor().getFullName() : null)
                .averageRating(round(reviewRepository.getAverageRatingByCourseId(course.getId())))
                .reviewCount(reviewRepository.countByCourseId(course.getId()))
                .createdAt(course.getCreatedAt())
                .build();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private void resetSequence(String sequenceName) {
        try {
            jdbcTemplate.execute("ALTER SEQUENCE " + sequenceName + " RESTART WITH 1");
        } catch (Exception ex) {
            throw new RuntimeException("Loi khi reset sequence " + sequenceName + ": " + ex.getMessage(), ex);
        }
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
