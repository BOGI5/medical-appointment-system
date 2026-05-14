package com.medical.appointments.auth.dto;

import com.medical.appointments.user.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SwitchRoleRequest(
        @NotNull
        Role role,

        @NotBlank
        String refreshToken
) {
}
