package com.medical.appointments.exception.handler;

import com.medical.appointments.exception.BaseException;
import com.medical.appointments.exception.dto.ExceptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ExceptionResponse> handleBaseException(BaseException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(
                new ExceptionResponse(e.getHttpStatus().value(), e.getHttpStatus().getReasonPhrase(), e.getMessage())
        );
    }
}
