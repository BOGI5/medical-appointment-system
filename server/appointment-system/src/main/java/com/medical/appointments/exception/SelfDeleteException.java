package com.medical.appointments.exception;

import org.springframework.http.HttpStatus;

public class SelfDeleteException extends HttpException {
    public SelfDeleteException() {
        super(ErrorMessages.SELF_DELETE, HttpStatus.FORBIDDEN);
    }
}
