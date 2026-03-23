package com.medical.appointments.security.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CookieService {
    @Value("${access.cookie.name}")
    private String accessTokenCookieName;

    @Value("${access.cookie.max-age}")
    private int accessTokenCookieMaxAge;

    @Value("${refresh.cookie.name}")
    private String refreshTokenCookieName;

    @Value("${refresh.cookie.max-age}")
    private int refreshTokenCookieMaxAge;

    @Value("${cookie.secure}")
    private boolean cookieSecure;

    public void setAccessTokenToCookie(HttpServletResponse response, String accessToken) {
        Cookie accessTokenCookie = buildCookie(accessTokenCookieName, accessToken, accessTokenCookieMaxAge);
        response.addCookie(accessTokenCookie);
    }

    public void setRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {
        Cookie refreshTokenCookie = buildCookie(refreshTokenCookieName, refreshToken, refreshTokenCookieMaxAge);
        response.addCookie(refreshTokenCookie);
    }

    public String extractAccessTokenFromCookie(HttpServletRequest request) {
        return extractCookie(request,  accessTokenCookieName);
    }

    public String extractRefreshTokenFromCookie(HttpServletRequest request) {
        return extractCookie(request,  refreshTokenCookieName);
    }

    public void clearAuthCookies(HttpServletResponse response) {
        Cookie accessTokenCookie =  buildCookie(accessTokenCookieName, "", 0);
        Cookie refreshTokenCookie =  buildCookie(refreshTokenCookieName, "", 0);
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }

    private Cookie buildCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        return cookie;
    }

    private String extractCookie(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(c -> cookieName.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
