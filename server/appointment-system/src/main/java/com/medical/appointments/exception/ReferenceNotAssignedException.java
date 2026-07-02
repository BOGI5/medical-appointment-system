package com.medical.appointments.exception;

import org.springframework.http.HttpStatus;

public class ReferenceNotAssignedException extends HttpException {
    public ReferenceNotAssignedException() {
        super(ErrorMessages.REFERENCE_NOT_ASSIGNED, HttpStatus.BAD_REQUEST);
    }
}
