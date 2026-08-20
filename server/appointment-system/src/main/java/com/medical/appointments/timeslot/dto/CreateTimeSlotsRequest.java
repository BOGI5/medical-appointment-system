package com.medical.appointments.timeslot.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateTimeSlotsRequest(
        @NotNull
        LocalDate date,

        @NotNull
        LocalTime startAt,

        @NotNull
        LocalTime endAt
) {
    @AssertTrue(message = "End time must be after start time")
    public boolean isValidTimeRange() {
        if (startAt == null || endAt == null) {
            return true;
        }

        return endAt.isAfter(startAt);
    }
}
