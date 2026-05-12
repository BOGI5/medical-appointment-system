package com.medical.appointments.profiles.profile;

import com.medical.appointments.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface ProfileRepository<T extends Profile> extends JpaRepository<T, Long> {
    boolean existsByUser(User user);

    Optional<T> findByUser(User user);
}
