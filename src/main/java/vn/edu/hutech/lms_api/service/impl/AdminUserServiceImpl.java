package vn.edu.hutech.lms_api.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.hutech.lms_api.domain.User;
import vn.edu.hutech.lms_api.dto.user.UserRequestDTO;
import vn.edu.hutech.lms_api.dto.user.UserResponseDTO;
import vn.edu.hutech.lms_api.repository.UserRepository;
import vn.edu.hutech.lms_api.service.AdminUserService;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::mapToResponseDTO);
    }

    @Override
    public Page<UserResponseDTO> getUsersByRole(String role, Pageable pageable) {
        if (role == null || role.isBlank()) {
            return getAllUsers(pageable);
        }
        return userRepository.findByRoleIgnoreCase(role, pageable).map(this::mapToResponseDTO);
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay user voi ID: " + id));
        return mapToResponseDTO(user);
    }

    @Override
    @Transactional
    public UserResponseDTO createUser(UserRequestDTO requestDTO) {
        if (userRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email nay da duoc su dung!");
        }
        if (requestDTO.getPassword() == null || requestDTO.getPassword().isBlank()) {
            throw new RuntimeException("Mat khau khong duoc de trong khi tao user!");
        }

        User user = User.builder()
                .fullName(requestDTO.getFullName())
                .email(requestDTO.getEmail())
                .passwordHash(passwordEncoder.encode(requestDTO.getPassword()))
                .role(requestDTO.getRole().toUpperCase())
                .isActive(requestDTO.getIsActive() != null ? requestDTO.getIsActive() : true)
                .build();

        return mapToResponseDTO(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponseDTO updateUser(Long id, UserRequestDTO requestDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay user voi ID: " + id));

        userRepository.findByEmail(requestDTO.getEmail())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new RuntimeException("Email nay da duoc su dung!");
                });

        user.setFullName(requestDTO.getFullName());
        user.setEmail(requestDTO.getEmail());
        user.setRole(requestDTO.getRole().toUpperCase());
        user.setIsActive(requestDTO.getIsActive() != null ? requestDTO.getIsActive() : user.getIsActive());

        if (requestDTO.getPassword() != null && !requestDTO.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(requestDTO.getPassword()));
            user.setRefreshToken(null);
            user.setRefreshTokenExpiresAt(null);
        }

        return mapToResponseDTO(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Khong tim thay user voi ID: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserResponseDTO mapToResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
