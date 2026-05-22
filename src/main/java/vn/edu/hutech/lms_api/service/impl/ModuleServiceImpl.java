package vn.edu.hutech.lms_api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.hutech.lms_api.domain.Course;
import vn.edu.hutech.lms_api.domain.Module;
import vn.edu.hutech.lms_api.dto.module.ModuleRequestDTO;
import vn.edu.hutech.lms_api.dto.module.ModuleResponseDTO;
import vn.edu.hutech.lms_api.repository.CourseRepository;
import vn.edu.hutech.lms_api.repository.ModuleRepository;
import vn.edu.hutech.lms_api.service.ModuleService;

@Service
@RequiredArgsConstructor
public class ModuleServiceImpl implements ModuleService {

    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public ModuleResponseDTO createModule(ModuleRequestDTO requestDTO) {
        Course course = courseRepository.findById(requestDTO.getCourseId())
                .orElseThrow(() -> new RuntimeException("Khong tim thay khoa hoc voi ID: " + requestDTO.getCourseId()));

        Module module = Module.builder()
                .title(requestDTO.getTitle())
                .orderIndex(requestDTO.getOrderIndex())
                .course(course)
                .build();

        return mapToResponseDTO(moduleRepository.save(module));
    }

    @Override
    public Page<ModuleResponseDTO> getModulesByCourse(Long courseId, Pageable pageable) {
        return moduleRepository.findByCourseIdOrderByOrderIndexAsc(courseId, pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    public ModuleResponseDTO getModuleById(Long id) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay chuong hoc voi ID: " + id));
        return mapToResponseDTO(module);
    }

    @Override
    public ModuleResponseDTO updateModule(Long id, ModuleRequestDTO requestDTO) {
        Module existingModule = moduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay chuong hoc voi ID: " + id));

        Course course = courseRepository.findById(requestDTO.getCourseId())
                .orElseThrow(() -> new RuntimeException("Khong tim thay khoa hoc voi ID: " + requestDTO.getCourseId()));

        existingModule.setTitle(requestDTO.getTitle());
        existingModule.setOrderIndex(requestDTO.getOrderIndex());
        existingModule.setCourse(course);

        return mapToResponseDTO(moduleRepository.save(existingModule));
    }

    @Override
    @Transactional
    public void deleteModule(Long id) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay chuong hoc voi ID: " + id));
        moduleRepository.delete(module);

        // Nếu không còn module nào, reset sequence để ID có thể bắt đầu lại từ 1
        if (moduleRepository.count() == 0) {
            try {
                jdbcTemplate.execute("ALTER SEQUENCE modules_id_seq RESTART WITH 1");
            } catch (Exception ex) {
                throw new RuntimeException("Loi khi reset sequence modules_id_seq: " + ex.getMessage(), ex);
            }
        }
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
