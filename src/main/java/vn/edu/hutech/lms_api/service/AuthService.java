package vn.edu.hutech.lms_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.hutech.lms_api.domain.User;
import vn.edu.hutech.lms_api.dto.auth.AuthResponseDTO;
import vn.edu.hutech.lms_api.dto.auth.LoginRequestDTO;
import vn.edu.hutech.lms_api.dto.auth.RefreshTokenRequestDTO;
import vn.edu.hutech.lms_api.dto.auth.RegisterRequestDTO;
import vn.edu.hutech.lms_api.repository.UserRepository;
import vn.edu.hutech.lms_api.security.JwtService;

import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email nay da duoc su dung!");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole().toUpperCase())
                .isActive(true)
                .build();

        userRepository.save(user);
        return buildTokenResponse(user);
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Tai khoan khong ton tai!"));

        return buildTokenResponse(user);
    }

    public AuthResponseDTO refresh(RefreshTokenRequestDTO request) {
        String refreshToken = request.getRefreshToken();
        String email = jwtService.extractUsername(refreshToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Refresh token khong hop le!"));

        if (!jwtService.isRefreshTokenValid(refreshToken, user)
                || user.getRefreshToken() == null
                || !user.getRefreshToken().equals(refreshToken)) {
            throw new RuntimeException("Refresh token khong hop le hoac da bi thu hoi!");
        }

        return buildTokenResponse(user);
    }

    private AuthResponseDTO buildTokenResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        persistRefreshToken(user, refreshToken);

        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .token(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }

    private void persistRefreshToken(User user, String refreshToken) {
        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiresAt(jwtService.extractExpirationDate(refreshToken)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime());
        userRepository.save(user);
    }
}
