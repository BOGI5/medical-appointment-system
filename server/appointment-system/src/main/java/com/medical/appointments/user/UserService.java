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
    private final UserMapper mapper;
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    private boolean passwordMatches(String password, String hashedPassword) {
        return passwordEncoder.matches(password, hashedPassword);
    }

    private String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public User findByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }

    public User findById(Long id) {
        return repository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public Optional<User> findOptionalByEmail(String email) {
        return repository.findByEmail(email);
    }

    public User findAndCheckCredentials(String email, String password) {
        User user = repository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordMatches(password, user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return user;
    }

    public boolean existsByRole(Role role) {
        return repository.existsByRolesContains(role);
    }

    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    public User create(@Valid CreateUser createUser) {
        if (repository.existsByEmail(createUser.email())) {
            throw new UserAlreadyExistsException();
        }

        return repository.save(mapper.toEntity(createUser, hashPassword(createUser.password())));
    }

    public User switchRole(Role role, Long id) {
        User user = findById(id);
        user.setActiveRole(role);
        return repository.save(user);
    }

    public UserResponse update(UpdateUserRequest request, Long id) {
        User user = findById(id);

        String firstName = request.firstName();
        if (firstName != null && !firstName.isBlank()) {
            user.setFirstName(firstName);
        }

        String lastName = request.lastName();
        if (lastName != null && !lastName.isBlank()) {
            user.setLastName(lastName);
        }

        return mapper.toResponse(repository.save(user));
    }

    public User addRole(User user, Role role) {
        user.addRole(role);
        return repository.save(user);
    }

    public void removeRole(User user, Role role) {
        if (
                role == Role.ADMIN
                && user.getRoles().contains(Role.ADMIN)
                && repository.countByRolesContains(Role.ADMIN) == 1
        ) {
                throw new CannotRemoveLastAdminException();
        }

        user.removeRole(role);
        repository.save(user);
    }

    public void updateCurrentPassword(User user, ChangePasswordRequest request) {
        if (!passwordMatches(
                request.oldPassword(),
                user.getPassword()
        )) {
            throw new InvalidPasswordException();
        }

        if (passwordMatches(
                request.newPassword(),
                user.getPassword()
        )) {
            throw new SamePasswordException();
        }

        user.setPassword(hashPassword(request.newPassword()));

        repository.save(user);
    }

    public void deleteCurrent(User user) {
        if (user.getRoles().contains(Role.ADMIN) && repository.countByRolesContains(Role.ADMIN) == 1) {
                throw new CannotRemoveLastAdminException();
        }

        repository.delete(user);
    }

    public void deleteById(Long issuerId, Long id) {
        if (issuerId.equals(id)) {
            throw new SelfDeleteException();
        }

        User user = findById(id);

        if (user.getRoles().contains(Role.ADMIN) && repository.countByRolesContains(Role.ADMIN) == 1) {
            throw new CannotRemoveLastAdminException();
        }

        repository.delete(user);
    }
}
