package com.medical.appointments.exception;

import org.springframework.http.HttpStatus;

public class ProfileAlreadyExistsException extends HttpException {
    public ProfileAlreadyExistsException() {
        super(ErrorMessages.PROFILE_ALREADY_EXISTS, HttpStatus.CONFLICT);
    }
}
