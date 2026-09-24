package com.medical.appointments.timeslot.dto;

import com.medical.appointments.profiles.doctor.dto.DoctorProfileResponse;

import java.time.LocalDateTime;

public record TimeSlotResponse(
        Long id,
        LocalDateTime startAt,
        LocalDateTime endAt,
        DoctorProfileResponse doctor
) {}
