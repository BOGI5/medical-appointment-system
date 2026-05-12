package com.medical.appointments.profiles.profile.mapper;

import com.medical.appointments.profiles.profile.Profile;
import com.medical.appointments.profiles.profile.dto.CreateProfile;
import com.medical.appointments.profiles.profile.dto.ProfileResponse;

public interface ProfileMapper<E extends Profile, R extends ProfileResponse, C extends CreateProfile> {
    R toResponse(E profile);
    E toEntity(C createProfile);
}
