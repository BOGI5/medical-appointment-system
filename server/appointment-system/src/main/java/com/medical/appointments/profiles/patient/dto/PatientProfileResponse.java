package com.medical.appointments.profiles.patient.dto;

import com.medical.appointments.profiles.profile.dto.ProfileResponse;
import com.medical.appointments.user.dto.UserResponse;

public record PatientProfileResponse (
        UserResponse user,
        Long id,
        String address,
        String phone,
        String medicalHistory,
        String allergies
) implements ProfileResponse {}
