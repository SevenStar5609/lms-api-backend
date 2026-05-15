package vn.edu.hutech.lms_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.hutech.lms_api.domain.User;
import vn.edu.hutech.lms_api.dto.auth.AuthResponseDTO;
import vn.edu.hutech.lms_api.dto.auth.LoginRequestDTO;
import vn.edu.hutech.lms_api.dto.auth.RegisterRequestDTO;
import vn.edu.hutech.lms_api.repository.UserRepository;
import vn.edu.hutech.lms_api.security.JwtService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // --- HÀM ĐĂNG KÝ TÀI KHOẢN ---
    public AuthResponseDTO register(RegisterRequestDTO request) {
        // 1. Kiểm tra email đã tồn tại chưa
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email này đã được sử dụng!");
        }

        // 2. Tạo User mới và MÃ HÓA mật khẩu
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole().toUpperCase()) // Viết hoa (VD: LEARNER)
                .isActive(true)
                .build();

        userRepository.save(user);

        // 3. Phát sinh JWT Token
        String jwtToken = jwtService.generateToken(user);

        return AuthResponseDTO.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }

    // --- HÀM ĐĂNG NHẬP ---
    public AuthResponseDTO login(LoginRequestDTO request) {
        // 1. Giao việc kiểm tra đúng/sai mật khẩu cho Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 2. Nếu không văng lỗi -> Mật khẩu đúng. Lấy User ra.
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));

        // 3. Phát sinh JWT Token mới
        String jwtToken = jwtService.generateToken(user);

        return AuthResponseDTO.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }
}