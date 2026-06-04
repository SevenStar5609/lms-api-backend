package vn.edu.hutech.lms_api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.edu.hutech.lms_api.dto.course.CourseRequestDTO;
import vn.edu.hutech.lms_api.dto.course.CourseResponseDTO;
import vn.edu.hutech.lms_api.dto.course.CourseSearchCriteria;

public interface CourseService {
    CourseResponseDTO createCourse(CourseRequestDTO requestDTO);
    Page<CourseResponseDTO> getAllCourses(CourseSearchCriteria criteria, Pageable pageable);
    CourseResponseDTO getCourseById(Long id);
    CourseResponseDTO updateCourse(Long id, CourseRequestDTO requestDTO);
    void deleteCourse(Long id);
}
