package com.medical.appointments.exception;

import org.springframework.http.HttpStatus;

public class PatientProfileAlreadyExistsException extends HttpException {
    public PatientProfileAlreadyExistsException() {
        super(ErrorMessages.PATIENT_PROFILE_ALREADY_EXISTS, HttpStatus.CONFLICT);
    }
}
