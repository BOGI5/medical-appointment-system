package com.medical.appointments.exception;

import org.springframework.http.HttpStatus;

public class NoAccessToTimeSlotException extends HttpException {
    public NoAccessToTimeSlotException() {
        super(ErrorMessages.NO_ACCESS_TO_TIME_SLOT, HttpStatus.FORBIDDEN);
    }
}
