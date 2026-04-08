package com.medical.appointments.user.exception;

import com.medical.appointments.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidPasswordException extends BaseException {

    private final static String MESSAGE = "Invalid password";

    public InvalidPasswordException() {
        super(MESSAGE, HttpStatus.UNAUTHORIZED);
    }
}
