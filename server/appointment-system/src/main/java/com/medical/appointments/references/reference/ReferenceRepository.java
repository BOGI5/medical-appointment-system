package com.medical.appointments.references.reference;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ReferenceRepository<E extends Reference> extends JpaRepository<E, Long> {
    boolean existsByName(String name);
}
