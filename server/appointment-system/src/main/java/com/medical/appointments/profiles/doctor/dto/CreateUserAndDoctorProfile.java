package com.medical.appointments.profiles.doctor.dto;

import com.medical.appointments.user.dto.CreateUser;
import jakarta.validation.Valid;

public record CreateUserAndDoctorProfile(
        @Valid CreateUser user
) {}
