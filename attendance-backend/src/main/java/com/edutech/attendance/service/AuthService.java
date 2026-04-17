package com.edutech.attendance.service;

import com.edutech.attendance.dto.LoginRequest;
import com.edutech.attendance.dto.LoginResponse;
import com.edutech.attendance.dto.RegisterRequest;
import com.edutech.attendance.entity.User;
import com.edutech.attendance.exception.InvalidCredentialsException;
import com.edutech.attendance.exception.ResourceNotFoundException;
import com.edutech.attendance.jwt.JwtTokenProvider;
import com.edutech.attendance.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole().toString());
        log.info("User {} logged in successfully", user.getEmail());

        return new LoginResponse(token, "Bearer", user.getId(), user.getEmail(), user.getUsername(), user.getFullName(), user.getRole().toString());
    }

    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceNotFoundException("Email already registered");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResourceNotFoundException("Username already taken");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole(User.Role.valueOf(request.getRole()));
        user.setIsActive(true);

        User savedUser = userRepository.save(user);
        log.info("New user registered: {}", request.getEmail());
        return savedUser;
    }
}
