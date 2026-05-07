package vn.edu.hutech.lms_api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.hutech.lms_api.domain.Course;
import vn.edu.hutech.lms_api.domain.User;
import vn.edu.hutech.lms_api.dto.course.CourseRequestDTO;
import vn.edu.hutech.lms_api.dto.course.CourseResponseDTO;
import vn.edu.hutech.lms_api.repository.CourseRepository;
import vn.edu.hutech.lms_api.repository.UserRepository;
import vn.edu.hutech.lms_api.service.CourseService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Lombok tự động tạo Constructor để nhúng Repository vào
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Override
    public CourseResponseDTO createCourse(CourseRequestDTO requestDTO) {
        // 1. Kiểm tra xem Giảng viên có tồn tại không
        User instructor = userRepository.findById(requestDTO.getInstructorId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Giảng viên với ID: " + requestDTO.getInstructorId()));

        // 2. Chuyển đổi dữ liệu từ DTO sang Entity (Domain)
        Course course = Course.builder()
                .title(requestDTO.getTitle())
                .description(requestDTO.getDescription())
                .thumbnailUrl(requestDTO.getThumbnailUrl())
                .status(requestDTO.getStatus() != null ? requestDTO.getStatus() : "DRAFT")
                .instructor(instructor)
                .build();

        // 3. Lưu vào Database
        Course savedCourse = courseRepository.save(course);

        // 4. Trả về Response DTO (giấu các thông tin nhạy cảm đi)
        return mapToResponseDTO(savedCourse);
    }

    @Override
    public List<CourseResponseDTO> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        // Chuyển một List<Course> thành List<CourseResponseDTO>
        return courses.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Hàm phụ trợ dùng chung để ánh xạ dữ liệu
    private CourseResponseDTO mapToResponseDTO(Course course) {
        return CourseResponseDTO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .thumbnailUrl(course.getThumbnailUrl())
                .status(course.getStatus())
                .instructorName(course.getInstructor().getFullName())
                .createdAt(course.getCreatedAt())
                .build();
    }

    @Override
    public CourseResponseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Khóa học với ID: " + id));
        return mapToResponseDTO(course);
    }

    @Override
    public CourseResponseDTO updateCourse(Long id, CourseRequestDTO requestDTO) {
        // 1. Tìm khóa học cũ
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Khóa học với ID: " + id));

        // 2. Kiểm tra giảng viên mới có tồn tại không
        User instructor = userRepository.findById(requestDTO.getInstructorId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Giảng viên với ID: " + requestDTO.getInstructorId()));

        // 3. Cập nhật dữ liệu
        existingCourse.setTitle(requestDTO.getTitle());
        existingCourse.setDescription(requestDTO.getDescription());
        existingCourse.setThumbnailUrl(requestDTO.getThumbnailUrl());
        existingCourse.setStatus(requestDTO.getStatus() != null ? requestDTO.getStatus() : "DRAFT");
        existingCourse.setInstructor(instructor);

        // 4. Lưu lại
        Course updatedCourse = courseRepository.save(existingCourse);
        return mapToResponseDTO(updatedCourse);
    }

    @Override
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Khóa học với ID: " + id));
        courseRepository.delete(course);
    }
}