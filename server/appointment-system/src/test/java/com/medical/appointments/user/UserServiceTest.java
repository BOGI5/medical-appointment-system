package com.medical.appointments.user;

import com.medical.appointments.exception.*;
import com.medical.appointments.user.dto.ChangePasswordRequest;
import com.medical.appointments.user.dto.CreateUser;
import com.medical.appointments.user.dto.UpdateUserRequest;
import com.medical.appointments.user.dto.UserResponse;
import com.medical.appointments.user.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UserService userService;

    // ===== CREATE =====

    @Test
    void create_success() {
        // given
        CreateUser createUser = createPatientCreateUser();

        User user = createUser();

        when(userRepository.existsByEmail("mail"))
                .thenReturn(false);

        when(passwordEncoder.encode("password"))
                .thenReturn("encoded");

        when(userMapper.toEntity(createUser, "encoded"))
                .thenReturn(user);

        when(userRepository.save(user))
                .thenReturn(user);

        // when
        User result = userService.create(createUser);

        // then
        assertNotNull(result);
        assertEquals("mail", result.getEmail());
        assertEquals(Role.PATIENT, result.getActiveRole());

        verify(userRepository).existsByEmail("mail");
        verify(passwordEncoder).encode("password");
        verify(userMapper).toEntity(createUser, "encoded");
        verify(userRepository).save(user);
    }

    @Test
    void create_userAlreadyExists() {
        // given
        CreateUser createUser = createPatientCreateUser();

        when(userRepository.existsByEmail("mail"))
                .thenReturn(true);

        // when + then
        assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.create(createUser)
        );

        verify(userRepository).existsByEmail("mail");

        verifyNoInteractions(userMapper, passwordEncoder);
        verify(userRepository, never()).save(any());
    }

    // ===== FIND AND CHECK CREDENTIALS =====

    @Test
    void findAndCheckCredentials_success() {
        // given
        User user = createUser();

        when(userRepository.findByEmail("mail"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("password", "encoded"))
                .thenReturn(true);

        // when
        User result = userService.findAndCheckCredentials("mail", "password");

        // then
        assertNotNull(result);
        assertEquals("mail", result.getEmail());
        assertEquals(Role.PATIENT, result.getActiveRole());

        verify(userRepository).findByEmail("mail");
        verify(passwordEncoder).matches("password", "encoded");
    }

    @Test
    void findAndCheckCredentials_userNotFound() {
        // given
        when(userRepository.findByEmail("mail"))
                .thenReturn(Optional.empty());

        // when + then
        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.findAndCheckCredentials("mail", "password")
        );

        verify(userRepository).findByEmail("mail");

        verifyNoInteractions(passwordEncoder);

        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findAndCheckCredentials_invalidPassword() {
        // given
        User user = createUser();

        when(userRepository.findByEmail("mail"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("wrong", "encoded"))
                .thenReturn(false);

        // when + then
        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.findAndCheckCredentials("mail", "wrong")
        );

        verify(userRepository).findByEmail("mail");
        verify(passwordEncoder).matches("wrong", "encoded");

        verifyNoMoreInteractions(userRepository,  passwordEncoder);
    }

    // ===== UPDATE =====

    @Test
    void update_success() {
        // given
        Long id = 1L;
        User user = createUser();

        UpdateUserRequest request = new UpdateUserRequest("New", "User");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        when(userMapper.toResponse(user))
                .thenAnswer(inv -> {
                    User u = inv.getArgument(0);
                    return new UserResponse(
                            id,
                            u.getEmail(),
                            u.getFirstName(),
                            u.getLastName(),
                            u.getRoles(),
                            u.getActiveRole()
                    );
                });

        // when
        UserResponse response = userService.update(request, id);

        // then
        assertNotNull(response);

        assertEquals("New", user.getFirstName());
        assertEquals("User", user.getLastName());

        assertEquals("New", response.firstName());
        assertEquals("User", response.lastName());
        assertEquals("mail", response.email());
        assertEquals(Role.PATIENT, response.activeRole());

        // verify
        verify(userRepository).findById(id);
        verify(userRepository).save(user);
        verify(userMapper).toResponse(user);
    }

    @Test
    void update_notFound() {
        // given
        Long id = 1L;
        UpdateUserRequest request = new UpdateUserRequest("A", "B");

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // when + then
        assertThrows(UserNotFoundException.class,
                () -> userService.update(request, id));

        // verify
        verify(userRepository).findById(id);

        verify(userRepository, never()).save(any());
        verifyNoInteractions(userMapper);
    }

    // ===== ROLE =====

    @Test
    void addRole_success() {
        // given
        User user = createUser();

        when(userRepository.save(user))
                .thenReturn(user);

        // when
        User result = userService.addRole(user, Role.DOCTOR);

        // then
        assertEquals(Set.of(Role.PATIENT, Role.DOCTOR), result.getRoles());

        verify(userRepository).save(user);
    }

    @Test
    void addRole_alreadyAssigned() {
        // given
        User user = createUser();

        // when + then
        assertThrows(
                RoleAlreadyAssignedException.class,
                () -> userService.addRole(user, Role.PATIENT)
        );

        verify(userRepository, never()).save(any());
    }

    @Test
    void switchRole_success() {
        // given
        Long id = 1L;

        User user = createPatientDoctorUser(Role.PATIENT);

        when(userRepository.findById(id))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        // when
        User result = userService.switchRole(Role.DOCTOR, id);

        // then
        assertEquals(Role.DOCTOR, result.getActiveRole());

        verify(userRepository).findById(id);
        verify(userRepository).save(user);
    }

    @Test
    void switchRole_roleNotAssigned() {
        // given
        Long id = 1L;

        User user = createUser();

        when(userRepository.findById(id))
                .thenReturn(Optional.of(user));

        // when + then
        assertThrows(
                RoleNotAssignedException.class,
                () -> userService.switchRole(Role.DOCTOR, id)
        );

        verify(userRepository).findById(id);

        verify(userRepository, never()).save(any());
    }

    @Test
    void removeRole_success() {
        // given
        User user = createPatientDoctorUser(Role.DOCTOR);

        when(userRepository.save(user))
                .thenReturn(user);

        // when
        userService.removeRole(user, Role.DOCTOR);

        // then
        assertFalse(user.getRoles().contains(Role.DOCTOR));
        assertEquals(Role.PATIENT, user.getActiveRole());

        verify(userRepository).save(user);
    }

    @Test
    void removeRole_lastAdmin() {
        // given
        User user = createAdminUser();

        when(userRepository.countByRolesContains(Role.ADMIN))
                .thenReturn(1L);

        // when + then
        assertThrows(
                CannotRemoveLastAdminException.class,
                () -> userService.removeRole(user, Role.ADMIN)
        );

        verify(userRepository).countByRolesContains(Role.ADMIN);

        verify(userRepository, never()).save(any());

        verifyNoMoreInteractions(userRepository);
    }

    // ===== DELETE =====

    @Test
    void deleteById_success() {
        // given
        Long issuerId = 1L;
        Long targetId = 2L;

        User user = createUser();

        when(userRepository.findById(targetId))
                .thenReturn(Optional.of(user));

        // when
        userService.deleteById(issuerId, targetId);

        // then
        verify(userRepository).findById(targetId);
        verify(userRepository).delete(user);

        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteById_selfDelete() {
        // given
        Long id = 1L;

        // when + then
        assertThrows(SelfDeleteException.class,
                () -> userService.deleteById(id, id));

        // verify
        verifyNoInteractions(userRepository);
    }

    @Test
    void deleteById_notFound() {
        // given
        Long issuerId = 1L;
        Long targetId = 2L;

        when(userRepository.findById(targetId))
                .thenReturn(Optional.empty());

        // when + then
        assertThrows(UserNotFoundException.class,
                () -> userService.deleteById(issuerId, targetId));

        // verify
        verify(userRepository).findById(targetId);

        verify(userRepository, never()).delete(any());

        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteCurrent_success() {
        // given
        User user = createUser();

        // when
        userService.deleteCurrent(user);

        // then
        verify(userRepository).delete(user);

        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteCurrent_lastAdmin() {
        // given
        User user = createAdminUser();

        when(userRepository.countByRolesContains(Role.ADMIN))
                .thenReturn(1L);

        // when + then
        assertThrows(
                CannotRemoveLastAdminException.class,
                () -> userService.deleteCurrent(user)
        );

        verify(userRepository).countByRolesContains(Role.ADMIN);

        verify(userRepository, never()).delete(any());

        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteById_lastAdmin() {
        // given
        Long issuerId = 1L;
        Long targetId = 2L;

        User admin = createAdminUser();

        when(userRepository.findById(targetId))
                .thenReturn(Optional.of(admin));

        when(userRepository.countByRolesContains(Role.ADMIN))
                .thenReturn(1L);

        // when + then
        assertThrows(
                CannotRemoveLastAdminException.class,
                () -> userService.deleteById(issuerId, targetId)
        );

        verify(userRepository).findById(targetId);
        verify(userRepository).countByRolesContains(Role.ADMIN);

        verify(userRepository, never()).delete(any());

        verifyNoMoreInteractions(userRepository);
    }

    // ===== CHANGE PASSWORD =====

    @Test
    void changePassword_success() {
        // given
        User user = createUser();

        ChangePasswordRequest request =
                new ChangePasswordRequest("old", "new");

        when(passwordEncoder.matches("old", "encoded"))
                .thenReturn(true);

        when(passwordEncoder.matches("new", "encoded"))
                .thenReturn(false);

        when(passwordEncoder.encode("new"))
                .thenReturn("newEncoded");

        // when
        userService.updateCurrentPassword(user, request);

        // then
        assertEquals("newEncoded", user.getPassword());

        // verify
        verify(passwordEncoder).matches("old", "encoded");
        verify(passwordEncoder).matches("new", "encoded");
        verify(passwordEncoder).encode("new");

        verify(userRepository).save(user);

        verifyNoMoreInteractions(userRepository, passwordEncoder);
    }

    @Test
    void changePassword_invalidPassword() {
        // given
        User user = createUser();

        ChangePasswordRequest request =
                new ChangePasswordRequest("wrong", "new");

        when(passwordEncoder.matches("wrong", "encoded"))
                .thenReturn(false);

        // when + then
        assertThrows(InvalidPasswordException.class,
                () -> userService.updateCurrentPassword(user, request));

        // verify
        verify(passwordEncoder).matches("wrong", "encoded");

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());

        verifyNoMoreInteractions(userRepository, passwordEncoder);
    }

    @Test
    void changePassword_samePassword() {
        // given
        User user = createUser();

        ChangePasswordRequest request =
                new ChangePasswordRequest("same", "same");

        when(passwordEncoder.matches("same", "encoded"))
                .thenReturn(true, true);

        // when + then
        assertThrows(SamePasswordException.class,
                () -> userService.updateCurrentPassword(user, request));

        // verify
        verify(passwordEncoder, times(2)).matches("same", "encoded");

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());

        verifyNoMoreInteractions(userRepository, passwordEncoder);
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

    private User createAdminUser() {
        return User.builder()
                .email("mail")
                .password("encoded")
                .firstName("John")
                .lastName("Doe")
                .roles(Set.of(Role.ADMIN))
                .activeRole(Role.ADMIN)
                .build();
    }

    private User createPatientDoctorUser(Role activeRole) {
        return User.builder()
                .email("mail")
                .password("encoded")
                .firstName("John")
                .lastName("Doe")
                .roles(Set.of(Role.PATIENT, Role.DOCTOR))
                .activeRole(activeRole)
                .build();
    }

    private CreateUser createPatientCreateUser() {
        return new CreateUser(
                "mail",
                "password",
                Set.of(Role.PATIENT),
                Role.PATIENT,
                "John",
                "Doe"
        );
    }
}
