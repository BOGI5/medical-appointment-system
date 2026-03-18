package com.medical.appointments.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginUser(
        @Email
        @NotBlank
        String email,

        @NotBlank
        @Size(min = 6)
        String password
) {}
