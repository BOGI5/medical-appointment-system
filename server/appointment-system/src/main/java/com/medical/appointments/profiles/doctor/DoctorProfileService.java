package com.medical.appointments.profiles.doctor;

import com.medical.appointments.exception.ProfileNotFoundException;
import com.medical.appointments.profiles.doctor.dto.CreateDoctorProfile;
import com.medical.appointments.profiles.doctor.dto.CreateUserAndDoctorProfileRequest;
import com.medical.appointments.profiles.doctor.dto.DoctorProfileResponse;
import com.medical.appointments.profiles.doctor.dto.UpdateDoctorProfile;
import com.medical.appointments.profiles.doctor.mapper.DoctorProfileMapper;
import com.medical.appointments.profiles.profile.ProfileService;
import com.medical.appointments.references.specialization.SpecializationService;
import com.medical.appointments.user.Role;
import com.medical.appointments.user.User;
import com.medical.appointments.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DoctorProfileService extends ProfileService<
        DoctorProfile,
        DoctorProfileResponse,
        CreateDoctorProfile,
        UpdateDoctorProfile,
        DoctorProfileRepository,
        DoctorProfileMapper
        >
{
    private final SpecializationService specializationService;

    public DoctorProfileService(
            UserService userService,
            DoctorProfileRepository repository,
            DoctorProfileMapper mapper,
            SpecializationService specializationService
    ) {
        super(userService, repository, mapper);
        this.specializationService = specializationService;
    }

    @Override
    protected Role getRole() {
        return Role.DOCTOR;
    }

    @Transactional
    public DoctorProfileResponse createUserAndProfile(
            CreateUserAndDoctorProfileRequest createUserAndDoctorProfileRequest
    ) {
        User user = userService.create(mapper.toCreateUser(createUserAndDoctorProfileRequest));
        return create(new CreateDoctorProfile(user));
    }

    @Override
    public DoctorProfileResponse updateById(UpdateDoctorProfile updateProfile, Long id) {
        DoctorProfile profile = repository.findById(id).orElseThrow(ProfileNotFoundException::new);

        if (updateProfile.specializationId() != null) {
            profile.setSpecialization(
                    specializationService.findEntityById(updateProfile.specializationId())
            );
        }

        if (updateProfile.phone() != null && !updateProfile.phone().isBlank()) {
            profile.setPhone(updateProfile.phone());
        }

        if (updateProfile.clinicAddress() != null && !updateProfile.clinicAddress().isBlank()) {
            profile.setClinicAddress(updateProfile.clinicAddress());
        }

        if (updateProfile.bio() != null && !updateProfile.bio().isBlank()) {
            profile.setBio(updateProfile.bio());
        }

        if (updateProfile.yearsOfExperience() != null) {
            profile.setYearsOfExperience(updateProfile.yearsOfExperience());
        }

        return mapper.toResponse(repository.save(profile));
    }
}
