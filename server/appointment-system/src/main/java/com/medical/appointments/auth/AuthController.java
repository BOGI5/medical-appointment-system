package com.medical.appointments.auth;

import com.medical.appointments.auth.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest user) {
        return authService.registerUser(user);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest user) {
        return authService.login(user);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@Valid @RequestBody TokenRequest tokenRequest) {
        authService.logoutUser(tokenRequest);
    }

    @PostMapping("/refresh")
    public AuthResponse refreshToken(@Valid @RequestBody TokenRequest tokenRequest) {
        return authService.refreshTokens(tokenRequest);
    }
}
