package com.medical.appointments.user.service;

import com.medical.appointments.user.dto.CreateUser;
import com.medical.appointments.user.dto.UserResponse;
import com.medical.appointments.user.entity.User;
import com.medical.appointments.user.exception.UserNotFoundException;
import com.medical.appointments.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public UserResponse registerUser(CreateUser createUser) {

        User user = User.builder()
                .email(createUser.email())
                .password(createUser.password())
                .firstName(createUser.firstName())
                .lastName(createUser.lastName())
                .build();

        userRepository.save(user);
        return new UserResponse(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName());
    }
}
