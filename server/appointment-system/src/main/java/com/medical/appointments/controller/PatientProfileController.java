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
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
        return patientProfileService.createForExistingUser(currentUserProvider.getCurrent());
    }

    @IsAdminOrDoctor
    @GetMapping
    public Page<PatientProfileResponse> getAll(@ParameterObject Pageable pageable) {
        return patientProfileService.findAll(pageable);
    }

    @IsPatient
    @GetMapping(ApiPaths.CURRENT)
    public PatientProfileResponse getCurrent() {
        return patientProfileService.findByUser(currentUserProvider.getCurrent());
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
                currentUserProvider.getCurrent().getId()
        );
    }

    @IsPatient
    @PostMapping(ApiPaths.CURRENT_ALLERGIES_BY_ID)
    public PatientProfileResponse addAllergyToCurrent(@PathVariable Long allergyId) {
        return patientProfileService.addAllergyById(allergyId, currentUserProvider.getCurrent().getId());
    }

    @IsAdmin
    @PatchMapping(ApiPaths.BY_ID)
    public PatientProfileResponse updateById(
            @RequestBody @Valid UpdatePatientProfile updatePatientProfile,
            @PathVariable Long id
    ) {
        return patientProfileService.updateById(updatePatientProfile, id);
    }

    @IsAdmin
    @PostMapping(ApiPaths.BY_ID_ALLERGY_BY_ID)
    public PatientProfileResponse addAllergyToPatientById(@PathVariable Long id, @PathVariable Long allergyId) {
        return patientProfileService.addAllergyById(allergyId, id);
    }

    @IsPatient
    @DeleteMapping(ApiPaths.CURRENT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCurrent() {
        patientProfileService.deleteById(currentUserProvider.getCurrent().getId());
    }

    @IsPatient
    @DeleteMapping(ApiPaths.CURRENT_ALLERGIES_BY_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAllergyFromCurrent(@PathVariable Long allergyId) {
        patientProfileService.removeAllergy(allergyId, currentUserProvider.getCurrent().getId());
    }

    @IsAdmin
    @DeleteMapping(ApiPaths.BY_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        patientProfileService.deleteById(id);
    }

    @IsAdmin
    @DeleteMapping(ApiPaths.BY_ID_ALLERGY_BY_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAllergyFromPatientById(@PathVariable Long id, @PathVariable Long allergyId) {
        patientProfileService.removeAllergy(allergyId, id);
    }
}
