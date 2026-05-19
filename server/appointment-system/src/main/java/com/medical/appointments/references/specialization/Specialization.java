package com.medical.appointments.references.specialization;

import com.medical.appointments.database.DbNames;
import com.medical.appointments.references.reference.Reference;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = DbNames.SPECIALIZATIONS)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Specialization extends Reference {
    public Specialization(String name) {
        super(name);
    }
}
