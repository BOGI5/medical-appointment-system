package com.medical.appointments.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends HttpException {
    public UserNotFoundException() {
        super(ErrorMessages.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
}
