package com.medical.appointments.controller;

import com.medical.appointments.security.CurrentUserProvider;
import com.medical.appointments.user.User;
import com.medical.appointments.user.UserService;
import com.medical.appointments.user.dto.ChangePasswordRequest;
import com.medical.appointments.user.dto.UpdateUserRequest;
import com.medical.appointments.user.dto.UserResponse;
import com.medical.appointments.user.mapper.UserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.USERS)
@RequiredArgsConstructor
public class UserController {
    private final UserMapper userMapper;
    private final UserService userService;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping(ApiPaths.CURRENT)
    public UserResponse getCurrentUser() {
        return userMapper.toResponse(currentUserProvider.getCurrentUser());
    }

    @PatchMapping(ApiPaths.CURRENT)
    public UserResponse updateCurrentUser(@Valid @RequestBody UpdateUserRequest updateUserRequest) {
        User user = currentUserProvider.getCurrentUser();
        return userService.updateUser(updateUserRequest, user.getId());
    }

    // TODO: restrict to ADMIN role
    @PatchMapping(ApiPaths.BY_ID)
    public UserResponse updateUserById(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        return userService.updateUser(updateUserRequest, id);
    }

    @PatchMapping(ApiPaths.CURRENT_PASSWORD)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCurrentUserPassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        userService.updateCurrentUserPassword(currentUserProvider.getCurrentUser(), changePasswordRequest);
    }

    @DeleteMapping(ApiPaths.CURRENT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCurrentUser() {
        User user = currentUserProvider.getCurrentUser();
        userService.deleteCurrentUser(user);
    }

    // TODO: restrict to ADMIN role
    @DeleteMapping(ApiPaths.BY_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable Long id) {
        User issuer = currentUserProvider.getCurrentUser();
        userService.deleteUserById(issuer.getId(), id);
    }
}
