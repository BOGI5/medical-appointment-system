package com.medical.appointments.profiles.admin;

import com.medical.appointments.exception.CannotRemoveLastAdminException;
import com.medical.appointments.exception.RoleAlreadyAssignedException;
import com.medical.appointments.exception.UserAlreadyExistsException;
import com.medical.appointments.exception.UserNotFoundException;
import com.medical.appointments.profiles.admin.dto.CreateAdminRequest;
import com.medical.appointments.user.Role;
import com.medical.appointments.user.User;
import com.medical.appointments.user.UserService;
import com.medical.appointments.user.dto.UserResponse;
import com.medical.appointments.user.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock private UserService userService;
    @Mock private UserMapper userMapper;

    @InjectMocks
    private AdminService service;

    // ===== CREATE =====

    @Test
    void create_success() {
        // given
        CreateAdminRequest request =
                new CreateAdminRequest(
                        "admin@mail.com",
                        "password",
                        "John",
                        "Doe"
                );

        User user = createAdminUser();

        UserResponse response = createResponse(user);

        when(userService.create(any()))
                .thenReturn(user);

        when(userMapper.toResponse(user))
                .thenReturn(response);

        // when
        UserResponse result = service.create(request);

        // then
        assertEquals(response, result);

        verify(userService).create(
                argThat(createUser ->
                        createUser.email().equals("admin@mail.com")
                                && createUser.password().equals("password")
                                && createUser.roles().equals(Set.of(Role.ADMIN))
                                && createUser.activeRole().equals(Role.ADMIN)
                                && createUser.firstName().equals("John")
                                && createUser.lastName().equals("Doe")
                )
        );

        verify(userMapper).toResponse(user);
    }

    @Test
    void create_userAlreadyExists() {
        // given
        CreateAdminRequest request =
                new CreateAdminRequest(
                        "admin@mail.com",
                        "password",
                        "John",
                        "Doe"
                );

        when(userService.create(any()))
                .thenThrow(new UserAlreadyExistsException());

        // when + then
        assertThrows(
                UserAlreadyExistsException.class,
                () -> service.create(request)
        );

        verify(userService).create(any());

        verifyNoInteractions(userMapper);
    }

    // ===== PROMOTE =====

    @Test
    void promoteById_success() {
        // given
        Long id = 1L;

        User user = createPatientUser();

        User promoted = createAdminUser();

        UserResponse response = createResponse(promoted);

        when(userService.findById(id))
                .thenReturn(user);

        when(userService.addRole(user, Role.ADMIN))
                .thenReturn(promoted);

        when(userMapper.toResponse(promoted))
                .thenReturn(response);

        // when
        UserResponse result = service.promoteById(id);

        // then
        assertEquals(response, result);

        verify(userService).findById(id);
        verify(userService).addRole(user, Role.ADMIN);
        verify(userMapper).toResponse(promoted);
    }

    @Test
    void promoteById_userNotFound() {
        // given
        Long id = 1L;

        when(userService.findById(id))
                .thenThrow(new UserNotFoundException());

        // when + then
        assertThrows(
                UserNotFoundException.class,
                () -> service.promoteById(id)
        );

        verify(userService).findById(id);

        verify(userService, never())
                .addRole(any(), any());

        verifyNoInteractions(userMapper);
    }

    @Test
    void promoteById_roleAlreadyAssigned() {
        // given
        Long id = 1L;

        User user = createAdminUser();

        when(userService.findById(id))
                .thenReturn(user);

        when(userService.addRole(user, Role.ADMIN))
                .thenThrow(new RoleAlreadyAssignedException());

        // when + then
        assertThrows(
                RoleAlreadyAssignedException.class,
                () -> service.promoteById(id)
        );

        verify(userService).findById(id);
        verify(userService).addRole(user, Role.ADMIN);

        verifyNoInteractions(userMapper);
    }

    // ===== REMOVE ROLE =====

    @Test
    void removeAdminRole_success() {
        // given
        Long id = 1L;

        User user = createAdminUser();

        when(userService.findById(id))
                .thenReturn(user);

        // when
        service.removeAdminRole(id);

        // then
        verify(userService).findById(id);
        verify(userService).removeRole(user, Role.ADMIN);

        verifyNoMoreInteractions(userService);
    }

    @Test
    void removeAdminRole_userNotFound() {
        // given
        Long id = 1L;

        when(userService.findById(id))
                .thenThrow(new UserNotFoundException());

        // when + then
        assertThrows(
                UserNotFoundException.class,
                () -> service.removeAdminRole(id)
        );

        verify(userService).findById(id);

        verify(userService, never())
                .removeRole(any(), any());
    }

    @Test
    void removeAdminRole_lastAdmin() {
        // given
        Long id = 1L;

        User user = createAdminUser();

        when(userService.findById(id))
                .thenReturn(user);

        doThrow(new CannotRemoveLastAdminException())
                .when(userService)
                .removeRole(user, Role.ADMIN);

        // when + then
        assertThrows(
                CannotRemoveLastAdminException.class,
                () -> service.removeAdminRole(id)
        );

        verify(userService).findById(id);
        verify(userService).removeRole(user, Role.ADMIN);
    }

    // ===== helper =====

    private User createAdminUser() {
        return User.builder()
                .email("admin@mail.com")
                .password("encoded")
                .firstName("John")
                .lastName("Doe")
                .roles(Set.of(Role.ADMIN))
                .activeRole(Role.ADMIN)
                .build();
    }

    private User createPatientUser() {
        return User.builder()
                .email("patient@mail.com")
                .password("encoded")
                .firstName("Jane")
                .lastName("Doe")
                .roles(Set.of(Role.PATIENT))
                .activeRole(Role.PATIENT)
                .build();
    }

    private UserResponse createResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles(),
                user.getActiveRole()
        );
    }
}
