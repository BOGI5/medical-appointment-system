package com.medical.appointments.auth;

import com.medical.appointments.auth.dto.*;
import com.medical.appointments.auth.mapper.AuthMapper;
import com.medical.appointments.profiles.patient.PatientProfileService;
import com.medical.appointments.profiles.patient.dto.CreatePatientProfile;
import com.medical.appointments.security.jwt.JwtService;
import com.medical.appointments.security.token.RefreshToken;
import com.medical.appointments.security.token.RefreshTokenService;
import com.medical.appointments.exception.InvalidRefreshTokenException;
import com.medical.appointments.user.Role;
import com.medical.appointments.user.User;
import com.medical.appointments.user.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthMapper authMapper;
    private final PatientProfileService patientProfileService;

    public AuthResponse login(LoginRequest loginRequest) {
        return generateAuthResponse(userService.findAndCheckCredentials(loginRequest.email(), loginRequest.password()));
    }

    @Transactional
    public AuthResponse registerUser(RegisterRequest registerRequest) {
        User user = userService.create(authMapper.toCreateUser(
                registerRequest,
                Set.of(Role.PATIENT),
                Role.PATIENT
        ));

        patientProfileService.create(new CreatePatientProfile(user));

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

    @Transactional
    public AuthResponse switchCurrentUserRole(SwitchRoleRequest switchRoleRequest, User currentUser) {
        User user = userService.switchRole(switchRoleRequest.role(), currentUser.getId());
        refreshTokenService.revokeToken(switchRoleRequest.refreshToken());
        return generateAuthResponse(user);
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
