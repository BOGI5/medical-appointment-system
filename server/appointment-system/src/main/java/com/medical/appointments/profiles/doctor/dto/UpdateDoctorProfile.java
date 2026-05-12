package com.medical.appointments.profiles.doctor.dto;

import com.medical.appointments.profiles.profile.dto.UpdateProfile;

public record UpdateDoctorProfile(
        String specialization,
        String phone,
        String clinicAddress,
        String bio,
        Integer yearsOfExperience
) implements UpdateProfile {}
