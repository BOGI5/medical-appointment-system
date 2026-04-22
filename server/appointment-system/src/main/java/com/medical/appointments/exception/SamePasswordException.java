package com.medical.appointments.exception;

import org.springframework.http.HttpStatus;

public class SamePasswordException extends HttpException {
    public SamePasswordException() {
        super(ErrorMessages.SAME_PASSWORD, HttpStatus.BAD_REQUEST);
    }
}
