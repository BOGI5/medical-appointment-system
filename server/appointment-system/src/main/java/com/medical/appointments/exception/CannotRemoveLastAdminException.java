package com.medical.appointments.exception;

import org.springframework.http.HttpStatus;

public class CannotRemoveLastAdminException extends HttpException {
    public CannotRemoveLastAdminException() {
        super(ErrorMessages.CANNOT_REMOVE_LAST_ADMIN, HttpStatus.CONFLICT);
    }
}
