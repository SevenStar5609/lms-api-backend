package vn.edu.hutech.lms_api.dto.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponseDTO {
    private String token;
    private String email;
    private String fullName;
    private String role;
}