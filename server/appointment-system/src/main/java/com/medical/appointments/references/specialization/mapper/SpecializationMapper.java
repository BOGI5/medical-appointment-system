package com.medical.appointments.references.specialization.mapper;

import com.medical.appointments.references.reference.mapper.ReferenceMapper;
import com.medical.appointments.references.specialization.Specialization;
import com.medical.appointments.references.specialization.dto.CreateSpecialization;
import com.medical.appointments.references.specialization.dto.SpecializationResponse;
import org.springframework.stereotype.Component;

@Component
public class SpecializationMapper implements ReferenceMapper<
        Specialization, SpecializationResponse, CreateSpecialization> {
    @Override
    public Specialization toEntity(CreateSpecialization createReference) {
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
