package vn.edu.hutech.lms_api.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
// FileStorageServiceImpl deprecated - file upload endpoints removed
// implementation retained for reference but not registered as Spring bean

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.hutech.lms_api.service.FileStorageService;

import jakarta.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Deprecated
public class FileStorageServiceImpl implements vn.edu.hutech.lms_api.service.FileStorageService {

    private Path fileStorageLocation;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // Tự động tạo thư mục 'uploads' khi chạy server nếu chưa có
    @PostConstruct
    public void init() {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Không thể tạo thư mục lưu trữ file.", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        // Làm sạch tên file gốc
        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            if (originalFileName.contains("..")) {
                throw new RuntimeException("Tên file chứa ký tự không hợp lệ " + originalFileName);
            }

            // Đổi tên file bằng UUID để tránh bị trùng lặp tên
            String fileExtension = "";
            if (originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String newFileName = UUID.randomUUID().toString() + fileExtension;

            // Copy file vào thư mục đích
            Path targetLocation = this.fileStorageLocation.resolve(newFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return newFileName;
        } catch (Exception ex) {
            throw new RuntimeException("Không thể lưu file " + originalFileName + ". Vui lòng thử lại!", ex);
        }
    }

    @Override
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("Không tìm thấy file " + fileName);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Không tìm thấy file " + fileName, ex);
        }
    }
}