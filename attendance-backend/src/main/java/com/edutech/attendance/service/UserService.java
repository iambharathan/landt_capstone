package com.edutech.attendance.service;

import com.edutech.attendance.dto.UserDTO;
import com.edutech.attendance.entity.User;
import com.edutech.attendance.exception.ResourceNotFoundException;
import com.edutech.attendance.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    public User createUser(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setFullName(userDTO.getFullName());
        user.setRole(User.Role.valueOf(userDTO.getRole()));
        user.setIsActive(userDTO.getIsActive() != null ? userDTO.getIsActive() : true);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword() != null ? userDTO.getPassword() : "changeme"));
        if (userDTO.getManagerId() != null) {
            userRepository.findById(userDTO.getManagerId()).ifPresent(user::setManager);
        }
        return userRepository.save(user);
    }

    /** Safe update — never crashes on same-manager re-assignment */
    public User updateUser(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        if (userDTO.getFullName() != null && !userDTO.getFullName().isBlank()) {
            user.setFullName(userDTO.getFullName());
        }
        if (userDTO.getIsActive() != null) {
            user.setIsActive(userDTO.getIsActive());
        }
        if (userDTO.getManagerId() != null) {
            User mgr = userRepository.findById(userDTO.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found: " + userDTO.getManagerId()));
            user.setManager(mgr);
        } else {
            user.setManager(null);
        }
        User updated = userRepository.save(user);
        log.info("User updated: {}", userId);
        return updated;
    }

    /** Feature 1: Update own profile name */
    public UserDTO updateProfile(Long userId, String fullName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        if (fullName != null && !fullName.isBlank()) {
            user.setFullName(fullName);
        }
        return toDTO(userRepository.save(user));
    }

    /** Feature 4: Get all employees assigned to a manager */
    public List<UserDTO> getTeamByManager(Long managerId) {
        return userRepository.findByManagerId(managerId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    /** Feature 5: Toggle active status */
    public User toggleUserActive(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        user.setIsActive(!user.getIsActive());
        log.info("User {} active status set to {}", userId, user.getIsActive());
        return userRepository.save(user);
    }

    public void deleteUser(Long userId) { userRepository.deleteById(userId); }

    public List<UserDTO> getUsersByRole(String role) {
        return userRepository.findByRole(User.Role.valueOf(role.toUpperCase()))
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public UserDTO getUserById(Long userId) {
        return toDTO(userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId)));
    }

    private UserDTO toDTO(User u) {
        return new UserDTO(u.getId(), u.getUsername(), u.getEmail(),
                u.getFullName(), u.getRole().toString(), u.getIsActive(), null,
                u.getManager() != null ? u.getManager().getId() : null);
    }
}
