package com.medical.appointments.profiles.doctor.dto;

import com.medical.appointments.profiles.profile.dto.UpdateProfile;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateDoctorRequest(
        Long specializationId,
        String phone,
        String clinicAddress,
        String bio,

        @PositiveOrZero
        Integer yearsOfExperience
) implements UpdateProfile {}
