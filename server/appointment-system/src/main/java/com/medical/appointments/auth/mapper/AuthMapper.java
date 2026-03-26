package com.medical.appointments.auth.mapper;

import com.medical.appointments.auth.dto.AuthResponse;
import com.medical.appointments.auth.dto.RegisterRequest;
import com.medical.appointments.user.User;
import com.medical.appointments.user.dto.CreateUser;
import com.medical.appointments.user.dto.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {
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
                new UserResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName()
                ),
                accessToken,
                refreshToken
        );
    }
}
