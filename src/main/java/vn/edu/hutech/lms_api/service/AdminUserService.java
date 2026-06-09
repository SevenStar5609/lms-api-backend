package vn.edu.hutech.lms_api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.edu.hutech.lms_api.dto.user.UserRequestDTO;
import vn.edu.hutech.lms_api.dto.user.UserResponseDTO;

public interface AdminUserService {
    Page<UserResponseDTO> getAllUsers(Pageable pageable);
    Page<UserResponseDTO> getUsersByRole(String role, Pageable pageable);
    UserResponseDTO getUserById(Long id);
    UserResponseDTO createUser(UserRequestDTO requestDTO);
    UserResponseDTO updateUser(Long id, UserRequestDTO requestDTO);
    void deleteUser(Long id);
}
