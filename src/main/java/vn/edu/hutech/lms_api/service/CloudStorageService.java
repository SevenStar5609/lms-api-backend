package vn.edu.hutech.lms_api.service;

import java.nio.file.Path;

public interface CloudStorageService {
    String uploadCertificatePdf(Path filePath, String publicId);
}
