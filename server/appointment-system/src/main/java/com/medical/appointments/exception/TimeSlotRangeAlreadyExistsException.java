package com.medical.appointments.exception;

import org.springframework.http.HttpStatus;

public class TimeSlotRangeAlreadyExistsException extends HttpException {
    public TimeSlotRangeAlreadyExistsException() {
        super(ErrorMessages.TIME_RANGE_ALREADY_EXISTS, HttpStatus.CONFLICT);
    }
}
