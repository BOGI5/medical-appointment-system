package com.medical.appointments.roles.patient.dto;

public record UpdatePatientProfile(
        String address,
        String phone,
        String allergies
) {}
