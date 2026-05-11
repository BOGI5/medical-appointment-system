package com.medical.appointments.roles.patient.dto;

import com.medical.appointments.user.User;
import jakarta.validation.constraints.NotNull;

public record CreatePatientProfile (
        @NotNull
        User user
) {}
