package com.medical.appointments.controller;

public final class ApiPaths {

    private ApiPaths() {}

    // base
    public static final String AUTH = "/auth";
    public static final String USERS = "/users";
    public static final String ADMIN = "/admin";
    public static final String PATIENT_PROFILES = "/patient-profiles";
    public static final String DOCTOR_PROFILES = "/doctor-profiles";
    public static final String ALLERGIES = "/allergies";
    public static final String SPECIALIZATIONS = "/specializations";

    // general
    public static final String CURRENT = "/current";
    public static final String BY_ID = "/{id}";

    // user
    public static final String CURRENT_PASSWORD = CURRENT + "/password";

    // auth
    public static final String LOGIN = "/login";
    public static final String REGISTER = "/register";
    public static final String LOGOUT = "/logout";
    public static final String REFRESH = "/refresh";
    public static final String SWITCH_ROLE = "/switch-role";

    // patient profile
    public static final String ALLERGY_ID = "/{allergyId}";
    public static final String CURRENT_ALLERGIES_BY_ID = CURRENT + ALLERGIES + ALLERGY_ID;
    public static final String BY_ID_ALLERGY_BY_ID = BY_ID + ALLERGIES + ALLERGY_ID;
}
