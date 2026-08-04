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
    private final UserMapper mapper;
    private final UserService service;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping(ApiPaths.CURRENT)
    public UserResponse getCurrentUser() {
        return mapper.toResponse(currentUserProvider.getCurrent());
    }

    @PatchMapping(ApiPaths.CURRENT)
    public UserResponse updateCurrent(@Valid @RequestBody UpdateUserRequest request) {
        User user = currentUserProvider.getCurrent();
        return service.update(request, user.getId());
    }

    @IsAdmin
    @PatchMapping(ApiPaths.BY_ID)
    public UserResponse updateById(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return service.update(request, id);
    }

    @PatchMapping(ApiPaths.CURRENT_PASSWORD)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCurrentPassword(@Valid @RequestBody ChangePasswordRequest request) {
        service.updateCurrentPassword(currentUserProvider.getCurrent(), request);
    }

    @DeleteMapping(ApiPaths.CURRENT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCurrent() {
        User user = currentUserProvider.getCurrent();
        service.deleteCurrent(user);
    }

    @IsAdmin
    @DeleteMapping(ApiPaths.BY_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        User issuer = currentUserProvider.getCurrent();
        service.deleteById(issuer.getId(), id);
    }
}
