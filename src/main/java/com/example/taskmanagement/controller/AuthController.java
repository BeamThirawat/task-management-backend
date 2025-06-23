package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.request.AuthRequestDto;
import com.example.taskmanagement.dto.response.LoginResponseDto;
import com.example.taskmanagement.dto.response.StandardResponseDto;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth")
@Tag(name = "Authentication", description = "API For Login / Register / Logout")
public class AuthController {

    @Autowired
    private AuthService service;

    @Operation(summary = "Register")
    @PostMapping(value = "register")
    public StandardResponseDto<User> register(@RequestBody AuthRequestDto.RegisterRequestDto requestDto) {
        try {
            return StandardResponseDto.createSuccessResponse(service.register(requestDto));
        } catch (Exception e) {
            return StandardResponseDto.createFailResponse(e.getMessage(), null);
        }
    }

    @Operation(summary = "Login")
    @PostMapping(value = "login")
    public StandardResponseDto<LoginResponseDto> login(@RequestBody AuthRequestDto.LoginRequestDto requestDto, HttpServletResponse response) {
        try {

            LoginResponseDto result = service.login(requestDto);

            // create cookie for JWT
            Cookie cookie = new Cookie("token", result.getToken());
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24);
            response.addCookie(cookie);

            return StandardResponseDto.createSuccessResponse(result);
        } catch (Exception e) {
            return StandardResponseDto.createFailResponse(e.getMessage(), null);
        }
    }

    @Operation(summary = "Logout")
    @PostMapping(value = "logout")
    public StandardResponseDto<?> logout(HttpServletResponse response) {
        // create cookie and set 0
        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);

        return StandardResponseDto.createSuccessResponse("Logged out successfully.");
    }
}
