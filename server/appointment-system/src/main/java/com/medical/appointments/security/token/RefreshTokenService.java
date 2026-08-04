package com.medical.appointments.security.token;

import com.medical.appointments.config.properties.RefreshTokenProperties;
import com.medical.appointments.exception.InvalidRefreshTokenException;
import com.medical.appointments.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenProperties properties;

    private final RefreshTokenRepository repository;

    public RefreshToken create(User user) {
        String token = UUID.randomUUID().toString().replace("-", "");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(properties.expiration()));

        return repository.save(refreshToken);
    }

    public RefreshToken validateToken(String token) {
        RefreshToken refreshToken = repository.findByToken(token)
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

    @Transactional
    public RefreshToken rotateToken(RefreshToken refreshToken) {
        refreshToken.setActive(false);
        repository.save(refreshToken);
        return create(refreshToken.getUser());
    }

    @Transactional
    public void revokeToken(String token) {
        RefreshToken refreshToken = validateToken(token);
        refreshToken.setActive(false);
        repository.save(refreshToken);
    }
}
