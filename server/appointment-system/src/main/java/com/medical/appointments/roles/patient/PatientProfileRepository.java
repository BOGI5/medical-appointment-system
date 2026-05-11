package com.medical.appointments.roles.patient;

import com.medical.appointments.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientProfileRepository extends JpaRepository<PatientProfile, Long> {
    boolean existsByUser(User user);

    Optional<PatientProfile> findByUser(User user);
}
