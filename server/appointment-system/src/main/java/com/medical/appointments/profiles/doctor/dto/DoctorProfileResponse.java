package com.medical.appointments.profiles.doctor.dto;

import com.medical.appointments.profiles.profile.dto.ProfileResponse;
import com.medical.appointments.references.specialization.dto.SpecializationResponse;
import com.medical.appointments.user.dto.UserResponse;

public record DoctorProfileResponse(
        UserResponse user,
        Long id,
        SpecializationResponse specialization,
        String phone,
        String clinicAddress,
        String bio,
        Integer yearsOfExperience
) implements ProfileResponse {}
