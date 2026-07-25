package com.medical.appointments.config.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "access.token")
public record AccessTokenProperties(
        @NotNull
        @Min(1)
        Long expiration
) {}
