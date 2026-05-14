package com.medical.appointments.references.allergy.dto;

import com.medical.appointments.references.reference.dto.ReferenceResponse;

public record AllergyResponse(
        Long id,
        String name
) implements ReferenceResponse {}
