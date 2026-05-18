package com.medical.appointments.exception;

import org.springframework.http.HttpStatus;

public class ReferenceAlreadyAssignedException extends HttpException {
    public ReferenceAlreadyAssignedException() {
        super(ErrorMessages.REFERENCE_ALREADY_ASSIGNED, HttpStatus.CONFLICT);
    }
}
