package com.medical.appointments.bootstrap;

import com.medical.appointments.logger.LoggerMessages;
import com.medical.appointments.user.Role;
import com.medical.appointments.user.UserService;
import com.medical.appointments.user.dto.CreateUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {
    private final UserService userService;

    @Value("${app.bootstrap.admin.email}")
    private String email;

    @Value("${app.bootstrap.admin.password}")
    private String password;

    @Value("${app.bootstrap.admin.first-name}")
    private String firstName;

    @Value("${app.bootstrap.admin.last-name}")
    private String lastName;

    @Override
    public void run(String... args) {
        if (userService.existsByRole(Role.ADMIN)) {
            log.info(LoggerMessages.ADMIN_ALREADY_EXISTS);
            return;
        }

        userService.create(
                new CreateUser(
                    email,
                    password,
                    Set.of(Role.ADMIN),
                    Role.ADMIN,
                    firstName,
                    lastName
                )
        );

        log.info(LoggerMessages.ADMIN_CREATED, email);
    }
}
