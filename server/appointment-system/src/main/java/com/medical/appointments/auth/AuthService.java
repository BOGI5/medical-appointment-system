package com.medical.appointments.auth;

import com.medical.appointments.auth.dto.AuthResponse;
import com.medical.appointments.auth.exception.InvalidCredentialsException;
import com.medical.appointments.auth.mapper.AuthMapper;
import com.medical.appointments.security.cookie.CookieService;
import com.medical.appointments.security.jwt.JwtService;
import com.medical.appointments.auth.dto.RegisterRequest;
import com.medical.appointments.auth.dto.LoginRequest;
import com.medical.appointments.user.User;
import com.medical.appointments.user.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final CookieService cookieService;
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthMapper authMapper;

    private String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public AuthResponse login(LoginRequest loginRequest,  HttpServletResponse response) {
        User user = userService.findOptionalByEmail(loginRequest.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(user);
        cookieService.setAccessTokenToCookie(response, token);

        return authMapper.toAuthResponse(user);
    }

    public AuthResponse registerUser(RegisterRequest registerRequest, HttpServletResponse response) {
        User user = userService.createUser(authMapper.toCreateUser(
                registerRequest,
                hashPassword(registerRequest.password())
        ));

        String token = jwtService.generateToken(user);
        cookieService.setAccessTokenToCookie(response, token);

        return authMapper.toAuthResponse(user);
    }

    public void logoutUser(HttpServletResponse response) {
        cookieService.clearAuthCookies(response);
    }
}
