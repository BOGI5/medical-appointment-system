package com.medical.appointments.timeslot;

import com.medical.appointments.config.properties.TimeSlotProperties;
import com.medical.appointments.exception.*;
import com.medical.appointments.profiles.doctor.DoctorProfile;
import com.medical.appointments.profiles.doctor.DoctorProfileService;
import com.medical.appointments.timeslot.dto.CreateTimeSlotsRequest;
import com.medical.appointments.timeslot.dto.TimeSlotResponse;
import com.medical.appointments.timeslot.mapper.TimeSlotMapper;
import com.medical.appointments.user.Role;
import com.medical.appointments.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeSlotService {
    private final TimeSlotMapper mapper;
    private final TimeSlotRepository repository;
    private final TimeSlotProperties properties;
    private final DoctorProfileService doctorProfileService;

    @Transactional
    public List<TimeSlotResponse> createSlots(CreateTimeSlotsRequest request, User currentUser) {
        LocalDateTime startAt = LocalDateTime.of(request.date(), request.startAt());
        LocalDateTime endAt = LocalDateTime.of(request.date(), request.endAt());

        validateTimeRangeCreation(startAt, endAt, currentUser);

        List<TimeSlot> timeSlots = new ArrayList<>();
        DoctorProfile doctor = doctorProfileService.findEntityById(currentUser.getId());

        for (LocalDateTime time = startAt; time.isBefore(endAt); time = time.plusMinutes(properties.duration())) {
            TimeSlot timeSlot = new TimeSlot(doctor, time, time.plusMinutes(properties.duration()));
            timeSlots.add(timeSlot);
        }

        repository.saveAll(timeSlots);

        return filterResponse(timeSlots);
    }

    private void validateTimeRangeCreation(LocalDateTime startAt, LocalDateTime endAt, User currentUser) {
        validateTimeRange(startAt, endAt, LocalDateTime.now());

        Duration range = Duration.between(startAt, endAt);
        long minutes = range.toMinutes();

        if (!range.equals(Duration.ofMinutes(minutes)) || minutes % properties.duration() != 0) {
            throw new TimeRangeNotDivisibleException();
        }

        if (repository.existsOverlappingTimeSlots(
                currentUser.getId(),
                startAt,
                endAt
        )) {
            throw new TimeSlotRangeAlreadyExistsException();
        }
    }

    private void validateTimeRange(LocalDateTime startAt, LocalDateTime endAt, LocalDateTime now) {
        if (endAt != null && !endAt.isAfter(startAt)) {
            throw new InvalidTimeOrderException();
        }

        if (startAt.isBefore(now)) {
            throw new TimeSlotRangeInPastException();
        }
    }

    private LocalDateTime resolveStartAt(LocalDateTime startAt, LocalDateTime endAt) {
        LocalDateTime now = LocalDateTime.now();
        startAt = startAt == null ? now : startAt;

        validateTimeRange(startAt, endAt, now);

        return startAt;
    }

    public Integer getSlotDuration() {
        return properties.duration();
    }

    public TimeSlotResponse findById(Long id) {
        TimeSlot timeSlot = repository.findById(id)
                .orElseThrow(TimeSlotNotFoundException::new);

        if (timeSlot.getStartAt().isBefore(LocalDateTime.now())) {
            throw new TimeSlotNotFoundException();
        }

        return mapper.toResponse(timeSlot);
    }

    public List<TimeSlotResponse> findAll(LocalDateTime startAt, LocalDateTime endAt) {
        startAt = resolveStartAt(startAt, endAt);

        List<TimeSlot> timeSlots =
                endAt == null
                ? repository.findByStartAt(startAt)
                : repository.findByTimeRange(startAt, endAt);

        return filterResponse(timeSlots);
    }

    public List<TimeSlotResponse> findByDoctorId(Long doctorId, LocalDateTime startAt, LocalDateTime endAt) {
        startAt = resolveStartAt(startAt, endAt);

        List<TimeSlot> timeSlots =
                endAt == null
                ? repository.findByDoctorIdAndStartAt(doctorId, startAt)
                : repository.findByDoctorIdAndTimeRange(doctorId, startAt, endAt);

        return filterResponse(timeSlots);
    }

    public List<TimeSlotResponse> findBySpecializationId(
            Long specializationId,
            LocalDateTime startAt,
            LocalDateTime endAt
    ) {
        startAt = resolveStartAt(startAt, endAt);

        List<TimeSlot> timeSlots =
                endAt == null
                ? repository.findBySpecializationIdAndStartAt(specializationId, startAt)
                : repository.findBySpecializationIdAndTimeRange(specializationId, startAt, endAt);

        return filterResponse(timeSlots);
    }

    @Transactional
    public void deleteById(Long id, User issuer) {
        TimeSlot timeSlot = repository.findById(id).orElseThrow(TimeSlotNotFoundException::new);

        if (!issuer.getActiveRole().equals(Role.ADMIN) && !timeSlot.getDoctor().getId().equals(issuer.getId())) {
            throw new NoAccessToTimeSlotException();
        }

        // TODO: check is time slot busy by appointment

        repository.deleteById(id);
    }

    @Transactional
    public void deleteByTimeRange(LocalDateTime startAt, LocalDateTime endAt, User issuer) {
        validateTimeRange(startAt, endAt, LocalDateTime.now());

        List<TimeSlot> timeSlots = repository.findByDoctorIdAndTimeRange(issuer.getId(), startAt, endAt);

        // TODO: check is time slot busy by appointment

        repository.deleteAll(timeSlots);
    }

    private List<TimeSlotResponse> filterResponse(List<TimeSlot> timeSlots) {
        return timeSlots.stream()
                .map(mapper::toResponse)
                .toList();
    }
}
