package com.medical.appointments.security;

import com.medical.appointments.user.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CurrentUserProvider {

    public User getCurrentUser() {
        return (User)  Objects.requireNonNull(SecurityContextHolder
                        .getContext()
                        .getAuthentication())
                .getPrincipal();
    }
}
