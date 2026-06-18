package com.medical.appointments.references.reference.mapper;

import com.medical.appointments.references.reference.Reference;
import com.medical.appointments.references.reference.dto.CreateReferenceRequest;
import com.medical.appointments.references.reference.dto.ReferenceResponse;

public interface ReferenceMapper<E extends Reference, R extends ReferenceResponse, C extends CreateReferenceRequest> {
    E toEntity(C createReference);
    R toResponse(E reference);
}
