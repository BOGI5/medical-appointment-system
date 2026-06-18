package com.medical.appointments.profiles.doctor;

import com.medical.appointments.exception.ProfileNotFoundException;
import com.medical.appointments.profiles.doctor.dto.CreateDoctorProfile;
import com.medical.appointments.user.dto.CreateUserRequest;
import com.medical.appointments.profiles.doctor.dto.DoctorProfileResponse;
import com.medical.appointments.profiles.doctor.dto.UpdateDoctorRequest;
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
        UpdateDoctorRequest,
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
    public DoctorProfileResponse createUserAndProfile(CreateUserRequest request) {
        User user = userService.create(mapper.toCreateUser(request));
        return create(new CreateDoctorProfile(user));
    }

    @Override
    public DoctorProfileResponse updateById(UpdateDoctorRequest request, Long id) {
        DoctorProfile profile = repository.findById(id).orElseThrow(ProfileNotFoundException::new);

        if (request.specializationId() != null) {
            profile.setSpecialization(
                    specializationService.findEntityById(request.specializationId())
            );
        }

        String phone = request.phone();
        if (phone != null && !phone.isBlank()) {
            profile.setPhone(phone);
        }

        String clinicAddress = request.clinicAddress();
        if (clinicAddress != null && !clinicAddress.isBlank()) {
            profile.setClinicAddress(clinicAddress);
        }

        String bio = request.bio();
        if (bio != null && !bio.isBlank()) {
            profile.setBio(bio);
        }

        if (request.yearsOfExperience() != null) {
            profile.setYearsOfExperience(request.yearsOfExperience());
        }

        return mapper.toResponse(repository.save(profile));
    }
}
