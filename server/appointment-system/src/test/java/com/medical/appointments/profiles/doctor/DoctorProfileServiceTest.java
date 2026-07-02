package com.medical.appointments.profiles.doctor;

import com.medical.appointments.exception.*;
import com.medical.appointments.profiles.doctor.dto.CreateDoctorProfile;
import com.medical.appointments.user.dto.CreateUserRequest;
import com.medical.appointments.profiles.doctor.dto.DoctorProfileResponse;
import com.medical.appointments.profiles.doctor.dto.UpdateDoctorRequest;
import com.medical.appointments.profiles.doctor.mapper.DoctorProfileMapper;
import com.medical.appointments.references.specialization.Specialization;
import com.medical.appointments.references.specialization.SpecializationService;
import com.medical.appointments.references.specialization.dto.SpecializationResponse;
import com.medical.appointments.user.Role;
import com.medical.appointments.user.User;
import com.medical.appointments.user.UserService;
import com.medical.appointments.user.dto.CreateUser;
import com.medical.appointments.user.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorProfileServiceTest {

    @Mock private UserService userService;
    @Mock private DoctorProfileRepository repository;
    @Mock private DoctorProfileMapper mapper;
    @Mock private SpecializationService specializationService;

    @InjectMocks
    private DoctorProfileService service;

    // ===== CREATE =====

    @Test
    void createUserAndProfile_success() {
        // given
        CreateUserRequest request =
                createCreateUserAndDoctorProfileRequest();

        CreateUser createUser = createCreateUser();

        User user = createDoctorUser();

        DoctorProfile profile = createProfile(user);

        DoctorProfileResponse response = createResponse(user, null, "088", "clinic", "bio", 5);

        when(mapper.toCreateUser(request))
                .thenReturn(createUser);

        when(userService.create(createUser))
                .thenReturn(user);

        when(repository.existsByUser(user))
                .thenReturn(false);

        when(mapper.toEntity(any(CreateDoctorProfile.class)))
                .thenReturn(profile);

        when(repository.save(profile))
                .thenReturn(profile);

        when(mapper.toResponse(profile))
                .thenReturn(response);

        // when
        DoctorProfileResponse result =
                service.createUserAndProfile(request);

        // then
        assertNotNull(result);
        assertEquals(response, result);

        verify(mapper).toCreateUser(request);
        verify(userService).create(createUser);
        verify(repository).existsByUser(user);
        verify(mapper).toEntity(any(CreateDoctorProfile.class));
        verify(repository).save(profile);
        verify(mapper).toResponse(profile);
    }

    @Test
    void createUserAndProfile_userAlreadyExists() {
        // given
        CreateUserRequest request =
                createCreateUserAndDoctorProfileRequest();

        CreateUser createUser = createCreateUser();

        when(mapper.toCreateUser(request))
                .thenReturn(createUser);

        when(userService.create(createUser))
                .thenThrow(new UserAlreadyExistsException());

        // when + then
        assertThrows(
                UserAlreadyExistsException.class,
                () -> service.createUserAndProfile(request)
        );

        verify(mapper).toCreateUser(request);
        verify(userService).create(createUser);

        verifyNoInteractions(repository);

        verify(mapper, never()).toEntity(any());
        verify(mapper, never()).toResponse(any());
    }

    @Test
    void createUserAndProfile_profileAlreadyExists() {
        // given
        CreateUserRequest request =
                createCreateUserAndDoctorProfileRequest();

        CreateUser createUser = createCreateUser();

        User user = createDoctorUser();

        when(mapper.toCreateUser(request))
                .thenReturn(createUser);

        when(userService.create(createUser))
                .thenReturn(user);

        when(repository.existsByUser(user))
                .thenReturn(true);

        // when + then
        assertThrows(
                ProfileAlreadyExistsException.class,
                () -> service.createUserAndProfile(request)
        );

        verify(mapper).toCreateUser(request);
        verify(userService).create(createUser);
        verify(repository).existsByUser(user);

        verify(repository, never()).save(any());

        verify(mapper, never()).toResponse(any());
        verify(mapper, never()).toEntity(any());
    }

    // ===== UPDATE =====

    @Test
    void updateById_success() {
        // given
        Long id = 1L;
        Long specializationId = 1L;

        User user = createDoctorUser();

        DoctorProfile profile = createProfile(user);

        Specialization specialization =
                createSpecialization();

        UpdateDoctorRequest request =
                new UpdateDoctorRequest(
                        specializationId,
                        "0888888888",
                        "new clinic",
                        "new bio",
                        10
                );

        DoctorProfileResponse response =
                createResponse(user, specialization, "0888888888", "new clinic", "new bio", 10);

        when(repository.findById(id))
                .thenReturn(Optional.of(profile));

        when(specializationService.findEntityById(specializationId))
                .thenReturn(specialization);

        when(repository.save(profile))
                .thenReturn(profile);

        when(mapper.toResponse(profile))
                .thenReturn(response);

        // when
        DoctorProfileResponse result = service.updateById(request, id);

        // then
        assertNotNull(result);
        assertEquals(response, result);

        assertEquals(specialization, profile.getSpecialization());

        assertEquals("0888888888", profile.getPhone());

        assertEquals("new clinic", profile.getClinicAddress());

        assertEquals("new bio", profile.getBio());

        assertEquals(10, profile.getYearsOfExperience());

        verify(repository).findById(id);
        verify(specializationService).findEntityById(specializationId);
        verify(repository).save(profile);
        verify(mapper).toResponse(profile);
    }

    @Test
    void updateById_notFound() {
        // given
        Long id = 1L;

        UpdateDoctorRequest request =
                new UpdateDoctorRequest(
                        null,
                        "088",
                        "clinic",
                        "bio",
                        5
                );

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        // when + then
        assertThrows(
                ProfileNotFoundException.class,
                () -> service.updateById(request, id)
        );

        verify(repository).findById(id);

        verify(repository, never()).save(any());

        verifyNoInteractions(
                mapper,
                specializationService
        );
    }

    @Test
    void updateById_specializationNotFound() {
        // given
        Long id = 1L;

        User user = createDoctorUser();

        DoctorProfile profile = createProfile(user);

        UpdateDoctorRequest request =
                new UpdateDoctorRequest(
                        1L,
                        "088",
                        "clinic",
                        "bio",
                        5
                );

        when(repository.findById(id))
                .thenReturn(Optional.of(profile));

        when(specializationService.findEntityById(1L))
                .thenThrow(new ReferenceNotFoundException());

        // when + then
        assertThrows(
                ReferenceNotFoundException.class,
                () -> service.updateById(request, id)
        );

        verify(repository).findById(id);
        verify(specializationService).findEntityById(1L);

        verify(repository, never()).save(any());

        verifyNoInteractions(mapper);
    }

    // ===== FIND =====

    @Test
    void findById_success() {
        // given
        Long id = 1L;

        User user = createDoctorUser();

        DoctorProfile profile = createProfile(user);

        DoctorProfileResponse response =
                createResponse(user, null, "088", "clinic", "bio", 5);

        when(repository.findById(id))
                .thenReturn(Optional.of(profile));

        when(mapper.toResponse(profile))
                .thenReturn(response);

        // when
        DoctorProfileResponse result =
                service.findById(id);

        // then
        assertNotNull(result);
        assertEquals(response, result);

        verify(repository).findById(id);
        verify(mapper).toResponse(profile);
    }

    @Test
    void findById_notFound() {
        // given
        Long id = 1L;

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        // when + then
        assertThrows(
                ProfileNotFoundException.class,
                () -> service.findById(id)
        );

        verify(repository).findById(id);

        verifyNoInteractions(mapper);
    }

    @Test
    void findByUser_success() {
        // given
        User user = createDoctorUser();

        DoctorProfile profile = createProfile(user);

        DoctorProfileResponse response =
                createResponse(user, null, "088", "clinic", "bio", 5);

        when(repository.findByUser(user))
                .thenReturn(Optional.of(profile));

        when(mapper.toResponse(profile))
                .thenReturn(response);

        // when
        DoctorProfileResponse result =
                service.findByUser(user);

        // then
        assertNotNull(result);
        assertEquals(response, result);

        verify(repository).findByUser(user);
        verify(mapper).toResponse(profile);
    }

    @Test
    void findByUser_notFound() {
        // given
        User user = createDoctorUser();

        when(repository.findByUser(user))
                .thenReturn(Optional.empty());

        // when + then
        assertThrows(
                ProfileNotFoundException.class,
                () -> service.findByUser(user)
        );

        verify(repository).findByUser(user);

        verifyNoInteractions(mapper);
    }

    // ===== DELETE =====

    @Test
    void deleteById_success() {
        // given
        Long id = 1L;

        User user = createDoctorUser();

        DoctorProfile profile = createProfile(user);

        when(repository.findById(id))
                .thenReturn(Optional.of(profile));

        // when
        service.deleteById(id);

        // then
        InOrder inOrder =
                inOrder(userService, repository);

        inOrder.verify(userService)
                .removeRole(user, Role.DOCTOR);

        inOrder.verify(repository)
                .delete(profile);

        verify(repository, never()).save(any());
    }

    @Test
    void deleteById_notFound() {
        // given
        Long id = 1L;

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        // when + then
        assertThrows(
                ProfileNotFoundException.class,
                () -> service.deleteById(id)
        );

        verify(repository).findById(id);

        verify(repository, never()).delete(any());

        verifyNoInteractions(userService);
    }

    // ===== helper =====

    private User createDoctorUser() {
        return User.builder()
                .email("doctor@mail.com")
                .password("encoded")
                .firstName("John")
                .lastName("Doe")
                .roles(Set.of(Role.DOCTOR))
                .activeRole(Role.DOCTOR)
                .build();
    }

    private DoctorProfile createProfile(User user) {
        DoctorProfile profile = new DoctorProfile(user);
        profile.setPhone("088");
        profile.setClinicAddress("clinic");
        profile.setBio("bio");
        profile.setYearsOfExperience(5);
        return profile;
    }

    private Specialization createSpecialization() {
        return new Specialization("Cardiology");
    }

    private CreateUser createCreateUser() {
        return new CreateUser(
                "doctor@mail.com",
                "password",
                Set.of(Role.DOCTOR),
                Role.DOCTOR,
                "John",
                "Doe"
        );
    }

    private CreateUserRequest createCreateUserAndDoctorProfileRequest() {
        return new CreateUserRequest(
                "doctor@mail.com",
                "password",
                "John",
                "Doe"
        );
    }

    private DoctorProfileResponse createResponse(
            User user,
            Specialization specialization,
            String phone,
            String clinicAddress,
            String bio,
            Integer yearsOfExperience
    ) {
        specialization = specialization == null ? createSpecialization() : specialization;
        return new DoctorProfileResponse(
                new UserResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getRoles(),
                        user.getActiveRole()
                ),
                1L,
                new SpecializationResponse(
                        specialization.getId(),
                        specialization.getName()
                ),
                phone,
                clinicAddress,
                bio,
                yearsOfExperience
        );
    }
}
