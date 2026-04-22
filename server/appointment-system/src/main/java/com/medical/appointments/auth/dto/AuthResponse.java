package com.medical.appointments.auth.dto;

import com.medical.appointments.user.dto.UserResponse;

public record AuthResponse(
        UserResponse user,
        String accessToken,
        String refreshToken
) {}
