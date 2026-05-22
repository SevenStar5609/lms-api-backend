package vn.edu.hutech.lms_api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.edu.hutech.lms_api.dto.module.ModuleRequestDTO;
import vn.edu.hutech.lms_api.dto.module.ModuleResponseDTO;

public interface ModuleService {
    ModuleResponseDTO createModule(ModuleRequestDTO requestDTO);
    Page<ModuleResponseDTO> getModulesByCourse(Long courseId, Pageable pageable);
    ModuleResponseDTO getModuleById(Long id);
    ModuleResponseDTO updateModule(Long id, ModuleRequestDTO requestDTO);
    void deleteModule(Long id);
}
