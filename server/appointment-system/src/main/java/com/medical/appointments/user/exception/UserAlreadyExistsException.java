package com.medical.appointments.user.exception;

import com.medical.appointments.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends BaseException {

    private static final String MESSAGE = "User already exists";

    public UserAlreadyExistsException() {
        super(MESSAGE, HttpStatus.CONFLICT);
    }
}
