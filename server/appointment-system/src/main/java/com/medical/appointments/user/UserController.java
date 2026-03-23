package com.medical.appointments.user;

import com.medical.appointments.security.CurrentUserProvider;
import com.medical.appointments.user.dto.UserResponse;
import com.medical.appointments.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserMapper userMapper;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping("/me")
    public UserResponse getCurrentUser() {
        User user = currentUserProvider.getCurrentUser();
        return userMapper.toResponse(user);
    }
}
