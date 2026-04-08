package com.medical.appointments.auth.exception;

import com.medical.appointments.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends BaseException {

    private static final String MESSAGE = "Invalid credentials";

    public InvalidCredentialsException() {
        super(MESSAGE, HttpStatus.UNAUTHORIZED);
    }
}
