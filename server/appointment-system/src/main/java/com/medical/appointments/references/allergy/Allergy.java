package com.medical.appointments.references.allergy;

import com.medical.appointments.references.reference.Reference;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "allergies")
@NoArgsConstructor(access =  AccessLevel.PROTECTED)
public class Allergy extends Reference {

    public Allergy(String name) {
        super(name);
    }
}
