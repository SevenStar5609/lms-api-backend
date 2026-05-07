package vn.edu.hutech.lms_api.service;

import vn.edu.hutech.lms_api.dto.module.ModuleRequestDTO;
import vn.edu.hutech.lms_api.dto.module.ModuleResponseDTO;
import java.util.List;

public interface ModuleService {
    ModuleResponseDTO createModule(ModuleRequestDTO requestDTO);
    List<ModuleResponseDTO> getModulesByCourse(Long courseId);

    ModuleResponseDTO getModuleById(Long id);
    ModuleResponseDTO updateModule(Long id, ModuleRequestDTO requestDTO);
    void deleteModule(Long id);
}