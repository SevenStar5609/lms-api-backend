package vn.edu.hutech.lms_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {


    // Upload/download feature removed - dashboard replaces file management
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam(name = "file", required = false) MultipartFile file) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND)
                .body("File upload has been removed. Use dashboard endpoints for analytics.");
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<String> downloadFile(@PathVariable String fileName) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND)
                .body("File download has been removed.");
    }
}