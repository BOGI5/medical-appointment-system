package com.medical.appointments.user;

import com.medical.appointments.user.dto.ChangePasswordRequest;
import com.medical.appointments.user.dto.UpdateUserRequest;
import com.medical.appointments.user.dto.UserResponse;
import com.medical.appointments.user.exception.*;
import com.medical.appointments.user.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UserService userService;

    // ===== UPDATE =====

    @Test
    void updateUser_success() {
        // given
        Long id = 1L;
        User user = createUser();

        UpdateUserRequest request = new UpdateUserRequest("New", "User");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        when(userMapper.toResponse(any()))
                .thenAnswer(inv -> {
                    User u = inv.getArgument(0);
                    return new UserResponse(
                            id,
                            u.getEmail(),
                            u.getFirstName(),
                            u.getLastName()
                    );
                });

        // when
        UserResponse response = userService.updateUser(request, id);

        // then
        assertNotNull(response);

        assertEquals("New", user.getFirstName());
        assertEquals("User", user.getLastName());

        assertEquals("New", response.firstName());
        assertEquals("User", response.lastName());
        assertEquals("mail", response.email());

        // verify
        verify(userRepository).findById(id);
        verify(userRepository).save(user);
        verify(userMapper).toResponse(user);
    }

    @Test
    void updateUser_notFound() {
        // given
        Long id = 1L;
        UpdateUserRequest request = new UpdateUserRequest("A", "B");

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // when + then
        assertThrows(UserNotFoundException.class,
                () -> userService.updateUser(request, id));

        // verify
        verify(userRepository).findById(id);

        verify(userRepository, never()).save(any());
        verifyNoInteractions(userMapper);
    }

    // ===== DELETE =====

    @Test
    void deleteUser_success() {
        // given
        Long issuerId = 1L;
        Long targetId = 2L;

        User user = createUser();

        when(userRepository.findById(targetId))
                .thenReturn(Optional.of(user));

        // when
        userService.deleteUserById(issuerId, targetId);

        // then
        verify(userRepository).findById(targetId);
        verify(userRepository).delete(user);

        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteUser_selfDelete() {
        // given
        Long id = 1L;

        // when + then
        assertThrows(SelfDeleteException.class,
                () -> userService.deleteUserById(id, id));

        // verify
        verifyNoInteractions(userRepository);
    }

    @Test
    void deleteUser_notFound() {
        // given
        Long issuerId = 1L;
        Long targetId = 2L;

        when(userRepository.findById(targetId))
                .thenReturn(Optional.empty());

        // when + then
        assertThrows(UserNotFoundException.class,
                () -> userService.deleteUserById(issuerId, targetId));

        // verify
        verify(userRepository).findById(targetId);

        verify(userRepository, never()).delete(any());
    }

    @Test
    void deleteCurrentUser_success() {
        // given
        User user = createUser();

        // when
        userService.deleteCurrentUser(user);

        // then
        verify(userRepository).delete(user);

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
        userService.updateCurrentUserPassword(user, request);

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
                () -> userService.updateCurrentUserPassword(user, request));

        // verify
        verify(passwordEncoder).matches("wrong", "encoded");

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void changePassword_samePassword() {
        // given
        User user = createUser();

        ChangePasswordRequest request =
                new ChangePasswordRequest("same", "same");

        when(passwordEncoder.matches("same", "encoded"))
                .thenReturn(true); // oldPassword OK

        when(passwordEncoder.matches("same", "encoded"))
                .thenReturn(true); // newPassword совпадает

        // when + then
        assertThrows(SamePasswordException.class,
                () -> userService.updateCurrentUserPassword(user, request));

        // verify
        verify(passwordEncoder, times(2)).matches("same", "encoded");

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    // ===== helper =====

    private User createUser() {
        return new User("mail", "encoded", "John", "Doe");
    }
}