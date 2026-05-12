package com.medical.appointments.profiles.profile;

import com.medical.appointments.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Profile {
    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    protected Profile(User user) {
        this.user = user;
    }
}
