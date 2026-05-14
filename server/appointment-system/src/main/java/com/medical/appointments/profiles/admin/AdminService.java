package com.medical.appointments.profiles.admin;

import com.medical.appointments.profiles.admin.dto.CreateAdminRequest;
import com.medical.appointments.user.Role;
import com.medical.appointments.user.User;
import com.medical.appointments.user.UserService;
import com.medical.appointments.user.dto.CreateUser;
import com.medical.appointments.user.dto.UserResponse;
import com.medical.appointments.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserService userService;
    private final UserMapper userMapper;

    private User findUserById(Long id) {
        return userService.findById(id);
    }

    public UserResponse create(CreateAdminRequest createAdminRequest) {
        return userMapper.toResponse(
                userService.create(
                        new CreateUser(
                                createAdminRequest.email(),
                                createAdminRequest.password(),
                                Set.of(Role.ADMIN),
                                Role.ADMIN,
                                createAdminRequest.firstName(),
                                createAdminRequest.lastName()
                        )
                )
        );
    }

    public UserResponse promoteById(Long id) {
        return userMapper.toResponse(userService.addRole(findUserById(id), Role.ADMIN));
    }

    public void removeAdminRole(Long id) {
        userService.removeRole(findUserById(id), Role.ADMIN);
    }
}
