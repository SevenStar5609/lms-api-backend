package vn.edu.hutech.lms_api.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import vn.edu.hutech.lms_api.service.CloudStorageService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;

@Service
public class CloudinaryStorageServiceImpl implements CloudStorageService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${cloudinary.enabled:false}")
    private boolean enabled;

    @Value("${cloudinary.cloud-name:}")
    private String cloudName;

    @Value("${cloudinary.api-key:}")
    private String apiKey;

    @Value("${cloudinary.api-secret:}")
    private String apiSecret;

    @Override
    public String uploadCertificatePdf(Path filePath, String publicId) {
        if (!enabled) {
            return null;
        }

        validateConfig();

        try {
            long timestamp = System.currentTimeMillis() / 1000;
            String folder = "lms/certificates";
            String signature = sha1("folder=" + folder + "&public_id=" + publicId + "&timestamp=" + timestamp + apiSecret);

            ByteArrayResource fileResource = new ByteArrayResource(Files.readAllBytes(filePath)) {
                @Override
                public String getFilename() {
                    return filePath.getFileName().toString();
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);
            body.add("api_key", apiKey);
            body.add("timestamp", timestamp);
            body.add("public_id", publicId);
            body.add("folder", folder);
            body.add("signature", signature);

            String response = RestClient.create()
                    .post()
                    .uri("https://api.cloudinary.com/v1_1/{cloudName}/raw/upload", cloudName)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(String.class);

            JsonNode json = objectMapper.readTree(response);
            String secureUrl = json.path("secure_url").asText(null);
            if (secureUrl == null || secureUrl.isBlank()) {
                throw new RuntimeException("Cloudinary khong tra ve secure_url");
            }

            return secureUrl;
        } catch (Exception ex) {
            throw new RuntimeException("Khong the upload chung chi len Cloudinary: " + ex.getMessage(), ex);
        }
    }

    private void validateConfig() {
        if (cloudName.isBlank() || apiKey.isBlank() || apiSecret.isBlank()) {
            throw new RuntimeException("Thieu cau hinh Cloudinary: cloud-name, api-key hoac api-secret");
        }
    }

    private String sha1(String value) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        return HexFormat.of().formatHex(digest.digest(value.getBytes()));
    }
}
