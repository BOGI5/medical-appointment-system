package com.medical.appointments.bootstrap.initialization;

import com.medical.appointments.config.properties.BootstrapAdminProperties;
import com.medical.appointments.logger.LoggerMessages;
import com.medical.appointments.user.Role;
import com.medical.appointments.user.UserService;
import com.medical.appointments.user.dto.CreateUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {
    private final UserService userService;

    private final BootstrapAdminProperties properties;

    @Override
    public void run(ApplicationArguments args) {
        if (userService.existsByRole(Role.ADMIN)) {
            log.info(LoggerMessages.ADMIN_ALREADY_EXISTS);
            return;
        }

        userService.create(
                new CreateUser(
                    properties.email(),
                    properties.password(),
                    Set.of(Role.ADMIN),
                    Role.ADMIN,
                    properties.firstName(),
                    properties.lastName()
                )
        );

        log.info(LoggerMessages.ADMIN_CREATED, properties.email());
    }
}
