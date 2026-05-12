package com.medical.appointments.profiles.patient.dto;

import com.medical.appointments.profiles.profile.dto.UpdateProfile;

public record UpdatePatientProfile(
        String address,
        String phone,
        String allergies
) implements UpdateProfile {}
