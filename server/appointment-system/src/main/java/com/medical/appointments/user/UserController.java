package com.medical.appointments.user;

import com.medical.appointments.security.CurrentUserProvider;
import com.medical.appointments.user.dto.UserResponse;
import com.medical.appointments.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserMapper userMapper;
    private final UserService userService;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping("/me")
    public UserResponse getCurrentUser() {
        User user = currentUserProvider.getCurrentUser();
        return userMapper.toResponse(user);
    }

    @DeleteMapping("/me")
    public void deleteCurrentUser() {
        User user = currentUserProvider.getCurrentUser();
        userService.deleteCurrentUser(user);
    }

    // access for ADMIN role only
    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Long id) {
        User issuer = currentUserProvider.getCurrentUser();
        userService.deleteUserById(issuer.getId(), id);
    }
}
