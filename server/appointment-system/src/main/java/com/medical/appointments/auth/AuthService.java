package com.medical.appointments.auth;

import com.medical.appointments.auth.dto.AuthResponse;
import com.medical.appointments.auth.exception.InvalidCredentialsException;
import com.medical.appointments.auth.mapper.AuthMapper;
import com.medical.appointments.security.cookie.CookieService;
import com.medical.appointments.security.jwt.JwtService;
import com.medical.appointments.auth.dto.RegisterRequest;
import com.medical.appointments.auth.dto.LoginRequest;
import com.medical.appointments.security.token.RefreshToken;
import com.medical.appointments.security.token.RefreshTokenService;
import com.medical.appointments.security.token.exception.InvalidRefreshTokenException;
import com.medical.appointments.user.User;
import com.medical.appointments.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final RefreshTokenService refreshTokenService;
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

        generateTokens(response, user);

        return authMapper.toAuthResponse(user);
    }

    public AuthResponse registerUser(RegisterRequest registerRequest, HttpServletResponse response) {
        User user = userService.createUser(authMapper.toCreateUser(
                registerRequest,
                hashPassword(registerRequest.password())
        ));

        generateTokens(response, user);

        return authMapper.toAuthResponse(user);
    }

    public AuthResponse refreshTokens(HttpServletRequest request, HttpServletResponse response) {
        String extractedToken = getRefreshTokenFromCookie(request);

        RefreshToken oldRefreshToken = refreshTokenService.validateToken(extractedToken);

        if (!userService.existsByEmail(oldRefreshToken.getUser().getEmail())) {
            throw new InvalidRefreshTokenException();
        }

        RefreshToken newRefreshToken = refreshTokenService.rotateToken(oldRefreshToken);

        String newAccessToken = jwtService.generateToken(newRefreshToken.getUser());

        setTokensToCookies(response, newAccessToken, newRefreshToken.getToken());

        return authMapper.toAuthResponse(newRefreshToken.getUser());
    }

    public void logoutUser(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshTokenFromCookie(request);

        try {
            refreshTokenService.revokeToken(refreshToken);
        } catch (InvalidRefreshTokenException ignored) {}

        cookieService.clearAuthCookies(response);
    }

    private void generateTokens(HttpServletResponse response, User user) {
        String accessToken = jwtService.generateToken(user);
        String refreshToken = refreshTokenService.create(user).getToken();
        setTokensToCookies(response, accessToken, refreshToken);
    }

    private void setTokensToCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        cookieService.setAccessTokenToCookie(response, accessToken);
        cookieService.setRefreshTokenToCookie(response, refreshToken);
    }

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        String refreshToken = cookieService.extractRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            throw new InvalidRefreshTokenException();
        }
        return refreshToken;
    }
}
