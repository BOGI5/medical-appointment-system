package com.medical.appointments.exception;

import org.springframework.http.HttpStatus;

public class InvalidRefreshTokenException extends HttpException {
    public InvalidRefreshTokenException() {
        super(ErrorMessages.INVALID_REFRESH_TOKEN, HttpStatus.UNAUTHORIZED);
    }
}
