package com.medical.appointments.exception;

import org.springframework.http.HttpStatus;

public class UserMustHaveAtLeastOneRoleException extends HttpException {
    public UserMustHaveAtLeastOneRoleException() {
        super(ErrorMessages.USER_MUST_HAVE_AT_LEAST_ONE_ROLE, HttpStatus.BAD_REQUEST);
    }
}
