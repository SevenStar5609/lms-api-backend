package vn.edu.hutech.lms_api.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequestDTO {
    @NotBlank(message = "Refresh token khong duoc de trong")
    private String refreshToken;
}
