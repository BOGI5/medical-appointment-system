package com.medical.appointments.profiles.patient;

import com.medical.appointments.exception.ReferenceAlreadyAssignedException;
import com.medical.appointments.exception.ReferenceNotAssignedException;
import com.medical.appointments.profiles.profile.Profile;
import com.medical.appointments.references.allergy.Allergy;
import com.medical.appointments.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

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

    @ManyToMany
    @JoinTable(
            name = "patient_allergies",
            joinColumns = @JoinColumn(name = "patient_id"),
            inverseJoinColumns = @JoinColumn(name = "allergy_id")
    )
    private Set<Allergy> allergies = new HashSet<>();

    public void addAllergy(Allergy allergy) {
        if (this.allergies.contains(allergy)) {
            throw new ReferenceAlreadyAssignedException();
        }
        allergies.add(allergy);
    }

    public void removeAllergy(Allergy allergy) {
        if (!this.allergies.contains(allergy)) {
            throw new ReferenceNotAssignedException();
        }
        allergies.remove(allergy);
    }

    public PatientProfile(User user) {
        super(user);
    }
}
