package com.medical.appointments.user;

public enum Role {
    PATIENT,
    DOCTOR,
    ADMIN;

    public String asAuthority() {
        return "ROLE_" + this.name();
    }
}
