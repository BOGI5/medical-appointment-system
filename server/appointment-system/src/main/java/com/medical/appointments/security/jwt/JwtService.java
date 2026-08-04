package com.medical.appointments.security.jwt;

import com.medical.appointments.config.properties.AccessTokenProperties;
import com.medical.appointments.config.properties.JwtProperties;
import com.medical.appointments.user.Role;
import com.medical.appointments.user.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;
    private final AccessTokenProperties accessTokenProperties;

    private static final String ROLES_CLAIM = "roles";
    private static final String ACTIVE_ROLE_CLAIM = "activeRole";

    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .claim(ROLES_CLAIM, user.getRoles().stream()
                        .map(Role::name)
                        .toList())
                .claim(ACTIVE_ROLE_CLAIM, user.getActiveRole().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenProperties.expiration()))
                .signWith(getSignKey())
                .compact();
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Role extractActiveRole(String token) {
        try {
            String role = extractAllClaims(token).get(ACTIVE_ROLE_CLAIM, String.class);
            return Role.valueOf(role);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public Set<Role> extractRoles(String token) {
        List<?> roles = extractAllClaims(token).get(ROLES_CLAIM, List.class);

        return roles.stream()
                .map(Object::toString)
                .map(Role::valueOf)
                .collect(Collectors.toSet());
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return !isTokenExpired(claims);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().getTime() < System.currentTimeMillis();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.secret()));
    }
}
