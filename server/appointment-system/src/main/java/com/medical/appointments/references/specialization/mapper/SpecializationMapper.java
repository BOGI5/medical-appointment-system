package com.medical.appointments.references.specialization.mapper;

import com.medical.appointments.references.reference.mapper.ReferenceMapper;
import com.medical.appointments.references.specialization.Specialization;
import com.medical.appointments.references.specialization.dto.CreateSpecializationRequest;
import com.medical.appointments.references.specialization.dto.SpecializationResponse;
import org.springframework.stereotype.Component;

@Component
public class SpecializationMapper implements ReferenceMapper<
        Specialization, SpecializationResponse, CreateSpecializationRequest> {
    @Override
    public Specialization toEntity(CreateSpecializationRequest createReference) {
        return new Specialization(createReference.name());
    }

    @Override
    public SpecializationResponse toResponse(Specialization reference) {
        return new SpecializationResponse(
                reference.getId(),
                reference.getName()
        );
    }
}
