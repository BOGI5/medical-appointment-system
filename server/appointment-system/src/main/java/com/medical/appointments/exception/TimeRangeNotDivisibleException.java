package com.medical.appointments.exception;

import org.springframework.http.HttpStatus;

public class TimeRangeNotDivisibleException extends HttpException {
    public TimeRangeNotDivisibleException() {
        super(ErrorMessages.TIME_RANGE_NOT_DIVISIBLE, HttpStatus.BAD_REQUEST);
    }
}
