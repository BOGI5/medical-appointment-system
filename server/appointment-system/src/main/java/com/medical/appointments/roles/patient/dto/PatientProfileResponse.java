package com.medical.appointments.roles.patient.dto;

import com.medical.appointments.user.dto.UserResponse;

public record PatientProfileResponse(
        UserResponse user,
        Long id,
        String address,
        String phone,
        String medicalHistory,
        String allergies
) {}
