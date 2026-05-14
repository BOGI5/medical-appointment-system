package com.medical.appointments.exception;

import org.springframework.http.HttpStatus;

public class RoleNotAssignedException extends HttpException {
    public RoleNotAssignedException() {
        super(ErrorMessages.ROLE_NOT_ASSIGNED, HttpStatus.CONFLICT);
    }
}
