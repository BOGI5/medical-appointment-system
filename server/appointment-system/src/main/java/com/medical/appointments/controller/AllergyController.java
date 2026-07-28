package com.medical.appointments.controller;

import com.medical.appointments.references.allergy.AllergyService;
import com.medical.appointments.references.allergy.dto.AllergyResponse;
import com.medical.appointments.references.allergy.dto.CreateAllergyRequest;
import com.medical.appointments.security.annotation.role.IsAdmin;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.ALLERGIES)
@RequiredArgsConstructor
public class AllergyController {
    private final AllergyService service;

    @IsAdmin
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AllergyResponse create(@RequestBody @Valid CreateAllergyRequest request) {
        return service.create(request);
    }

    @GetMapping
    public Page<AllergyResponse> getAll(@ParameterObject Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping(ApiPaths.BY_ID)
    public AllergyResponse getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @IsAdmin
    @DeleteMapping(ApiPaths.BY_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}
