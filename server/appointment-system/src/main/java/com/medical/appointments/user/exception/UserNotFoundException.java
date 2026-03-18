package com.medical.appointments.user.exception;

import com.medical.appointments.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseException {

    private static final String MESSAGE = "User not found";

    public UserNotFoundException() {
        super(MESSAGE, HttpStatus.NOT_FOUND);
    }
}
