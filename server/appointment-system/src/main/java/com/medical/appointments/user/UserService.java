package com.medical.appointments.user;

import com.medical.appointments.user.dto.CreateUser;
import com.medical.appointments.user.exception.UserAlreadyExistsException;
import com.medical.appointments.user.exception.UserNotFoundException;
import com.medical.appointments.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

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

    public User createUser(CreateUser createUser) {
        if (userRepository.existsByEmail(createUser.email())) {
            throw new UserAlreadyExistsException();
        }
        return userRepository.save(userMapper.toEntity(createUser));
    }
}
