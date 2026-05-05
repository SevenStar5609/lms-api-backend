package vn.edu.hutech.lms_api.service;

import vn.edu.hutech.lms_api.dto.course.CourseRequestDTO;
import vn.edu.hutech.lms_api.dto.course.CourseResponseDTO;
import java.util.List;

public interface CourseService {
    CourseResponseDTO createCourse(CourseRequestDTO requestDTO);
    List<CourseResponseDTO> getAllCourses();
}