package com.medical.appointments.user;

import com.medical.appointments.exception.*;
import com.medical.appointments.user.dto.CreateUser;
import com.medical.appointments.user.dto.ChangePasswordRequest;
import com.medical.appointments.user.dto.UpdateUserRequest;
import com.medical.appointments.user.dto.UserResponse;
import com.medical.appointments.user.mapper.UserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }

    public Optional<User> findOptionalByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User create(@Valid CreateUser createUser) {
        if (userRepository.existsByEmail(createUser.email())) {
            throw new UserAlreadyExistsException();
        }

        if (createUser.roles() == null || createUser.roles().isEmpty()) {
            throw new UserMustHaveAtLeastOneRoleException();
        }

        return userRepository.save(userMapper.toEntity(createUser));
    }

    public UserResponse update(UpdateUserRequest updateUserRequest, Long id) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        String firstName = updateUserRequest.firstName();
        if (firstName != null && !firstName.isBlank()) {
            user.setFirstName(firstName);
        }

        String lastName = updateUserRequest.lastName();
        if (lastName != null && !lastName.isBlank()) {
            user.setLastName(lastName);
        }

        return userMapper.toResponse(userRepository.save(user));
    }

    public void addRole(User user, Role role) {
        if (!userRepository.existsById(user.getId())) {
            throw new UserNotFoundException();
        }

        user.addRole(role);
        userRepository.save(user);
    }

    public void removeRole(User user, Role role) {
        if (!userRepository.existsById(user.getId())) {
            throw new UserNotFoundException();
        }

        user.removeRole(role);
        userRepository.save(user);
    }

    public void updateCurrentPassword(User user, ChangePasswordRequest changePasswordRequest) {
        if (!passwordEncoder.matches(
                changePasswordRequest.oldPassword(),
                user.getPassword()
        )) {
            throw new InvalidPasswordException();
        }

        if (passwordEncoder.matches(
                changePasswordRequest.newPassword(),
                user.getPassword()
        )) {
            throw new SamePasswordException();
        }

        user.setPassword(
                passwordEncoder.encode(changePasswordRequest.newPassword())
        );

        userRepository.save(user);
    }

    public void deleteCurrent(User user) {
        userRepository.delete(user);
    }

    public void deleteById(Long issuerId, Long id) {
        if (issuerId.equals(id)) {
            throw new SelfDeleteException();
        }

        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        userRepository.delete(user);
    }
}
