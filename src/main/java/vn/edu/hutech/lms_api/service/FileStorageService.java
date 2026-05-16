package vn.edu.hutech.lms_api.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    // Lưu file và trả về tên file đã lưu
    String storeFile(MultipartFile file);

    // Tải file lên để hiển thị/download
    Resource loadFileAsResource(String fileName);
}