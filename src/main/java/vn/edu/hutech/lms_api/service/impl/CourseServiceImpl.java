package vn.edu.hutech.lms_api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.hutech.lms_api.domain.Course;
import vn.edu.hutech.lms_api.domain.User;
import vn.edu.hutech.lms_api.dto.course.CourseRequestDTO;
import vn.edu.hutech.lms_api.dto.course.CourseResponseDTO;
import vn.edu.hutech.lms_api.repository.CourseRepository;
import vn.edu.hutech.lms_api.repository.UserRepository;
import vn.edu.hutech.lms_api.service.CourseService;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public CourseResponseDTO createCourse(CourseRequestDTO requestDTO) {
        User instructor = getCurrentUser();

        Course course = Course.builder()
                .title(requestDTO.getTitle())
                .description(requestDTO.getDescription())
                .thumbnailUrl(requestDTO.getThumbnailUrl())
                .price(requestDTO.getPrice())
                .status(requestDTO.getStatus() != null ? requestDTO.getStatus() : "DRAFT")
                .instructor(instructor)
                .build();

        return mapToResponseDTO(courseRepository.save(course));
    }

    @Override
    public Page<CourseResponseDTO> getAllCourses(Pageable pageable) {
        return courseRepository.findAll(pageable).map(this::mapToResponseDTO);
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

        // Nếu không còn course nào, reset sequence để ID có thể bắt đầu lại từ 1
        if (courseRepository.count() == 0) {
            try {
                jdbcTemplate.execute("ALTER SEQUENCE courses_id_seq RESTART WITH 1");
            } catch (Exception ex) {
                // Khong quan trong neu database khong phai Postgres hoac sequence khong ton tai
                // Log nếu cần - hiện tại ném runtime để dễ debug trong môi trường dev
                throw new RuntimeException("Loi khi reset sequence courses_id_seq: " + ex.getMessage(), ex);
            }
        }
    }

    private CourseResponseDTO mapToResponseDTO(Course course) {
        return CourseResponseDTO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .thumbnailUrl(course.getThumbnailUrl())
                .price(course.getPrice())
                .status(course.getStatus())
                .instructorName(course.getInstructor() != null ? course.getInstructor().getFullName() : null)
                .createdAt(course.getCreatedAt())
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
