package com.medical.appointments.exception;

import org.springframework.http.HttpStatus;

public class InvalidTimeOrderException extends HttpException {
    public InvalidTimeOrderException() {
        super(ErrorMessages.INVALID_TIME_ORDER, HttpStatus.BAD_REQUEST);
    }
}
