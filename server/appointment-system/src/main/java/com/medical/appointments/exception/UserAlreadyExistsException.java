package com.medical.appointments.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends HttpException {
    public UserAlreadyExistsException() {
        super(ErrorMessages.USER_ALREADY_EXISTS, HttpStatus.CONFLICT);
    }
}
