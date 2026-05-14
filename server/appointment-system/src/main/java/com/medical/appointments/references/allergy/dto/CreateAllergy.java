package com.medical.appointments.references.allergy.dto;

import com.medical.appointments.references.reference.dto.CreateReference;
import jakarta.validation.constraints.NotBlank;

public record CreateAllergy(
        @NotBlank
        String name
) implements CreateReference {}
