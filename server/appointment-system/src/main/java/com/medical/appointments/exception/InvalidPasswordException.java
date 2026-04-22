package com.medical.appointments.exception;

import org.springframework.http.HttpStatus;

public class InvalidPasswordException extends HttpException {
    public InvalidPasswordException() {
        super(ErrorMessages.INVALID_PASSWORD, HttpStatus.UNAUTHORIZED);
    }
}
