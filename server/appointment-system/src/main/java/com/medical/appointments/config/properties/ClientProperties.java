package com.medical.appointments.config.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "client")
public record ClientProperties(
        @NotBlank
        String baseUrl
) {}
