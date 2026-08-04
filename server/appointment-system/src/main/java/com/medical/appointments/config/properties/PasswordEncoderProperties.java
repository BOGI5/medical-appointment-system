package com.medical.appointments.config.properties;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "password.encoder")
public record PasswordEncoderProperties(
        @NotNull
        @Min(4)
        @Max(16)
        Integer strength
) {}
