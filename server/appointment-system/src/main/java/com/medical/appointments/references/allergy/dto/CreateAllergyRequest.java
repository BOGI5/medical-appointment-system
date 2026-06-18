package com.medical.appointments.references.allergy.dto;

import com.medical.appointments.references.reference.dto.CreateReferenceRequest;
import jakarta.validation.constraints.NotBlank;

public record CreateAllergyRequest(
        @NotBlank
        String name
) implements CreateReferenceRequest {}
