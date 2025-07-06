package com.example.taskmanagement.service;

import com.example.taskmanagement.dto.request.AuthRequestDto;
import com.example.taskmanagement.dto.response.LoginResponseDto;
import com.example.taskmanagement.dto.response.UserinfoResponseDto;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.repository.UserRepository;
import com.example.taskmanagement.util.JwtUtil;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    // register service
    public User register(AuthRequestDto.RegisterRequestDto requestDto) {
        logger.info("Attempting to register user with username: {} and email: {}", requestDto.getUsername(), requestDto.getEmail());

        // Check Duplicate username
        if (repository.existsByUsername(requestDto.getUsername())) {
            logger.warn("Registration failed - Username '{}' is already taken", requestDto.getUsername());
            throw new RuntimeException("Username is already taken.");
        }

        // Check Duplicate Email
        if (repository.existsByEmail(requestDto.getEmail())) {
            logger.warn("Registration failed - Email '{}' is already registered", requestDto.getEmail());
            throw new RuntimeException("Email is already registered.");
        }

        // encode password
        String password = passwordEncoder.encode(requestDto.getPassword());
        logger.debug("Password encoded for user: {}", requestDto.getUsername());

        // Add User
        User user = new User();
        user.setUsername(requestDto.getUsername())
                .setEmail(requestDto.getEmail())
                .setPassword(password)
                .setCreatedAt(LocalDateTime.now());

        User savedUser = repository.save(user);
        logger.info("User registered successfully with ID: {}", savedUser.getId());

        return savedUser;
    }

    // login service
    public LoginResponseDto login(AuthRequestDto.LoginRequestDto requestDto) {
        logger.info("Login attempt for email: {}", requestDto.getEmail());

        // check email
        Optional<User> userOptional = repository.findByEmail(requestDto.getEmail());
        if (userOptional.isEmpty()) {
            logger.warn("Login failed - Email '{}' not found", requestDto.getEmail());
            throw new RuntimeException("Email or Password is not Correct");
        }

        // get User data
        User user = userOptional.get();
        // Check password
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            logger.warn("Login failed - Invalid password for email: {}", requestDto.getEmail());
            throw new RuntimeException("Email or Password is not Correct");
        }

        // create Token
        String token = jwtUtil.generateToken(user.getEmail());
        logger.info("Login successful for email: {}", user.getEmail());

        return new LoginResponseDto(user.getId(), user.getUsername(), user.getEmail(), token);
    }

    // Check Current User
    public UserinfoResponseDto getCurrentUser(String email) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserinfoResponseDto(user.getId(), user.getUsername(), user.getEmail());
    }
}
