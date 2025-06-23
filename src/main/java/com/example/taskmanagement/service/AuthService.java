package com.example.taskmanagement.service;

import com.example.taskmanagement.dto.request.AuthRequestDto;
import com.example.taskmanagement.dto.response.LoginResponseDto;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.repository.UserRepository;
import com.example.taskmanagement.util.JwtUtil;
import jakarta.transaction.Transactional;
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

    // register service
    public User register(AuthRequestDto.RegisterRequestDto requestDto) {
        // Check Duplicate username
        if (repository.existsByUsername(requestDto.getUsername())) {
            throw new RuntimeException("Username is already taken.");
        }

        // Check Duplicate Email
        if (repository.existsByEmail(requestDto.getEmail())) {
            throw new RuntimeException("Email is already registered.");
        }

        // encode password
        String password = passwordEncoder.encode(requestDto.getPassword());

        // Add User
        User user = new User();
        user.setUsername(requestDto.getUsername())
                .setEmail(requestDto.getEmail())
                .setPassword(password)
                .setCreatedAt(LocalDateTime.now());

        return repository.save(user);
    }

    // login service
    public LoginResponseDto login(AuthRequestDto.LoginRequestDto requestDto) {

        // check email
        Optional<User> userOptional = repository.findByEmail(requestDto.getEmail());
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Email or Password is not Correct");
        }

        // get User data
        User user = userOptional.get();
        // Check password
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Email or Password is not Correct");
        }

        // create Token
        String token = jwtUtil.generateToken(user.getEmail());

        return new LoginResponseDto(user.getId(), user.getUsername(), user.getEmail(), token);
    }
}
