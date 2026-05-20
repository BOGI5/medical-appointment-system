package com.medical.appointments.profiles.patient;

import com.medical.appointments.exception.*;
import com.medical.appointments.profiles.patient.dto.CreatePatientProfile;
import com.medical.appointments.profiles.patient.dto.PatientProfileResponse;
import com.medical.appointments.profiles.patient.dto.UpdatePatientProfile;
import com.medical.appointments.profiles.patient.mapper.PatientProfileMapper;
import com.medical.appointments.references.allergy.Allergy;
import com.medical.appointments.references.allergy.AllergyService;
import com.medical.appointments.user.Role;
import com.medical.appointments.user.User;
import com.medical.appointments.user.UserService;
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
class PatientProfileServiceTest {

    @Mock private UserService userService;
    @Mock private PatientProfileRepository repository;
    @Mock private PatientProfileMapper mapper;
    @Mock private AllergyService allergyService;

    @InjectMocks
    private PatientProfileService service;

    // ===== CREATE =====

    @Test
    void createForExistingUser_success() {
        // given
        User user = createUser();

        PatientProfile profile = createProfile(user);

        PatientProfileResponse response = createResponse(user, "address", "088");

        when(userService.addRole(user, Role.PATIENT))
                .thenReturn(user);

        when(repository.existsByUser(user))
                .thenReturn(false);

        when(mapper.toEntity(any(CreatePatientProfile.class)))
                .thenReturn(profile);

        when(repository.save(profile))
                .thenReturn(profile);

        when(mapper.toResponse(profile))
                .thenReturn(response);

        // when
        PatientProfileResponse result =
                service.createForExistingUser(user);

        // then
        assertNotNull(result);
        assertEquals(response, result);

        verify(userService).addRole(user, Role.PATIENT);
        verify(repository).existsByUser(user);
        verify(mapper).toEntity(any(CreatePatientProfile.class));
        verify(repository).save(profile);
        verify(mapper).toResponse(profile);
    }

    @Test
    void createForExistingUser_profileAlreadyExists() {
        // given
        User user = createUser();

        when(userService.addRole(user, Role.PATIENT))
                .thenReturn(user);

        when(repository.existsByUser(user))
                .thenReturn(true);

        // when + then
        assertThrows(
                ProfileAlreadyExistsException.class,
                () -> service.createForExistingUser(user)
        );

        verify(userService).addRole(user, Role.PATIENT);
        verify(repository).existsByUser(user);

        verify(repository, never()).save(any());
        verifyNoInteractions(mapper);
    }

    // ===== UPDATE =====

    @Test
    void updateById_success() {
        // given
        Long id = 1L;

        User user = createUser();

        PatientProfile profile = createProfile(user);

        UpdatePatientProfile request =
                new UpdatePatientProfile(
                        "new address",
                        "0888888888"
                );

        PatientProfileResponse response = createResponse(user, "new address", "0888888888");

        when(repository.findById(id))
                .thenReturn(Optional.of(profile));

        when(repository.save(profile))
                .thenReturn(profile);

        when(mapper.toResponse(profile))
                .thenReturn(response);

        // when
        PatientProfileResponse result =
                service.updateById(request, id);

        // then
        assertNotNull(result);
        assertEquals(response, result);

        assertEquals("new address", profile.getAddress());
        assertEquals("0888888888", profile.getPhone());

        verify(repository).findById(id);
        verify(repository).save(profile);
        verify(mapper).toResponse(profile);
    }

    @Test
    void updateById_notFound() {
        // given
        Long id = 1L;

        UpdatePatientProfile request =
                new UpdatePatientProfile(
                        "address",
                        "088"
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
        verifyNoInteractions(mapper);
    }

    // ===== ALLERGIES =====

    @Test
    void addAllergyById_success() {
        // given
        Long allergyId = 1L;
        Long profileId = 2L;

        User user = createUser();

        PatientProfile profile = createProfile(user);

        Allergy allergy = createAllergy();

        PatientProfileResponse response = createResponse(user, "address", "088");

        when(repository.findById(profileId))
                .thenReturn(Optional.of(profile));

        when(allergyService.findEntityById(allergyId))
                .thenReturn(allergy);

        when(repository.save(profile))
                .thenReturn(profile);

        when(mapper.toResponse(profile))
                .thenReturn(response);

        // when
        PatientProfileResponse result =
                service.addAllergyById(allergyId, profileId);

        // then
        assertEquals(response, result);

        assertTrue(profile.getAllergies().contains(allergy));

        verify(repository).findById(profileId);
        verify(allergyService).findEntityById(allergyId);
        verify(repository).save(profile);
        verify(mapper).toResponse(profile);
    }

    @Test
    void addAllergyById_profileNotFound() {
        // given
        Long allergyId = 1L;
        Long profileId = 2L;

        when(repository.findById(profileId))
                .thenReturn(Optional.empty());

        // when + then
        assertThrows(
                ProfileNotFoundException.class,
                () -> service.addAllergyById(allergyId, profileId)
        );

        verify(repository).findById(profileId);

        verifyNoInteractions(allergyService, mapper);

        verify(repository, never()).save(any());
    }

    @Test
    void addAllergyById_allergyNotFound() {
        // given
        Long allergyId = 1L;
        Long profileId = 2L;

        User user = createUser();

        PatientProfile profile = createProfile(user);

        when(repository.findById(profileId))
                .thenReturn(Optional.of(profile));

        when(allergyService.findEntityById(allergyId))
                .thenThrow(ReferenceNotFoundException.class);

        // when + then
        assertThrows(
                ReferenceNotFoundException.class,
                () -> service.addAllergyById(allergyId, profileId)
        );

        verify(repository).findById(profileId);
        verify(allergyService).findEntityById(allergyId);

        verify(repository, never()).save(any());
        verifyNoInteractions(mapper);
    }

    @Test
    void addAllergyById_alreadyAssigned() {
        // given
        Long allergyId = 1L;
        Long profileId = 2L;

        User user = createUser();

        PatientProfile profile = createProfile(user);

        Allergy allergy = createAllergy();

        profile.addAllergy(allergy);

        when(repository.findById(profileId))
                .thenReturn(Optional.of(profile));

        when(allergyService.findEntityById(allergyId))
                .thenReturn(allergy);

        // when + then
        assertThrows(
                ReferenceAlreadyAssignedException.class,
                () -> service.addAllergyById(allergyId, profileId)
        );

        verify(repository).findById(profileId);
        verify(allergyService).findEntityById(allergyId);

        verify(repository, never()).save(any());
        verifyNoInteractions(mapper);
    }

    @Test
    void removeAllergy_success() {
        // given
        Long allergyId = 1L;
        Long profileId = 2L;

        User user = createUser();

        PatientProfile profile = createProfile(user);

        Allergy allergy = createAllergy();

        profile.addAllergy(allergy);

        when(repository.findById(profileId))
                .thenReturn(Optional.of(profile));

        when(allergyService.findEntityById(allergyId))
                .thenReturn(allergy);

        // when
        service.removeAllergy(allergyId, profileId);

        // then
        assertFalse(profile.getAllergies().contains(allergy));

        verify(repository).findById(profileId);
        verify(allergyService).findEntityById(allergyId);
        verify(repository).save(profile);
    }

    @Test
    void removeAllergy_profileNotFound() {
        // given
        Long allergyId = 1L;
        Long profileId = 2L;

        when(repository.findById(profileId))
                .thenReturn(Optional.empty());

        // when + then
        assertThrows(
                ProfileNotFoundException.class,
                () -> service.removeAllergy(allergyId, profileId)
        );

        verify(repository).findById(profileId);

        verifyNoInteractions(allergyService);

        verify(repository, never()).save(any());
    }

    @Test
    void removeAllergy_allergyNotFound() {
        // given
        Long allergyId = 1L;
        Long profileId = 2L;

        User user = createUser();

        PatientProfile profile = createProfile(user);

        when(repository.findById(profileId))
                .thenReturn(Optional.of(profile));

        when(allergyService.findEntityById(allergyId))
                .thenThrow(ReferenceNotFoundException.class);

        // when + then
        assertThrows(
                ReferenceNotFoundException.class,
                () -> service.removeAllergy(allergyId, profileId)
        );

        verify(repository).findById(profileId);
        verify(allergyService).findEntityById(allergyId);

        verify(repository, never()).save(any());
    }

    @Test
    void removeAllergy_notAssigned() {
        // given
        Long allergyId = 1L;
        Long profileId = 2L;

        User user = createUser();

        PatientProfile profile = createProfile(user);

        Allergy allergy = createAllergy();

        when(repository.findById(profileId))
                .thenReturn(Optional.of(profile));

        when(allergyService.findEntityById(allergyId))
                .thenReturn(allergy);

        // when + then
        assertThrows(
                ReferenceNotAssignedException.class,
                () -> service.removeAllergy(allergyId, profileId)
        );

        verify(repository).findById(profileId);
        verify(allergyService).findEntityById(allergyId);

        verify(repository, never()).save(any());
    }

    // ===== FIND =====

    @Test
    void findById_success() {
        // given
        Long id = 1L;

        User user = createUser();

        PatientProfile profile =
                createProfile(user);

        PatientProfileResponse response = createResponse(user, "address", "088");

        when(repository.findById(id))
                .thenReturn(Optional.of(profile));

        when(mapper.toResponse(profile))
                .thenReturn(response);

        // when
        PatientProfileResponse result =
                service.findById(id);

        // then
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
        User user = createUser();

        PatientProfile profile =
                createProfile(user);

        PatientProfileResponse response =
                createResponse(user,  "address", "088");

        when(repository.findByUser(user))
                .thenReturn(Optional.of(profile));

        when(mapper.toResponse(profile))
                .thenReturn(response);

        // when
        PatientProfileResponse result =
                service.findByUser(user);

        // then
        assertEquals(response, result);

        verify(repository).findByUser(user);
        verify(mapper).toResponse(profile);
    }

    @Test
    void findByUser_notFound() {
        // given
        User user = createUser();

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

        User user = createUser();

        PatientProfile profile =
                createProfile(user);

        when(repository.findById(id))
                .thenReturn(Optional.of(profile));

        // when
        service.deleteById(id);

        // then
        InOrder inOrder =
                inOrder(userService, repository);

        inOrder.verify(userService)
                .removeRole(user, Role.PATIENT);

        inOrder.verify(repository)
                .delete(profile);
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

    private User createUser() {
        return User.builder()
                .email("mail")
                .password("encoded")
                .firstName("John")
                .lastName("Doe")
                .roles(Set.of(Role.PATIENT))
                .activeRole(Role.PATIENT)
                .build();
    }

    private PatientProfile createProfile(User user) {
        PatientProfile profile = new PatientProfile(user);
        profile.setAddress("address");
        profile.setPhone("088");
        return profile;
    }

    private Allergy createAllergy() {
        return new Allergy("Peanuts");
    }

    private PatientProfileResponse createResponse(User user, String address, String phone) {
        return new PatientProfileResponse(
                new UserResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getRoles(),
                        user.getActiveRole()
                ),
                1L,
                address,
                phone,
                null,
                Set.of()
        );
    }
}
