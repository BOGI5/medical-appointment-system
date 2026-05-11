package com.medical.appointments.exception;

import org.springframework.http.HttpStatus;

public class RoleAlreadyAssignedException extends HttpException {
    public RoleAlreadyAssignedException() {
        super(ErrorMessages.ROLE_ALREADY_ASSIGNED, HttpStatus.CONFLICT);
    }
}
