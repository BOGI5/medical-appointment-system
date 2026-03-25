package com.medical.appointments.security.token.exception;

import com.medical.appointments.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidRefreshTokenException extends BaseException {

    private static final String MESSAGE = "Invalid Refresh Token";

    public InvalidRefreshTokenException() {
        super(MESSAGE, HttpStatus.UNAUTHORIZED);
    }
}
