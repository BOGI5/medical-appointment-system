package com.medical.appointments.references.specialization.dto;

import com.medical.appointments.references.reference.dto.ReferenceResponse;

public record SpecializationResponse(
        Long id,
        String name
) implements ReferenceResponse {}
