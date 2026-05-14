package com.medical.appointments.references.specialization.dto;

import com.medical.appointments.references.reference.dto.CreateReference;
import jakarta.validation.constraints.NotBlank;

public record CreateSpecialization(
        @NotBlank
        String name
) implements CreateReference {}
