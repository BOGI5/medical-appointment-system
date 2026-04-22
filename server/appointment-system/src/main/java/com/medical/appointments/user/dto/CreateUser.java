package com.medical.appointments.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUser(
        @Email
        @NotBlank
        String email,

        @NotBlank
        @Size(min = 6)
        String hashedPassword,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName
) {
}
