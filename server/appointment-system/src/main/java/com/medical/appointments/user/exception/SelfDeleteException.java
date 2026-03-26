package com.medical.appointments.user.exception;

import com.medical.appointments.exception.BaseException;
import org.springframework.http.HttpStatus;

public class SelfDeleteException extends BaseException {

    private static final String MESSAGE = "Issuer cannot be deleted, try using DELETE users/me endpoint";

    public SelfDeleteException() {
        super(MESSAGE, HttpStatus.CONFLICT);
    }
}
