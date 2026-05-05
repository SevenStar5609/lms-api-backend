package vn.edu.hutech.lms_api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.hutech.lms_api.domain.Course;
import vn.edu.hutech.lms_api.domain.Module;
import vn.edu.hutech.lms_api.dto.module.ModuleRequestDTO;
import vn.edu.hutech.lms_api.dto.module.ModuleResponseDTO;
import vn.edu.hutech.lms_api.repository.CourseRepository;
import vn.edu.hutech.lms_api.repository.ModuleRepository;
import vn.edu.hutech.lms_api.service.ModuleService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModuleServiceImpl implements ModuleService {

    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;

    @Override
    public ModuleResponseDTO createModule(ModuleRequestDTO requestDTO) {
        // 1. Kiểm tra xem Khóa học có tồn tại không
        Course course = courseRepository.findById(requestDTO.getCourseId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Khóa học với ID: " + requestDTO.getCourseId()));

        // 2. Chuyển đổi DTO -> Entity
        Module module = Module.builder()
                .title(requestDTO.getTitle())
                .orderIndex(requestDTO.getOrderIndex())
                .course(course) // Liên kết với khóa học tìm được ở trên
                .build();

        // 3. Lưu xuống DB
        Module savedModule = moduleRepository.save(module);

        // 4. Trả về DTO
        return mapToResponseDTO(savedModule);
    }

    @Override
    public List<ModuleResponseDTO> getModulesByCourse(Long courseId) {
        // Lấy danh sách chương học theo ID khóa học và sắp xếp theo thứ tự
        List<Module> modules = moduleRepository.findByCourseIdOrderByOrderIndexAsc(courseId);
        return modules.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private ModuleResponseDTO mapToResponseDTO(Module module) {
        return ModuleResponseDTO.builder()
                .id(module.getId())
                .title(module.getTitle())
                .orderIndex(module.getOrderIndex())
                .courseId(module.getCourse().getId())
                .courseTitle(module.getCourse().getTitle())
                .createdAt(module.getCreatedAt())
                .build();
    }
}