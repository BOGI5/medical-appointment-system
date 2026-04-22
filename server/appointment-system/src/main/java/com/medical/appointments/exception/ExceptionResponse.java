package com.medical.appointments.exception;

public record ExceptionResponse(int status, String error, String message) {}
