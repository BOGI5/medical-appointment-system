package com.medical.appointments.exception;

import org.springframework.http.HttpStatus;

public class TimeSlotNotFoundException extends HttpException {
    public TimeSlotNotFoundException() {
        super(ErrorMessages.TIME_SLOT_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
}
