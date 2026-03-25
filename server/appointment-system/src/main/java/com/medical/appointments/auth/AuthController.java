package com.medical.appointments.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.medical.appointments.auth.dto.AuthResponse;
import com.medical.appointments.auth.dto.LoginRequest;
import com.medical.appointments.auth.dto.RegisterRequest;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest user, HttpServletResponse response) {
        return authService.registerUser(user, response);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest user,  HttpServletResponse response) {
        return authService.login(user, response);
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logoutUser(request, response);
    }

    @PostMapping("/refresh")
    public AuthResponse refreshToken(HttpServletRequest request, HttpServletResponse response) {
        return authService.refreshTokens(request, response);
    }
}
