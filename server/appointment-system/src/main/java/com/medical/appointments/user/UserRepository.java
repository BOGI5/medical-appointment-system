package com.medical.appointments.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByRolesContains(Role role);
    long countByRolesContains(Role role);
    Optional<User> findByEmail(String email);
}
