package com.medical.appointments.profiles.doctor.dto;

import com.medical.appointments.profiles.profile.dto.CreateProfile;
import com.medical.appointments.user.User;
import jakarta.validation.constraints.NotNull;

public record CreateDoctorProfile(
        @NotNull
        User user
) implements CreateProfile {}
