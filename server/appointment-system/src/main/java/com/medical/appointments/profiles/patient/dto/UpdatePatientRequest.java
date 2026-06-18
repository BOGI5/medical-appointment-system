package com.medical.appointments.profiles.patient.dto;

import com.medical.appointments.profiles.profile.dto.UpdateProfile;

public record UpdatePatientRequest(
        String address,
        String phone
) implements UpdateProfile {}
