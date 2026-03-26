package com.medical.appointments.security.token;

import com.medical.appointments.security.token.exception.InvalidRefreshTokenException;
import com.medical.appointments.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    @Value("${refresh.token.expiration}")
    private int refreshTokenExpiration;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken create(User user) {
        String token = UUID.randomUUID().toString().replace("-", "");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(refreshTokenExpiration));

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken validateToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(InvalidRefreshTokenException::new);

        if (
                !refreshToken.isActive()
                || refreshToken.getExpiresAt().isBefore(LocalDateTime.now())
                || refreshToken.getUser() == null
        ) {
            throw new InvalidRefreshTokenException();
        }

        return refreshToken;
    }

    public RefreshToken rotateToken(RefreshToken refreshToken) {
        refreshToken.setActive(false);
        refreshTokenRepository.save(refreshToken);
        return create(refreshToken.getUser());
    }

    public void revokeToken(String token) {
        RefreshToken refreshToken = validateToken(token);
        refreshToken.setActive(false);
        refreshTokenRepository.save(refreshToken);
    }

    public void delete(RefreshToken refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }
}
