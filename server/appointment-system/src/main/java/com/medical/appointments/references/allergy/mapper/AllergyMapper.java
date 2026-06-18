package com.medical.appointments.references.allergy.mapper;

import com.medical.appointments.references.allergy.Allergy;
import com.medical.appointments.references.allergy.dto.AllergyResponse;
import com.medical.appointments.references.allergy.dto.CreateAllergyRequest;
import com.medical.appointments.references.reference.mapper.ReferenceMapper;
import org.springframework.stereotype.Component;

@Component
public class AllergyMapper implements ReferenceMapper<Allergy, AllergyResponse, CreateAllergyRequest> {
    @Override
    public Allergy toEntity(CreateAllergyRequest createReference) {
        return new Allergy(createReference.name());
    }

    @Override
    public AllergyResponse toResponse(Allergy reference) {
        return new AllergyResponse(
                reference.getId(),
                reference.getName()
        );
    }
}
