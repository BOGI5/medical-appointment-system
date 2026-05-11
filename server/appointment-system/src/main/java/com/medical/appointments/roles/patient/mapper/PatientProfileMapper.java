package com.medical.appointments.roles.patient.mapper;

import com.medical.appointments.roles.patient.PatientProfile;
import com.medical.appointments.roles.patient.dto.CreatePatientProfile;
import com.medical.appointments.roles.patient.dto.PatientProfileResponse;
import com.medical.appointments.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PatientProfileMapper {
    private final UserMapper userMapper;

    public PatientProfile toEntity(CreatePatientProfile createPatientProfile) {
        return new PatientProfile(createPatientProfile.user());
    }

    public PatientProfileResponse toResponse(PatientProfile patientProfile) {
        return new PatientProfileResponse(
                userMapper.toResponse(patientProfile.getUser()),
                patientProfile.getId(),
                patientProfile.getAddress(),
                patientProfile.getPhone(),
                patientProfile.getMedicalHistory(),
                patientProfile.getAllergies()
        );
    }
}
