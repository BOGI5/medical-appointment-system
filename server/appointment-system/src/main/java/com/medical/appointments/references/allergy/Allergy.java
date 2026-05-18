package com.medical.appointments.references.allergy;

import com.medical.appointments.profiles.patient.PatientProfile;
import com.medical.appointments.references.reference.Reference;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
@Table(name = "allergies")
@NoArgsConstructor(access =  AccessLevel.PROTECTED)
public class Allergy extends Reference {
    @ManyToMany(mappedBy = "allergies")
    private Set<PatientProfile> patients =  new HashSet<>();

    public Allergy(String name) {
        super(name);
    }
}
