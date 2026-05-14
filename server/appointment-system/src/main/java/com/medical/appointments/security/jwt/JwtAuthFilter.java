package com.medical.appointments.security.jwt;

import com.medical.appointments.user.Role;
import com.medical.appointments.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final UserService userService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        if (!jwtService.validateToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        String email = jwtService.extractEmail(token);
        Role activeRole = jwtService.extractActiveRole(token);

        if (email == null || activeRole == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        userService.findOptionalByEmail(email).ifPresent(user -> {
            if (!user.getRoles().contains(activeRole) || !user.getActiveRole().equals(activeRole)) return;

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            List.of(new SimpleGrantedAuthority(activeRole.asAuthority()))
                    );

            SecurityContextHolder.getContext().setAuthentication(auth);
        });

        filterChain.doFilter(request, response);
    }
}
