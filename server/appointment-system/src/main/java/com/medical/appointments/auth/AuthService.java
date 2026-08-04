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
import com.medical.appointments.user.dto.CreateUserRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PatientProfileService patientProfileService;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthMapper mapper;

    public AuthResponse login(LoginRequest request) {
        return generateAuthResponse(userService.findAndCheckCredentials(request.email(), request.password()));
    }

    @Transactional
    public AuthResponse register(CreateUserRequest request) {
        User user = userService.create(mapper.toCreateUser(
                request,
                Set.of(Role.PATIENT),
                Role.PATIENT
        ));

        patientProfileService.create(new CreatePatientProfile(user));

        return generateAuthResponse(user);
    }

    public AuthResponse refreshTokens(TokenRequest request) {
        RefreshToken oldRefreshToken = refreshTokenService.validateToken(request.refreshToken());

        RefreshToken newRefreshToken = refreshTokenService.rotateToken(oldRefreshToken);

        String newAccessToken = jwtService.generateToken(newRefreshToken.getUser());

        return mapper.toAuthResponse(
                newRefreshToken.getUser(),
                newAccessToken,
                newRefreshToken.getToken()
        );
    }

    @Transactional
    public AuthResponse switchCurrentUserRole(SwitchRoleRequest request, User currentUser) {
        User user = userService.switchRole(request.role(), currentUser.getId());
        refreshTokenService.revokeToken(request.refreshToken());
        return generateAuthResponse(user);
    }

    public void logout(TokenRequest request) {
        try {
            refreshTokenService.revokeToken(request.refreshToken());
        } catch (InvalidRefreshTokenException ignored) {}
    }

    private AuthResponse generateAuthResponse(User user) {
        String accessToken = jwtService.generateToken(user);
        String refreshToken = refreshTokenService.create(user).getToken();
        return mapper.toAuthResponse(user, accessToken, refreshToken);
    }
}
