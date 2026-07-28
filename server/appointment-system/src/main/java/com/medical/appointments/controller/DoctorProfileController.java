package com.medical.appointments.controller;

import com.medical.appointments.profiles.doctor.DoctorProfileService;
import com.medical.appointments.user.dto.CreateUserRequest;
import com.medical.appointments.profiles.doctor.dto.DoctorProfileResponse;
import com.medical.appointments.profiles.doctor.dto.UpdateDoctorRequest;
import com.medical.appointments.security.CurrentUserProvider;
import com.medical.appointments.security.annotation.role.IsAdmin;
import com.medical.appointments.security.annotation.role.IsDoctor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.DOCTOR_PROFILES)
@RequiredArgsConstructor
public class DoctorProfileController {
    private final DoctorProfileService service;
    private final CurrentUserProvider currentUserProvider;

    @IsAdmin
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorProfileResponse create(@Valid @RequestBody CreateUserRequest request) {
        return service.createUserAndProfile(request);
    }

    @IsDoctor
    @GetMapping(ApiPaths.CURRENT)
    public DoctorProfileResponse getCurrent() {
        return service.findByUser(currentUserProvider.getCurrent());
    }

    @GetMapping(ApiPaths.BY_ID)
    public DoctorProfileResponse getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping
    public Page<DoctorProfileResponse> getAll(@ParameterObject Pageable pageable) {
        return service.findAll(pageable);
    }

    @IsDoctor
    @PatchMapping(ApiPaths.CURRENT)
    public DoctorProfileResponse updateCurrent(@Valid @RequestBody UpdateDoctorRequest request) {
        return service.updateById(request, currentUserProvider.getCurrent().getId());
    }

    @IsAdmin
    @PatchMapping(ApiPaths.BY_ID)
    public DoctorProfileResponse updateById(
            @RequestBody @Valid UpdateDoctorRequest request,
            @PathVariable Long id
    ) {
        return service.updateById(request, id);
    }

    @IsDoctor
    @DeleteMapping(ApiPaths.CURRENT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCurrent() {
        service.deleteById(currentUserProvider.getCurrent().getId());
    }

    @IsAdmin
    @DeleteMapping(ApiPaths.BY_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        service.deleteById(id);
    }
}
