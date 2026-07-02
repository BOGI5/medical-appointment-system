package com.medical.appointments.references.reference;

import com.medical.appointments.exception.ReferenceAlreadyExistsException;
import com.medical.appointments.exception.ReferenceNotFoundException;
import com.medical.appointments.references.reference.dto.CreateReferenceRequest;
import com.medical.appointments.references.reference.dto.ReferenceResponse;
import com.medical.appointments.references.reference.mapper.ReferenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public abstract class ReferenceService<
        E extends Reference,
        R extends ReferenceResponse,
        C extends CreateReferenceRequest,
        Repository extends ReferenceRepository<E>,
        Mapper extends ReferenceMapper<E, R, C>
        >
{
    protected final Repository repository;
    protected final Mapper mapper;

    public R create(C createReference) {
        if (repository.existsByName(createReference.name())) {
            throw new ReferenceAlreadyExistsException();
        }

        return mapper.toResponse(repository.save(mapper.toEntity(createReference)));
    }

    public Page<R> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    public R findById(Long id) {
        return mapper.toResponse(findEntityById(id));
    }

    public E findEntityById(Long id) {
        return repository.findById(id).orElseThrow(ReferenceNotFoundException::new);
    }

    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new ReferenceNotFoundException();
        }

        repository.deleteById(id);
    }
}
