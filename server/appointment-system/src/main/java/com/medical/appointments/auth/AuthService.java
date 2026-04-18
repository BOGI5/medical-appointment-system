package com.medical.appointments.auth;

import com.medical.appointments.auth.dto.*;
import com.medical.appointments.auth.mapper.AuthMapper;
import com.medical.appointments.exception.InvalidCredentialsException;
import com.medical.appointments.security.jwt.JwtService;
import com.medical.appointments.security.token.RefreshToken;
import com.medical.appointments.security.token.RefreshTokenService;
import com.medical.appointments.exception.InvalidRefreshTokenException;
import com.medical.appointments.user.User;
import com.medical.appointments.user.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthMapper authMapper;

    private String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public AuthResponse login(LoginRequest loginRequest) {
        User user = userService.findOptionalByEmail(loginRequest.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return generateAuthResponse(user);
    }

    public AuthResponse registerUser(RegisterRequest registerRequest) {
        User user = userService.createUser(authMapper.toCreateUser(
                registerRequest,
                hashPassword(registerRequest.password())
        ));

        return generateAuthResponse(user);
    }

    public AuthResponse refreshTokens(TokenRequest tokenRequest) {
        RefreshToken oldRefreshToken = refreshTokenService.validateToken(tokenRequest.refreshToken());

        RefreshToken newRefreshToken = refreshTokenService.rotateToken(oldRefreshToken);

        String newAccessToken = jwtService.generateToken(newRefreshToken.getUser());

        return authMapper.toAuthResponse(
                newRefreshToken.getUser(),
                newAccessToken,
                newRefreshToken.getToken()
        );
    }

    public void logoutUser(TokenRequest tokenRequest) {
        try {
            refreshTokenService.revokeToken(tokenRequest.refreshToken());
        } catch (InvalidRefreshTokenException ignored) {}
    }

    private AuthResponse generateAuthResponse(User user) {
        String accessToken = jwtService.generateToken(user);
        String refreshToken = refreshTokenService.create(user).getToken();
        return authMapper.toAuthResponse(user, accessToken, refreshToken);
    }
}
