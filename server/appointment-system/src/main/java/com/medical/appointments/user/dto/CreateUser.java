package com.medical.appointments.user.dto;

import com.medical.appointments.user.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

public record CreateUser(
        @Email
        @NotBlank
        String email,

        @NotBlank
        @Size(min = 6)
        String hashedPassword,

        Set<Role> roles,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName
) {
        public void addRoles(Set<Role> roles) {
                if (roles == null) {
                        roles = new HashSet<>();
                }
                roles.addAll(this.roles);
        }
}
