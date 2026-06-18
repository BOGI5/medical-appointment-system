package com.medical.appointments.auth;

import com.medical.appointments.auth.dto.*;
import com.medical.appointments.exception.InvalidCredentialsException;
import com.medical.appointments.auth.mapper.AuthMapper;
import com.medical.appointments.exception.RoleNotAssignedException;
import com.medical.appointments.profiles.patient.PatientProfileService;
import com.medical.appointments.profiles.patient.dto.CreatePatientProfile;
import com.medical.appointments.security.jwt.JwtService;
import com.medical.appointments.security.token.RefreshToken;
import com.medical.appointments.security.token.RefreshTokenService;
import com.medical.appointments.exception.InvalidRefreshTokenException;
import com.medical.appointments.user.Role;
import com.medical.appointments.user.User;
import com.medical.appointments.user.UserService;
import com.medical.appointments.user.dto.CreateUser;
import com.medical.appointments.user.dto.CreateUserRequest;
import com.medical.appointments.user.dto.UserResponse;
import com.medical.appointments.exception.UserAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserService userService;
    @Mock private JwtService jwtService;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private PatientProfileService patientProfileService;
    @Mock private AuthMapper authMapper;

    @InjectMocks private AuthService authService;

    // ===== LOGIN =====

    @Test
    void login_success() {
        // given
        LoginRequest request = new LoginRequest("mail", "pass");
        User user = createPatientUser();

        when(userService.findAndCheckCredentials("mail", "pass")).thenReturn(user);

        when(jwtService.generateToken(user))
                .thenReturn("access");

        RefreshToken refresh = new RefreshToken();
        refresh.setToken("refresh");

        when(refreshTokenService.create(user))
                .thenReturn(refresh);

        mockAuthResponseMapper();

        // when
        AuthResponse result = authService.login(request);

        // then
        assertNotNull(result);

        assertEquals("access", result.accessToken());
        assertEquals("refresh", result.refreshToken());
        assertEquals("mail", result.user().email());
        assertEquals("John", result.user().firstName());

        verify(userService).findAndCheckCredentials("mail", "pass");
        verify(jwtService).generateToken(user);
        verify(refreshTokenService).create(user);
        verify(authMapper).toAuthResponse(user, "access", "refresh");
    }

    @Test
    void login_invalidCredentials() {
        // given
        LoginRequest request = new LoginRequest("mail", "pass");

        when(userService.findAndCheckCredentials("mail", "pass"))
                .thenThrow(new InvalidCredentialsException());

        // when + then
        assertThrows(InvalidCredentialsException.class,
                () -> authService.login(request));

        verify(userService).findAndCheckCredentials("mail", "pass");
        verifyNoInteractions(jwtService, refreshTokenService, authMapper);
    }

    // ===== REGISTER =====

    @Test
    void register_success() {
        // given
        CreateUserRequest request = new CreateUserRequest("mail", "pass", "John", "Doe");
        User user = createPatientUser();
        CreateUser createUser = new CreateUser(
                request.email(),
                request.password(),
                Set.of(Role.PATIENT),
                Role.PATIENT,
                request.firstName(),
                request.lastName()
        );

        when(authMapper.toCreateUser(
                request,
                Set.of(Role.PATIENT),
                Role.PATIENT
        )).thenReturn(createUser);

        when(userService.create(createUser))
                .thenReturn(user);

        when(jwtService.generateToken(user))
                .thenReturn("access");

        RefreshToken refresh = new RefreshToken();
        refresh.setToken("refresh");

        when(refreshTokenService.create(user))
                .thenReturn(refresh);

        mockAuthResponseMapper();

        // when
        AuthResponse result = authService.registerUser(request);

        // then
        assertNotNull(result);

        assertEquals("access", result.accessToken());
        assertEquals("refresh", result.refreshToken());
        assertEquals("mail", result.user().email());
        assertEquals("John", result.user().firstName());

        // verify
        verify(userService).create(createUser);
        verify(jwtService).generateToken(user);
        verify(refreshTokenService).create(user);
        verify(patientProfileService).create(any(CreatePatientProfile.class));
        verify(authMapper).toAuthResponse(user, "access", "refresh");
    }

    @Test
    void register_userAlreadyExists() {
        // given
        CreateUserRequest request = new CreateUserRequest("mail", "pass", "John", "Doe");

        CreateUser createUser = new CreateUser(
                request.email(),
                request.password(),
                Set.of(Role.PATIENT),
                Role.PATIENT,
                request.firstName(),
                request.lastName()
        );

        when(authMapper.toCreateUser(
                request,
                Set.of(Role.PATIENT),
                Role.PATIENT
        )).thenReturn(createUser);

        when(userService.create(createUser))
                .thenThrow(new UserAlreadyExistsException());

        // when + then
        assertThrows(UserAlreadyExistsException.class,
                () -> authService.registerUser(request));

        // verify
        verify(authMapper).toCreateUser(
                request,
                Set.of(Role.PATIENT),
                Role.PATIENT
        );
        verify(userService).create(createUser);

        verify(authMapper, never()).toAuthResponse(any(), any(), any());
        verifyNoInteractions(jwtService, refreshTokenService);
    }

    // ===== REFRESH =====

    @Test
    void refresh_success() {
        // given
        String token = "old";
        User user = createPatientUser();

        RefreshToken oldToken = new RefreshToken();
        oldToken.setUser(user);

        RefreshToken newToken = new RefreshToken();
        newToken.setUser(user);
        newToken.setToken("new");

        when(refreshTokenService.validateToken(token))
                .thenReturn(oldToken);

        when(refreshTokenService.rotateToken(oldToken))
                .thenReturn(newToken);

        when(jwtService.generateToken(user))
                .thenReturn("access");

        mockAuthResponseMapper();

        // when
        AuthResponse result = authService.refreshTokens(new TokenRequest(token));

        // then
        assertNotNull(result);

        assertEquals("access", result.accessToken());
        assertEquals("new", result.refreshToken());
        assertEquals("mail", result.user().email());

        // verify
        verify(refreshTokenService).validateToken(token);
        verify(refreshTokenService).rotateToken(oldToken);
        verify(jwtService).generateToken(user);
        verify(authMapper).toAuthResponse(user, "access", "new");
    }

    @Test
    void refresh_invalidToken() {
        // given
        String token = "bad";

        when(refreshTokenService.validateToken(token))
                .thenThrow(new InvalidRefreshTokenException());

        // when + then
        assertThrows(InvalidRefreshTokenException.class,
                () -> authService.refreshTokens(new TokenRequest(token)));

        // verify
        verify(refreshTokenService).validateToken(token);

        verifyNoInteractions(jwtService, authMapper);
        verify(refreshTokenService, never()).rotateToken(any());
    }

    // ===== ROLES =====

    @Test
    void switchCurrentUserRole_success() {
        // given
        User currentUser = createPatientUser();

        SwitchRoleRequest request = new SwitchRoleRequest(
                Role.DOCTOR,
                "old-refresh"
        );

        User updatedUser = User.builder()
                .email("mail")
                .password("encoded")
                .firstName("John")
                .lastName("Doe")
                .roles(Set.of(Role.PATIENT, Role.DOCTOR))
                .activeRole(Role.DOCTOR)
                .build();

        when(userService.switchRole(Role.DOCTOR, currentUser.getId()))
                .thenReturn(updatedUser);

        when(jwtService.generateToken(updatedUser))
                .thenReturn("new-access");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("new-refresh");

        when(refreshTokenService.create(updatedUser))
                .thenReturn(refreshToken);

        mockAuthResponseMapper();

        // when
        AuthResponse result = authService.switchCurrentUserRole(request, currentUser);

        // then
        assertNotNull(result);

        assertEquals("new-access", result.accessToken());
        assertEquals("new-refresh", result.refreshToken());
        assertEquals(Role.DOCTOR, result.user().activeRole());

        verify(userService).switchRole(Role.DOCTOR, currentUser.getId());
        verify(refreshTokenService).revokeToken("old-refresh");
        verify(jwtService).generateToken(updatedUser);
        verify(refreshTokenService).create(updatedUser);
        verify(authMapper).toAuthResponse(updatedUser, "new-access", "new-refresh");
    }

    @Test
    void switchCurrentUserRole_invalidRole() {
        // given
        User currentUser = createPatientUser();

        SwitchRoleRequest request = new SwitchRoleRequest(
                Role.DOCTOR,
                "old-refresh"
        );

        when(userService.switchRole(Role.DOCTOR, currentUser.getId()))
                .thenThrow(new RoleNotAssignedException());

        // when + then
        assertThrows(
                RoleNotAssignedException.class,
                () -> authService.switchCurrentUserRole(request, currentUser)
        );

        verify(userService).switchRole(Role.DOCTOR, currentUser.getId());

        verify(refreshTokenService, never()).revokeToken(any());
        verifyNoInteractions(jwtService, authMapper);
    }

    // ===== LOGOUT =====

    @Test
    void logout_success() {
        // given
        TokenRequest request = new TokenRequest("refresh-token");

        // when
        authService.logoutUser(request);

        // then
        verify(refreshTokenService).revokeToken("refresh-token");
    }

    @Test
    void logout_invalidToken_doesNotThrow() {
        // given
        TokenRequest request = new TokenRequest("bad-token");

        doThrow(new InvalidRefreshTokenException())
                .when(refreshTokenService)
                .revokeToken("bad-token");

        // when + then
        assertDoesNotThrow(() -> authService.logoutUser(request));

        verify(refreshTokenService).revokeToken("bad-token");
    }

    private void mockAuthResponseMapper() {
        when(authMapper.toAuthResponse(any(), any(), any()))
                .thenAnswer(inv -> {
                    User u = inv.getArgument(0);
                    String access = inv.getArgument(1);
                    String refresh = inv.getArgument(2);

                    return new AuthResponse(
                            new UserResponse(
                                    u.getId(),
                                    u.getEmail(),
                                    u.getFirstName(),
                                    u.getLastName(),
                                    u.getRoles(),
                                    u.getActiveRole()
                            ),
                            access,
                            refresh
                    );
                });
    }

    private User createPatientUser() {
        return User.builder()
                .email("mail")
                .password("encoded")
                .firstName("John")
                .lastName("Doe")
                .roles(Set.of(Role.PATIENT))
                .activeRole(Role.PATIENT)
                .build();
    }
}
