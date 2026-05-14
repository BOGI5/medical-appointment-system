package com.medical.appointments.profiles.patient;

import com.medical.appointments.profiles.profile.Profile;
import com.medical.appointments.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Table(name = "patients")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PatientProfile extends Profile {
    @Setter
    private String address;

    @Setter
    private String phone;

    // TODO: This field is temporary. Medical history should eventually be generated
    // from completed appointments, diagnoses, prescriptions, and doctor notes.
    @Column(length = 2000)
    private String medicalHistory;

    @Setter
    @Column(length = 2000)
    private String allergies;

    public PatientProfile(User user) {
        super(user);
    }
}
