package com.medical.appointments.controller;

public final class ApiPaths {

    private ApiPaths() {}

    // base
    public static final String AUTH = "/auth";
    public static final String USERS = "/users";

    // user
    public static final String CURRENT = "/current";
    public static final String CURRENT_PASSWORD = CURRENT + "/password";
    public static final String BY_ID = "/{id}";

    // auth
    public static final String LOGIN = "/login";
    public static final String REGISTER = "/register";
    public static final String LOGOUT = "/logout";
    public static final String REFRESH = "/refresh";
}