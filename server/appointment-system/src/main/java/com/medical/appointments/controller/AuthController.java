package com.medical.appointments.controller;

import com.medical.appointments.auth.AuthService;
import com.medical.appointments.auth.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.AUTH)
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping(ApiPaths.REGISTER)
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest user) {
        return authService.registerUser(user);
    }

    @PostMapping(ApiPaths.LOGIN)
    public AuthResponse login(@Valid @RequestBody LoginRequest user) {
        return authService.login(user);
    }

    @PostMapping(ApiPaths.LOGOUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@Valid @RequestBody TokenRequest tokenRequest) {
        authService.logoutUser(tokenRequest);
    }

    @PostMapping(ApiPaths.REFRESH)
    public AuthResponse refreshToken(@Valid @RequestBody TokenRequest tokenRequest) {
        return authService.refreshTokens(tokenRequest);
    }
}
