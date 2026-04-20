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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setFullName(userDTO.getFullName());
        user.setRole(User.Role.valueOf(userDTO.getRole()));
        user.setIsActive(userDTO.getIsActive() != null ? userDTO.getIsActive() : true);
        // Fix #4: encode password so the user can actually log in
        user.setPassword(passwordEncoder.encode(
                userDTO.getPassword() != null ? userDTO.getPassword() : "changeme"));

        if (userDTO.getManagerId() != null) {
            userRepository.findById(userDTO.getManagerId()).ifPresent(user::setManager);
        }

        User saved = userRepository.save(user);
        log.info("User created: {}", userDTO.getEmail());
        return saved;
    }

    public User updateUser(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        user.setFullName(userDTO.getFullName());
        user.setIsActive(userDTO.getIsActive());

        if (userDTO.getManagerId() != null) {
            userRepository.findById(userDTO.getManagerId()).ifPresent(user::setManager);
        } else {
            user.setManager(null);
        }

        User updated = userRepository.save(user);
        log.info("User updated: {}", userId);
        return updated;
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
        log.info("User deleted: {}", userId);
    }

    // Fix #6: DB-level query instead of in-memory stream filter
    public List<UserDTO> getUsersByRole(String role) {
        User.Role userRole = User.Role.valueOf(role.toUpperCase());
        return userRepository.findByRole(userRole).stream()
                .map(u -> new UserDTO(u.getId(), u.getUsername(), u.getEmail(),
                        u.getFullName(), u.getRole().toString(), u.getIsActive(), null,
                        u.getManager() != null ? u.getManager().getId() : null))
                .collect(Collectors.toList());
    }

    // Fix #5: get all users
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(u -> new UserDTO(u.getId(), u.getUsername(), u.getEmail(),
                        u.getFullName(), u.getRole().toString(), u.getIsActive(), null,
                        u.getManager() != null ? u.getManager().getId() : null))
                .collect(Collectors.toList());
    }

    // Fix #5: get user by ID
    public UserDTO getUserById(Long userId) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        return new UserDTO(u.getId(), u.getUsername(), u.getEmail(),
                u.getFullName(), u.getRole().toString(), u.getIsActive(), null,
                u.getManager() != null ? u.getManager().getId() : null);
    }
}
