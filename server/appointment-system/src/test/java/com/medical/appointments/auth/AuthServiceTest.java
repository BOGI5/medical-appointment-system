package com.medical.appointments.auth;

import com.medical.appointments.auth.dto.*;
import com.medical.appointments.auth.exception.InvalidCredentialsException;
import com.medical.appointments.auth.mapper.AuthMapper;
import com.medical.appointments.security.jwt.JwtService;
import com.medical.appointments.security.token.RefreshToken;
import com.medical.appointments.security.token.RefreshTokenService;
import com.medical.appointments.security.token.exception.InvalidRefreshTokenException;
import com.medical.appointments.user.User;
import com.medical.appointments.user.UserService;
import com.medical.appointments.user.dto.UserResponse;
import com.medical.appointments.user.exception.UserAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserService userService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private AuthMapper authMapper;

    @InjectMocks private AuthService authService;

    // ===== LOGIN =====

    @Test
    void login_success() {
        // given
        LoginRequest request = new LoginRequest("mail", "pass");
        User user = createUser();

        when(userService.findOptionalByEmail("mail"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("pass", "encoded"))
                .thenReturn(true);

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

        verify(userService).findOptionalByEmail("mail");
        verify(passwordEncoder).matches("pass", "encoded");
        verify(jwtService).generateToken(user);
        verify(refreshTokenService).create(user);
        verify(authMapper).toAuthResponse(user, "access", "refresh");
    }

    @Test
    void login_userNotFound() {
        // given
        LoginRequest request = new LoginRequest("mail", "pass");

        when(userService.findOptionalByEmail("mail"))
                .thenReturn(Optional.empty());

        // when + then
        assertThrows(InvalidCredentialsException.class,
                () -> authService.login(request));

        verify(userService).findOptionalByEmail("mail");
        verifyNoInteractions(passwordEncoder, jwtService, refreshTokenService, authMapper);
    }

    @Test
    void login_wrongPassword() {
        // given
        LoginRequest request = new LoginRequest("mail", "pass");
        User user = createUser();

        when(userService.findOptionalByEmail("mail"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("pass", "encoded"))
                .thenReturn(false);

        // when + then
        assertThrows(InvalidCredentialsException.class,
                () -> authService.login(request));

        // verify
        verify(userService).findOptionalByEmail("mail");
        verify(passwordEncoder).matches("pass", "encoded");

        verifyNoInteractions(jwtService, refreshTokenService, authMapper);
    }

    // ===== REGISTER =====

    @Test
    void register_success() {
        // given
        RegisterRequest request = new RegisterRequest("mail", "pass", "John", "Doe");
        User user = createUser();

        when(passwordEncoder.encode("pass"))
                .thenReturn("encoded");

        when(userService.createUser(any()))
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
        verify(passwordEncoder).encode("pass");
        verify(userService).createUser(any());
        verify(jwtService).generateToken(user);
        verify(refreshTokenService).create(user);
        verify(authMapper).toAuthResponse(user, "access", "refresh");
    }

    @Test
    void register_userAlreadyExists() {
        // given
        RegisterRequest request = new RegisterRequest("mail", "pass", "John", "Doe");

        when(passwordEncoder.encode("pass"))
                .thenReturn("encoded");

        when(userService.createUser(any()))
                .thenThrow(new UserAlreadyExistsException());

        // when + then
        assertThrows(UserAlreadyExistsException.class,
                () -> authService.registerUser(request));

        // verify
        verify(passwordEncoder).encode("pass");

        verify(authMapper).toCreateUser(eq(request), eq("encoded"));
        verify(userService).createUser(any());

        verify(authMapper, never()).toAuthResponse(any(), any(), any());
        verifyNoInteractions(jwtService, refreshTokenService);
    }

    // ===== REFRESH =====

    @Test
    void refresh_success() {
        // given
        String token = "old";
        User user = createUser();

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
                                    u.getLastName()
                            ),
                            access,
                            refresh
                    );
                });
    }

    private User createUser() {
        return new User("mail", "encoded", "John", "Doe");
    }
}