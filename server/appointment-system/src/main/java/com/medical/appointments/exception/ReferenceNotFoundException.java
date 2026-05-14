package com.medical.appointments.exception;

import org.springframework.http.HttpStatus;

public class ReferenceNotFoundException extends HttpException {
    public ReferenceNotFoundException() {
        super(ErrorMessages.REFERENCE_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
}
