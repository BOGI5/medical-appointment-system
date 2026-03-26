package com.medical.appointments.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.medical.appointments.auth.dto.AuthResponse;
import com.medical.appointments.auth.dto.LoginRequest;
import com.medical.appointments.auth.dto.TokenRequest;
import com.medical.appointments.auth.dto.RegisterRequest;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest user) {
        return authService.registerUser(user);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest user) {
        return authService.login(user);
    }

    @PostMapping("/logout")
    public void logout(@Valid @RequestBody TokenRequest tokenRequest) {
        authService.logoutUser(tokenRequest);
    }

    @PostMapping("/refresh")
    public AuthResponse refreshToken(@Valid @RequestBody TokenRequest tokenRequest) {
        return authService.refreshTokens(tokenRequest);
    }
}
