package com.medical.appointments.controller;

import com.medical.appointments.profiles.admin.AdminService;
import com.medical.appointments.user.dto.CreateUserRequest;
import com.medical.appointments.security.CurrentUserProvider;
import com.medical.appointments.security.annotation.role.IsAdmin;
import com.medical.appointments.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.ADMIN)
@RequiredArgsConstructor
@IsAdmin
public class AdminController {
    private final AdminService service;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createNew(@RequestBody @Valid CreateUserRequest request) {
        return service.create(request);
    }

    @PostMapping(ApiPaths.BY_ID)
    public UserResponse promoteExistingById(@PathVariable Long id) {
        return service.promoteById(id);
    }

    @DeleteMapping(ApiPaths.BY_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAdminRoleById(@PathVariable Long id) {
        service.removeAdminRole(id);
    }

    @DeleteMapping(ApiPaths.CURRENT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCurrentUserAdminRole() {
        service.removeAdminRole(currentUserProvider.getCurrent().getId());
    }
}
