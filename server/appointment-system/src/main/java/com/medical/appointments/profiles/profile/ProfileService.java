package com.medical.appointments.profiles.profile;

import com.medical.appointments.exception.ProfileAlreadyExistsException;
import com.medical.appointments.exception.ProfileNotFoundException;
import com.medical.appointments.profiles.profile.dto.CreateProfile;
import com.medical.appointments.profiles.profile.dto.ProfileResponse;
import com.medical.appointments.profiles.profile.dto.UpdateProfile;
import com.medical.appointments.profiles.profile.mapper.ProfileMapper;
import com.medical.appointments.user.Role;
import com.medical.appointments.user.User;
import com.medical.appointments.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
public abstract class ProfileService<
        E extends Profile,
        R extends ProfileResponse,
        C extends CreateProfile,
        U extends UpdateProfile,
        Repository extends ProfileRepository<E>,
        Mapper extends ProfileMapper<E, R, C>
        >
{
    protected final UserService userService;
    protected final Repository repository;
    protected final Mapper mapper;

    protected abstract Role getRole();

    public abstract R updateById(U updateProfile, Long id);

    public R create(C createProfile) {
        if (repository.existsByUser(createProfile.user())) {
            throw new ProfileAlreadyExistsException();
        }

        return mapper.toResponse(repository.save(mapper.toEntity(createProfile)));
    }

    public List<R> findAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    public R findById(Long id) {
        return mapper.toResponse(
                repository.findById(id).orElseThrow(ProfileNotFoundException::new)
        );
    }

    public R findByUser(User user) {
        return mapper.toResponse(
                repository.findByUser(user).orElseThrow(ProfileNotFoundException::new)
        );
    }

    @Transactional
    public void deleteById(Long id) {
        E profile = repository.findById(id).orElseThrow(ProfileNotFoundException::new);
        userService.removeRole(profile.getUser(), getRole());
        repository.delete(profile);
    }
}
