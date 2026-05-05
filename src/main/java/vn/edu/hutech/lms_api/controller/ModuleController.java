package vn.edu.hutech.lms_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.hutech.lms_api.dto.module.ModuleRequestDTO;
import vn.edu.hutech.lms_api.dto.module.ModuleResponseDTO;
import vn.edu.hutech.lms_api.service.ModuleService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/modules")
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService moduleService;

    // API Tạo Chương học mới
    @PostMapping
    public ResponseEntity<ModuleResponseDTO> createModule(@Valid @RequestBody ModuleRequestDTO requestDTO) {
        ModuleResponseDTO response = moduleService.createModule(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // API Lấy danh sách Chương học CỦA MỘT KHÓA HỌC CỤ THỂ
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<ModuleResponseDTO>> getModulesByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(moduleService.getModulesByCourse(courseId));
    }
}