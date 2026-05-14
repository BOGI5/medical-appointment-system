package com.medical.appointments.references.allergy;

import com.medical.appointments.references.allergy.dto.AllergyResponse;
import com.medical.appointments.references.allergy.dto.CreateAllergy;
import com.medical.appointments.references.allergy.mapper.AllergyMapper;
import com.medical.appointments.references.reference.ReferenceService;
import org.springframework.stereotype.Service;

@Service
public class AllergyService extends ReferenceService<
        Allergy,
        AllergyResponse,
        CreateAllergy,
        AllergyRepository,
        AllergyMapper
        > {
    public AllergyService(AllergyRepository repository,  AllergyMapper mapper) {
        super(repository, mapper);
    }
}
