package com.medical.appointments.profiles.doctor;

import com.medical.appointments.profiles.profile.Profile;
import com.medical.appointments.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "doctors")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DoctorProfile extends Profile {
    private String specialization;

    private String phone;

    private String clinicAddress;

    @Column(length = 2000)
    private String bio;

    private Integer yearsOfExperience;

    public DoctorProfile(User user) {
        super(user);
    }
}
