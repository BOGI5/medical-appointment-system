package com.medical.appointments.auth.controller;

import com.medical.appointments.auth.service.AuthService;
import com.medical.appointments.user.dto.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.medical.appointments.user.dto.UserResponse;
import com.medical.appointments.user.dto.CreateUser;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody CreateUser user) {
        return authService.registerUser(user);
    }

    @PostMapping("/login")
    public UserResponse login(@Valid @RequestBody LoginUser user) {
        return authService.login(user);
    }
}
