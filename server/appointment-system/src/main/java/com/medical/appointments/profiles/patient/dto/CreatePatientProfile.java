package com.medical.appointments.profiles.patient.dto;

import com.medical.appointments.profiles.profile.dto.CreateProfile;
import com.medical.appointments.user.User;
import jakarta.validation.constraints.NotNull;

public record CreatePatientProfile (
        @NotNull
        User user
) implements CreateProfile {}
