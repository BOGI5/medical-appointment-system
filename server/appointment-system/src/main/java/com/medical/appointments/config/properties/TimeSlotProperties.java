package com.medical.appointments.config.properties;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "time.slot")
public record TimeSlotProperties (
    @Min(1)
    @Max(60)
    @NotNull
    Integer duration
) {}
