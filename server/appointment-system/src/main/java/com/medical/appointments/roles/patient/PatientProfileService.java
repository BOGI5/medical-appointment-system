package com.medical.appointments.roles.patient;

import com.medical.appointments.exception.PatientProfileAlreadyExistsException;
import com.medical.appointments.exception.PatientProfileNotFoundException;
import com.medical.appointments.roles.patient.dto.CreatePatientProfile;
import com.medical.appointments.roles.patient.dto.PatientProfileResponse;
import com.medical.appointments.roles.patient.dto.UpdatePatientProfile;
import com.medical.appointments.roles.patient.mapper.PatientProfileMapper;
import com.medical.appointments.user.Role;
import com.medical.appointments.user.User;
import com.medical.appointments.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientProfileService {
    private final PatientProfileRepository patientProfileRepository;
    private final PatientProfileMapper patientProfileMapper;
    private final UserService userService;

    public PatientProfile create(CreatePatientProfile createPatientProfile) {
        if (patientProfileRepository.existsByUser(createPatientProfile.user())) {
            throw new PatientProfileAlreadyExistsException();
        }

        return patientProfileRepository.save(patientProfileMapper.toEntity(createPatientProfile));
    }

    @Transactional
    public PatientProfileResponse createForExistingUser(User user) {
        userService.addRole(user, Role.PATIENT);
        return patientProfileMapper.toResponse(create(new CreatePatientProfile(user)));
    }

    // TODO: pagination, filtering, etc.
    public List<PatientProfileResponse> findAll() {
        return patientProfileRepository.findAll().stream().map(patientProfileMapper::toResponse).toList();
    }

    public PatientProfileResponse findByUser(User user) {
        return patientProfileMapper.toResponse(
                patientProfileRepository.findByUser(user).orElseThrow(PatientProfileNotFoundException::new)
        );
    }

    public PatientProfileResponse findById(Long id) {
        return patientProfileMapper.toResponse(
                patientProfileRepository.findById(id).orElseThrow(PatientProfileNotFoundException::new)
        );
    }

    public PatientProfileResponse updateById(UpdatePatientProfile updatePatientProfile, Long id) {
        PatientProfile patientProfile = patientProfileRepository.findById(id)
                .orElseThrow(PatientProfileNotFoundException::new);

        if (updatePatientProfile.address() != null) {
            patientProfile.setAddress(updatePatientProfile.address());
        }

        if (updatePatientProfile.phone() != null) {
            patientProfile.setPhone(updatePatientProfile.phone());
        }

        if (updatePatientProfile.allergies() != null) {
            patientProfile.setAllergies(updatePatientProfile.allergies());
        }

        return patientProfileMapper.toResponse(patientProfileRepository.save(patientProfile));
    }

    @Transactional
    public void deleteById(Long id) {
        PatientProfile patientProfile = patientProfileRepository.findById(id)
                .orElseThrow(PatientProfileNotFoundException::new);
        userService.removeRole(patientProfile.getUser(), Role.PATIENT);
        patientProfileRepository.delete(patientProfile);
    }
}
