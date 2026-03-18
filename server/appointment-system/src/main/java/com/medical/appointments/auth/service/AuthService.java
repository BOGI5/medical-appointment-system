package com.medical.appointments.auth.service;

import com.medical.appointments.auth.exception.InvalidCredentialsException;
import com.medical.appointments.user.dto.CreateUser;
import com.medical.appointments.user.dto.LoginUser;
import com.medical.appointments.user.dto.UserResponse;
import com.medical.appointments.user.entity.User;
import com.medical.appointments.auth.exception.UserAlreadyExistsException;
import com.medical.appointments.user.exception.UserNotFoundException;
import com.medical.appointments.user.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean matchesPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public UserResponse login(LoginUser loginUser) {
        User user;
        try {
            user = userService.findByEmail(loginUser.email());
        } catch (UserNotFoundException e) {
            throw new InvalidCredentialsException();
        }

        if (!matchesPassword(loginUser.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        return new UserResponse(user.getId(), user.getEmail(),  user.getFirstName(), user.getLastName());
    }

    public UserResponse registerUser(CreateUser createUser) {
        if (userService.existsByEmail(createUser.email())) {
            throw new UserAlreadyExistsException();
        }

        return userService.registerUser(new CreateUser(createUser.email(), hashPassword(createUser.password())
                , createUser.firstName(), createUser.lastName()));
    }
}
