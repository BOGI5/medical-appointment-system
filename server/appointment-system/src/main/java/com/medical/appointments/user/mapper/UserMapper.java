package com.medical.appointments.user.mapper;

import com.medical.appointments.user.User;
import com.medical.appointments.user.dto.CreateUser;
import com.medical.appointments.user.dto.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toResponse(User user) {
        return new UserResponse(user.getId(),  user.getEmail(), user.getFirstName(), user.getLastName());
    }

    public User toEntity(CreateUser createUser) {
        return User.builder()
                .email(createUser.email())
                .password(createUser.hashedPassword())
                .roles(createUser.roles())
                .firstName(createUser.firstName())
                .lastName(createUser.lastName())
                .build();
    }
}
