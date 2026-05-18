package com.medical.appointments.profiles.patient;

import com.medical.appointments.exception.ProfileNotFoundException;
import com.medical.appointments.profiles.patient.dto.CreatePatientProfile;
import com.medical.appointments.profiles.patient.dto.PatientProfileResponse;
import com.medical.appointments.profiles.patient.dto.UpdatePatientProfile;
import com.medical.appointments.profiles.patient.mapper.PatientProfileMapper;
import com.medical.appointments.profiles.profile.ProfileService;
import com.medical.appointments.references.allergy.Allergy;
import com.medical.appointments.references.allergy.AllergyService;
import com.medical.appointments.user.Role;
import com.medical.appointments.user.User;
import com.medical.appointments.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

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
    private final AllergyService allergyService;

    public PatientProfileService(
            UserService userService,
            PatientProfileRepository repository,
            PatientProfileMapper mapper,
            AllergyService allergyService
    ) {
        super(userService, repository, mapper);
        this.allergyService = allergyService;
    }

    @Override
    protected Role getRole() {
        return Role.PATIENT;
    }

    @Transactional
    public PatientProfileResponse createForExistingUser(User user) {
        user = userService.addRole(user, getRole());
        return create(new CreatePatientProfile(user));
    }

    @Override
    public PatientProfileResponse updateById(UpdatePatientProfile updateProfile, Long id) {
        PatientProfile patientProfile = findEntityById(id);

        if (updateProfile.address() != null) {
            patientProfile.setAddress(updateProfile.address());
        }

        if (updateProfile.phone() != null) {
            patientProfile.setPhone(updateProfile.phone());
        }

        return mapper.toResponse(repository.save(patientProfile));
    }

    public PatientProfileResponse addAllergyById(Long allergyId, Long profileId) {
        PatientProfile patientProfile = findEntityById(profileId);
        Allergy allergy = allergyService.findEntityById(allergyId);
        patientProfile.addAllergy(allergy);
        return mapper.toResponse(repository.save(patientProfile));
    }

    public void removeAllergy(Long allergyId, Long profileId) {
        PatientProfile patientProfile = findEntityById(profileId);
        Allergy allergy = allergyService.findEntityById(allergyId);
        patientProfile.removeAllergy(allergy);
        repository.save(patientProfile);
    }
}
