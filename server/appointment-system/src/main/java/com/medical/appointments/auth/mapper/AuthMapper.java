package com.medical.appointments.auth.mapper;

import com.medical.appointments.auth.dto.AuthResponse;
import com.medical.appointments.auth.dto.RegisterRequest;
import com.medical.appointments.user.User;
import com.medical.appointments.user.dto.CreateUser;
import com.medical.appointments.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthMapper {
    private final UserMapper userMapper;

    public CreateUser toCreateUser(RegisterRequest registerRequest, String hashedPassword) {
        return new CreateUser(
                registerRequest.email(),
                hashedPassword,
                registerRequest.firstName(),
                registerRequest.lastName()
        );
    }

    public AuthResponse toAuthResponse(User user, String accessToken, String refreshToken) {
        return new AuthResponse(
                userMapper.toResponse(user),
                accessToken,
                refreshToken
        );
    }
}
