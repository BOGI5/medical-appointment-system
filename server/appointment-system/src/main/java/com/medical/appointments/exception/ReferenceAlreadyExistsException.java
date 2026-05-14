package com.medical.appointments.exception;

import org.springframework.http.HttpStatus;

public class ReferenceAlreadyExistsException extends HttpException {
    public ReferenceAlreadyExistsException() {
        super(ErrorMessages.REFERENCE_ALREADY_EXISTS, HttpStatus.CONFLICT);
    }
}
