package com.edutech.attendance.service;

import com.edutech.attendance.dto.UserDTO;
import com.edutech.attendance.entity.User;
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
        user.setIsActive(userDTO.getIsActive());
        
        User saved = userRepository.save(user);
        log.info("User created: {}", userDTO.getEmail());
        return saved;
    }

    public User updateUser(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setFullName(userDTO.getFullName());
        user.setIsActive(userDTO.getIsActive());
        
        User updated = userRepository.save(user);
        log.info("User updated: {}", userId);
        return updated;
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
        log.info("User deleted: {}", userId);
    }

    public List<UserDTO> getUsersByRole(String role) {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole().toString().equals(role))
                .map(u -> new UserDTO(u.getId(), u.getUsername(), u.getEmail(), u.getFullName(), u.getRole().toString(), u.getIsActive()))
                .collect(Collectors.toList());
    }
}
