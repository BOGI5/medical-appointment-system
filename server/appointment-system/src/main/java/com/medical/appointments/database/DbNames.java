package com.medical.appointments.database;

public final class DbNames {
    private DbNames() {}

    // tables
    public static final String USERS = "users";
    public static final String USER_ROLES = "user_roles";
    public static final String REFRESH_TOKENS = "refresh_tokens";
    public static final String PATIENTS = "patients";
    public static final String DOCTORS = "doctors";
    public static final String ALLERGIES = "allergies";
    public static final String SPECIALIZATIONS = "specializations";
    public static final String PATIENT_ALLERGIES = "patient_allergies";
    public static final String TIME_SLOTS = "time_slots";

    // column keys
    public static final String USER_ID = "user_id";
    public static final String PATIENT_ID = "patient_id";
    public static final String DOCTOR_ID = "doctor_id";
    public static final String ALLERGY_ID = "allergy_id";
    public static final String SPECIALIZATION_ID = "specialization_id";
}
