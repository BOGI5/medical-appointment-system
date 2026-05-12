package com.medical.appointments.profiles.profile.dto;

import com.medical.appointments.user.dto.UserResponse;

public interface ProfileResponse {
    UserResponse user();
    Long id();
}
