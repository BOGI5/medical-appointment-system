package com.medical.appointments.controller;

import com.medical.appointments.auth.AuthService;
import com.medical.appointments.auth.dto.*;
import com.medical.appointments.security.CurrentUserProvider;
import com.medical.appointments.user.dto.CreateUserRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.AUTH)
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping(ApiPaths.REGISTER)
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody CreateUserRequest request) {
        return service.register(request);
    }

    @PostMapping(ApiPaths.LOGIN)
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return service.login(request);
    }

    @PostMapping(ApiPaths.LOGOUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@Valid @RequestBody TokenRequest request) {
        service.logout(request);
    }

    @PostMapping(ApiPaths.REFRESH)
    public AuthResponse refreshTokens(@Valid @RequestBody TokenRequest request) {
        return service.refreshTokens(request);
    }

    @PostMapping(ApiPaths.SWITCH_ROLE)
    public AuthResponse switchRole(@Valid @RequestBody SwitchRoleRequest request) {
        return service.switchCurrentUserRole(request, currentUserProvider.getCurrent());
    }
}
