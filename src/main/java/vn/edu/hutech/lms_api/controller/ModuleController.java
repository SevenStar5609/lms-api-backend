package vn.edu.hutech.lms_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.hutech.lms_api.dto.module.ModuleRequestDTO;
import vn.edu.hutech.lms_api.dto.module.ModuleResponseDTO;
import vn.edu.hutech.lms_api.service.ModuleService;

@RestController
@RequestMapping("/api/v1/modules")
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService moduleService;

    @PostMapping
    public ResponseEntity<ModuleResponseDTO> createModule(@Valid @RequestBody ModuleRequestDTO requestDTO) {
        return new ResponseEntity<>(moduleService.createModule(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<Page<ModuleResponseDTO>> getModulesByCourse(@PathVariable Long courseId, Pageable pageable) {
        return ResponseEntity.ok(moduleService.getModulesByCourse(courseId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModuleResponseDTO> getModuleById(@PathVariable Long id) {
        return ResponseEntity.ok(moduleService.getModuleById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ModuleResponseDTO> updateModule(@PathVariable Long id, @Valid @RequestBody ModuleRequestDTO requestDTO) {
        return ResponseEntity.ok(moduleService.updateModule(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteModule(@PathVariable Long id) {
        moduleService.deleteModule(id);
        return ResponseEntity.ok("Da xoa thanh cong chuong hoc co ID: " + id);
    }
}
