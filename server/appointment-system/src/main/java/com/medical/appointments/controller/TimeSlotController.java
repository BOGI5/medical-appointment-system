package com.medical.appointments.controller;

import com.medical.appointments.security.CurrentUserProvider;
import com.medical.appointments.security.annotation.role.IsAdminOrDoctor;
import com.medical.appointments.security.annotation.role.IsDoctor;
import com.medical.appointments.timeslot.TimeSlotService;
import com.medical.appointments.timeslot.dto.CreateTimeSlotsRequest;
import com.medical.appointments.timeslot.dto.TimeSlotResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(ApiPaths.TIME_SLOTS)
@RequiredArgsConstructor
public class TimeSlotController {
    private final TimeSlotService service;
    private final CurrentUserProvider currentUserProvider;

    @IsDoctor
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<TimeSlotResponse> create(@RequestBody @Valid CreateTimeSlotsRequest request) {
        return service.createSlots(request, currentUserProvider.getCurrent());
    }

    @GetMapping(ApiPaths.DURATION)
    public Integer getSlotDuration() {
        return service.getSlotDuration();
    }

    @GetMapping
    public List<TimeSlotResponse> findAll(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startAt,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endAt
    ) {
        return service.findAll(startAt, endAt);
    }

    @GetMapping(ApiPaths.BY_ID)
    public TimeSlotResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @IsDoctor
    @GetMapping(ApiPaths.CURRENT_DOCTOR)
    public List<TimeSlotResponse> findCurrentDoctor(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startAt,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endAt
    ) {
        return service.findByDoctorId(currentUserProvider.getCurrent().getId(), startAt, endAt);
    }

    @GetMapping(ApiPaths.BY_DOCTOR_ID)
    public List<TimeSlotResponse> findByDoctorId(
            @PathVariable Long id,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startAt,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endAt
    ) {
        return service.findByDoctorId(id, startAt, endAt);
    }

    @GetMapping(ApiPaths.BY_SPECIALIZATION_ID)
    public List<TimeSlotResponse> findBySpecializationId(
            @PathVariable Long id,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startAt,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endAt
    ) {
        return service.findBySpecializationId(id, startAt, endAt);
    }

    @IsAdminOrDoctor
    @DeleteMapping(ApiPaths.BY_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        service.deleteById(id, currentUserProvider.getCurrent());
    }

    @IsDoctor
    @DeleteMapping(params = {"startAt", "endAt"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startAt,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endAt
    ) {
        service.deleteByTimeRange(startAt, endAt, currentUserProvider.getCurrent());
    }
}
