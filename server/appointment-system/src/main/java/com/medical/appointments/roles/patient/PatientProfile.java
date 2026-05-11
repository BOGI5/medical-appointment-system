package com.medical.appointments.roles.patient;

import com.medical.appointments.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Table(name = "patients")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PatientProfile {
    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Setter
    private String address;

    @Setter
    private String phone;

    @Column(length = 2000)
    private String medicalHistory;

    @Setter
    @Column(length = 2000)
    private String allergies;

    public PatientProfile(User user) {
        this.user = user;
    }
}
