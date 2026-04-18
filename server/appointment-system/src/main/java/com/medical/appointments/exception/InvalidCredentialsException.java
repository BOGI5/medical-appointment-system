package com.medical.appointments.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends HttpException {
    public InvalidCredentialsException() {
        super(ErrorMessages.INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
    }
}
