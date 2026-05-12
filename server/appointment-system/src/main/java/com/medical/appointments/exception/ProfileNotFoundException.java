package com.medical.appointments.exception;

import org.springframework.http.HttpStatus;

public class ProfileNotFoundException extends HttpException {
    public ProfileNotFoundException() {
        super(ErrorMessages.PROFILE_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
}
