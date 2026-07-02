package com.medical.appointments.profiles.doctor;

import com.medical.appointments.database.DbNames;
import com.medical.appointments.profiles.profile.Profile;
import com.medical.appointments.references.specialization.Specialization;
import com.medical.appointments.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = DbNames.DOCTORS)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DoctorProfile extends Profile {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = DbNames.SPECIALIZATION_ID)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Specialization specialization;

    private String phone;

    private String clinicAddress;

    @Column(length = 2000)
    private String bio;

    private Integer yearsOfExperience;

    public DoctorProfile(User user) {
        super(user);
    }
}
