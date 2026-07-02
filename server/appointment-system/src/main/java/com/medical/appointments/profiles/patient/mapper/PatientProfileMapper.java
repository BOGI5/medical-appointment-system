package com.medical.appointments.profiles.patient.mapper;

import com.medical.appointments.profiles.patient.PatientProfile;
import com.medical.appointments.profiles.patient.dto.CreatePatientProfile;
import com.medical.appointments.profiles.patient.dto.PatientProfileResponse;
import com.medical.appointments.profiles.profile.mapper.ProfileMapper;
import com.medical.appointments.references.allergy.mapper.AllergyMapper;
import com.medical.appointments.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PatientProfileMapper implements ProfileMapper<PatientProfile, PatientProfileResponse, CreatePatientProfile> {
    private final UserMapper userMapper;
    private final AllergyMapper allergyMapper;

    public PatientProfile toEntity(CreatePatientProfile createProfile) {
        return new PatientProfile(createProfile.user());
    }

    public PatientProfileResponse toResponse(PatientProfile profile) {
        return new PatientProfileResponse(
                userMapper.toResponse(profile.getUser()),
                profile.getId(),
                profile.getAddress(),
                profile.getPhone(),
                profile.getMedicalHistory(),
                profile.getAllergies()
                        .stream()
                        .map(allergyMapper::toResponse)
                        .collect(Collectors.toSet())
        );
    }
}
