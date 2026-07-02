package com.medical.appointments.user.dto;

import com.medical.appointments.user.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CreateUser(
        @Email
        @NotBlank
        String email,

        @NotBlank
        @Size(min = 6)
        String password,

        Set<Role> roles,

        @NotNull
        Role activeRole,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName
) {}
