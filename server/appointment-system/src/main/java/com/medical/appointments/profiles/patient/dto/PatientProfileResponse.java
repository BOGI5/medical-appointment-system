package com.medical.appointments.profiles.patient.dto;

import com.medical.appointments.profiles.profile.dto.ProfileResponse;
import com.medical.appointments.references.allergy.dto.AllergyResponse;
import com.medical.appointments.user.dto.UserResponse;

import java.util.Set;

public record PatientProfileResponse (
        UserResponse user,
        Long id,
        String address,
        String phone,
        String medicalHistory,
        Set<AllergyResponse> allergies
) implements ProfileResponse {}
