package com.medical.appointments.timeslot.mapper;

import com.medical.appointments.profiles.doctor.mapper.DoctorProfileMapper;
import com.medical.appointments.timeslot.TimeSlot;
import com.medical.appointments.timeslot.dto.TimeSlotResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TimeSlotMapper {
    private final DoctorProfileMapper doctorProfileMapper;

    public TimeSlotResponse toResponse(TimeSlot timeSlot) {
        return new TimeSlotResponse(
                timeSlot.getId(),
                timeSlot.getStartAt(),
                timeSlot.getEndAt(),
                doctorProfileMapper.toResponse(timeSlot.getDoctor())
        );
    }
}
