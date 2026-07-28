package com.medical.appointments.controller;

import com.medical.appointments.references.specialization.SpecializationService;
import com.medical.appointments.references.specialization.dto.CreateSpecializationRequest;
import com.medical.appointments.references.specialization.dto.SpecializationResponse;
import com.medical.appointments.security.annotation.role.IsAdmin;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.SPECIALIZATIONS)
@RequiredArgsConstructor
public class SpecializationController {
    private final SpecializationService service;

    @IsAdmin
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SpecializationResponse create(@RequestBody @Valid CreateSpecializationRequest request) {
        return service.create(request);
    }

    @GetMapping
    public Page<SpecializationResponse> getAll(@ParameterObject Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping(ApiPaths.BY_ID)
    public SpecializationResponse getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @IsAdmin
    @DeleteMapping(ApiPaths.BY_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}
