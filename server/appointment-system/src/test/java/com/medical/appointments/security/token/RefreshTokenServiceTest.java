package com.medical.appointments.security.token;

import com.medical.appointments.exception.InvalidRefreshTokenException;
import com.medical.appointments.user.Role;
import com.medical.appointments.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository repository;

    @InjectMocks
    private RefreshTokenService service;

    private User user;

    @BeforeEach
    void setup() {
        user = User.builder()
                .email("mail")
                .password("pass")
                .firstName("John")
                .lastName("Doe")
                .roles(Set.of(Role.PATIENT))
                .activeRole(Role.PATIENT)
                .build();

        // set expiration 1 day
        ReflectionTestUtils.setField(service, "refreshTokenExpiration", 1);
    }

    // ===== CREATE =====

    @Test
    void create_success() {
        // given
        when(repository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        LocalDateTime before = LocalDateTime.now();

        // when
        RefreshToken token = service.create(user);

        LocalDateTime after = LocalDateTime.now();

        // then
        assertNotNull(token.getToken());
        assertFalse(token.getToken().isBlank());

        assertEquals(32, token.getToken().length());

        assertEquals(user, token.getUser());
        assertTrue(token.isActive());

        assertNotNull(token.getExpiresAt());

        assertTrue(token.getExpiresAt().isAfter(before));
        assertTrue(token.getExpiresAt().isAfter(after.minusSeconds(1)));

        // verify
        verify(repository).save(token);
    }

    // ===== VALIDATE =====

    @Test
    void validateToken_success() {
        // given
        String tokenValue = "token";
        RefreshToken token = validToken();

        when(repository.findByToken(tokenValue))
                .thenReturn(Optional.of(token));

        // when
        RefreshToken result = service.validateToken(tokenValue);

        // then
        assertNotNull(result);
        assertEquals(token, result);

        assertTrue(result.isActive());
        assertNotNull(result.getUser());
        assertTrue(result.getExpiresAt().isAfter(LocalDateTime.now()));

        // verify
        verify(repository).findByToken(tokenValue);
    }

    @Test
    void validateToken_notFound() {
        // given
        String tokenValue = "token";

        when(repository.findByToken(tokenValue))
                .thenReturn(Optional.empty());

        // when + then
        assertThrows(InvalidRefreshTokenException.class,
                () -> service.validateToken(tokenValue));

        // verify
        verify(repository).findByToken(tokenValue);

        verifyNoMoreInteractions(repository);
    }

    @Test
    void validateToken_inactive() {
        // given
        String tokenValue = "token";

        RefreshToken token = validToken();
        token.setActive(false);

        when(repository.findByToken(tokenValue))
                .thenReturn(Optional.of(token));

        // when + then
        assertThrows(InvalidRefreshTokenException.class,
                () -> service.validateToken(tokenValue));

        // verify
        verify(repository).findByToken(tokenValue);

        assertFalse(token.isActive());

        verifyNoMoreInteractions(repository);
    }

    @Test
    void validateToken_expired() {
        // given
        String tokenValue = "token";

        RefreshToken token = validToken();
        LocalDateTime expiredTime = LocalDateTime.now().minusDays(1);
        token.setExpiresAt(expiredTime);

        when(repository.findByToken(tokenValue))
                .thenReturn(Optional.of(token));

        // when + then
        assertThrows(InvalidRefreshTokenException.class,
                () -> service.validateToken(tokenValue));

        // verify
        verify(repository).findByToken(tokenValue);

        assertTrue(token.getExpiresAt().isBefore(LocalDateTime.now()));

        verifyNoMoreInteractions(repository);
    }

    @Test
    void validateToken_noUser() {
        // given
        String tokenValue = "token";

        RefreshToken token = validToken();
        token.setUser(null);

        when(repository.findByToken(tokenValue))
                .thenReturn(Optional.of(token));

        // when + then
        assertThrows(InvalidRefreshTokenException.class,
                () -> service.validateToken(tokenValue));

        // verify
        verify(repository).findByToken(tokenValue);

        assertNull(token.getUser());

        verifyNoMoreInteractions(repository);
    }

    // ===== ROTATE =====

    @Test
    void rotateToken_success() {
        // given
        RefreshToken oldToken = validToken();

        when(repository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        // when
        RefreshToken newToken = service.rotateToken(oldToken);

        // then

        assertFalse(oldToken.isActive());

        assertNotNull(newToken);
        assertTrue(newToken.isActive());
        assertEquals(oldToken.getUser(), newToken.getUser());

        assertNotNull(newToken.getToken());
        assertFalse(newToken.getToken().isBlank());
        assertEquals(32, newToken.getToken().length());

        // verify
        verify(repository, times(2)).save(any());

        verify(repository).save(oldToken);
        verify(repository).save(newToken);
    }

    // ===== REVOKE =====

    @Test
    void revokeToken_success() {
        // given
        String tokenValue = "token";

        RefreshToken token = validToken();

        when(repository.findByToken(tokenValue))
                .thenReturn(Optional.of(token));

        when(repository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        // when
        service.revokeToken(tokenValue);

        // then
        assertFalse(token.isActive());

        // verify
        verify(repository).findByToken(tokenValue);
        verify(repository).save(token);

        verifyNoMoreInteractions(repository);
    }

    @Test
    void revokeToken_invalid() {
        // given
        String tokenValue = "token";

        when(repository.findByToken(tokenValue))
                .thenReturn(Optional.empty());

        // when + then
        assertThrows(InvalidRefreshTokenException.class,
                () -> service.revokeToken(tokenValue));

        // verify
        verify(repository).findByToken(tokenValue);

        verify(repository, never()).save(any());

        verifyNoMoreInteractions(repository);
    }

    // ===== helper =====

    private RefreshToken validToken() {
        RefreshToken token = new RefreshToken();
        token.setToken("token");
        token.setUser(user);
        token.setActive(true);
        token.setExpiresAt(LocalDateTime.now().plusDays(1));
        return token;
    }
}