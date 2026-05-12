package com.medical.appointments.profiles.doctor.mapper;

import com.medical.appointments.profiles.doctor.DoctorProfile;
import com.medical.appointments.profiles.doctor.dto.CreateDoctorProfile;
import com.medical.appointments.profiles.doctor.dto.DoctorProfileResponse;
import com.medical.appointments.profiles.profile.mapper.ProfileMapper;
import com.medical.appointments.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DoctorProfileMapper implements ProfileMapper<DoctorProfile, DoctorProfileResponse, CreateDoctorProfile> {
    private final UserMapper userMapper;

    @Override
    public DoctorProfileResponse toResponse(DoctorProfile profile) {
        return new DoctorProfileResponse(
                userMapper.toResponse(profile.getUser()),
                profile.getId(),
                profile.getSpecialization(),
                profile.getPhone(),
                profile.getClinicAddress(),
                profile.getBio(),
                profile.getYearsOfExperience()
        );
    }

    @Override
    public DoctorProfile toEntity(CreateDoctorProfile createProfile) {
        return new DoctorProfile(createProfile.user());
    }
}
