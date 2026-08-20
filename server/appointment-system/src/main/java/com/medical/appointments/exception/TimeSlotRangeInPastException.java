package com.medical.appointments.exception;

import org.springframework.http.HttpStatus;

public class TimeSlotRangeInPastException extends HttpException {
    public TimeSlotRangeInPastException() {
        super(ErrorMessages.TIME_RANGE_IN_THE_PAST, HttpStatus.BAD_REQUEST);
    }
}
