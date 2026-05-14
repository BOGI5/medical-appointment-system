package com.medical.appointments.profiles.doctor.dto;

import com.medical.appointments.profiles.profile.dto.UpdateProfile;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateDoctorProfile(
        String specialization,
        String phone,
        String clinicAddress,
        String bio,

        @PositiveOrZero
        Integer yearsOfExperience
) implements UpdateProfile {}
