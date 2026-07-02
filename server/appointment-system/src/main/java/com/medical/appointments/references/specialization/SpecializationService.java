package com.medical.appointments.references.specialization;

import com.medical.appointments.references.reference.ReferenceService;
import com.medical.appointments.references.specialization.dto.CreateSpecializationRequest;
import com.medical.appointments.references.specialization.dto.SpecializationResponse;
import com.medical.appointments.references.specialization.mapper.SpecializationMapper;
import org.springframework.stereotype.Service;

@Service
public class SpecializationService extends ReferenceService<
        Specialization,
        SpecializationResponse,
        CreateSpecializationRequest,
        SpecializationRepository,
        SpecializationMapper
        > {
    public SpecializationService(SpecializationRepository repository, SpecializationMapper mapper) {
        super(repository, mapper);
    }
}
