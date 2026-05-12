package com.medical.appointments.controller;

import com.medical.appointments.profiles.patient.PatientProfileService;
import com.medical.appointments.profiles.patient.dto.PatientProfileResponse;
import com.medical.appointments.profiles.patient.dto.UpdatePatientProfile;
import com.medical.appointments.security.CurrentUserProvider;
import com.medical.appointments.security.annotation.role.IsAdmin;
import com.medical.appointments.security.annotation.role.IsAdminOrDoctor;
import com.medical.appointments.security.annotation.role.IsPatient;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.PATIENT_PROFILES)
@RequiredArgsConstructor
public class PatientProfileController {
    private final PatientProfileService patientProfileService;
    private final CurrentUserProvider currentUserProvider;

    @IsAdminOrDoctor
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientProfileResponse create() {
        return patientProfileService.createForExistingUser(currentUserProvider.getCurrentUser());
    }

    // TODO: pagination, filtering, etc.
    @IsAdminOrDoctor
    @GetMapping
    public List<PatientProfileResponse> getAll() {
        return patientProfileService.findAll();
    }

    @IsPatient
    @GetMapping(ApiPaths.CURRENT)
    public PatientProfileResponse getCurrent() {
        return patientProfileService.findByUser(currentUserProvider.getCurrentUser());
    }

    @IsAdminOrDoctor
    @GetMapping(ApiPaths.BY_ID)
    public PatientProfileResponse getById(@PathVariable Long id) {
        return patientProfileService.findById(id);
    }

    @IsPatient
    @PatchMapping(ApiPaths.CURRENT)
    public PatientProfileResponse updateCurrent(
            @RequestBody @Valid UpdatePatientProfile updatePatientProfile
    ) {
        return patientProfileService.updateById(
                updatePatientProfile,
                currentUserProvider.getCurrentUser().getId()
        );
    }

    @IsAdmin
    @PatchMapping(ApiPaths.BY_ID)
    public PatientProfileResponse updateById(
            @RequestBody @Valid UpdatePatientProfile updatePatientProfile,
            @PathVariable Long id
    ) {
        return patientProfileService.updateById(updatePatientProfile, id);
    }

    @IsPatient
    @DeleteMapping(ApiPaths.CURRENT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCurrent() {
        patientProfileService.deleteById(currentUserProvider.getCurrentUser().getId());
    }

    @IsAdmin
    @DeleteMapping(ApiPaths.BY_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        patientProfileService.deleteById(id);
    }
}
