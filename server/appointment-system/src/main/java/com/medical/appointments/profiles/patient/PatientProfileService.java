package com.medical.appointments.profiles.patient;

import com.medical.appointments.exception.ProfileNotFoundException;
import com.medical.appointments.profiles.patient.dto.CreatePatientProfile;
import com.medical.appointments.profiles.patient.dto.PatientProfileResponse;
import com.medical.appointments.profiles.patient.dto.UpdatePatientProfile;
import com.medical.appointments.profiles.patient.mapper.PatientProfileMapper;
import com.medical.appointments.profiles.profile.ProfileService;
import com.medical.appointments.user.Role;
import com.medical.appointments.user.User;
import com.medical.appointments.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PatientProfileService extends ProfileService<
        PatientProfile,
        PatientProfileResponse,
        CreatePatientProfile,
        UpdatePatientProfile,
        PatientProfileRepository,
        PatientProfileMapper
        >
{
    public PatientProfileService(
            UserService userService,
            PatientProfileRepository repository,
            PatientProfileMapper mapper
    ) {
        super(userService, repository, mapper);
    }

    @Override
    protected Role getRole() {
        return Role.PATIENT;
    }

    @Transactional
    public PatientProfileResponse createForExistingUser(User user) {
        userService.addRole(user, getRole());
        return mapper.toResponse(create(new CreatePatientProfile(user)));
    }

    @Override
    public PatientProfileResponse updateById(UpdatePatientProfile updateProfile, Long id) {
        PatientProfile patientProfile = repository.findById(id)
                .orElseThrow(ProfileNotFoundException::new);

        if (updateProfile.address() != null) {
            patientProfile.setAddress(updateProfile.address());
        }

        if (updateProfile.phone() != null) {
            patientProfile.setPhone(updateProfile.phone());
        }

        if (updateProfile.allergies() != null) {
            patientProfile.setAllergies(updateProfile.allergies());
        }

        return mapper.toResponse(repository.save(patientProfile));
    }
}
