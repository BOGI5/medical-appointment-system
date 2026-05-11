package com.medical.appointments.exception;

import org.springframework.http.HttpStatus;

public class PatientProfileNotFoundException extends HttpException {
    public PatientProfileNotFoundException() {
        super(ErrorMessages.PATIENT_PROFILE_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
}
