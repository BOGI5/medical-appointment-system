package com.medical.appointments.user.exception;

import com.medical.appointments.exception.BaseException;
import org.springframework.http.HttpStatus;

public class SamePasswordException extends BaseException {

    private final static String MESSAGE = "The new password is same as the current";

    public SamePasswordException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
