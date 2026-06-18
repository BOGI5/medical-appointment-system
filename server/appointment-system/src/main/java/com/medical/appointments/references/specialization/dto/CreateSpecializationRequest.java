package com.medical.appointments.references.specialization.dto;

import com.medical.appointments.references.reference.dto.CreateReferenceRequest;
import jakarta.validation.constraints.NotBlank;

public record CreateSpecializationRequest(
        @NotBlank
        String name
) implements CreateReferenceRequest {}
