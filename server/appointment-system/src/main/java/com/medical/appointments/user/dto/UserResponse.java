package com.medical.appointments.user.dto;

import com.medical.appointments.user.Role;

import java.util.Set;

public record UserResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        Set<Role> roles,
        Role activeRole
) {}
