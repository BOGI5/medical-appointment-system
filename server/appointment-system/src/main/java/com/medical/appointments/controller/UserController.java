package com.medical.appointments.controller;

import com.medical.appointments.security.CurrentUserProvider;
import com.medical.appointments.security.annotation.role.IsAdmin;
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
        return userMapper.toResponse(currentUserProvider.getCurrent());
    }

    @PatchMapping(ApiPaths.CURRENT)
    public UserResponse updateCurrent(@Valid @RequestBody UpdateUserRequest updateUserRequest) {
        User user = currentUserProvider.getCurrent();
        return userService.update(updateUserRequest, user.getId());
    }

    @IsAdmin
    @PatchMapping(ApiPaths.BY_ID)
    public UserResponse updateById(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        return userService.update(updateUserRequest, id);
    }

    @PatchMapping(ApiPaths.CURRENT_PASSWORD)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCurrentPassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        userService.updateCurrentPassword(currentUserProvider.getCurrent(), changePasswordRequest);
    }

    @DeleteMapping(ApiPaths.CURRENT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCurrent() {
        User user = currentUserProvider.getCurrent();
        userService.deleteCurrent(user);
    }

    @IsAdmin
    @DeleteMapping(ApiPaths.BY_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        User issuer = currentUserProvider.getCurrent();
        userService.deleteById(issuer.getId(), id);
    }
}
