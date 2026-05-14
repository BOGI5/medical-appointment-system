package com.medical.appointments.profiles.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAdminRequest(
        @Email
        @NotBlank
        String email,

        @NotBlank
        @Size(min = 6)
        String password,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName
) {
}
