package com.medical.appointments.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ExceptionResponse> handleBaseException(BaseException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(
                new ExceptionResponse(e.getHttpStatus().value(), e.getHttpStatus().getReasonPhrase(), e.getMessage())
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .orElse("Validation error");

        return ResponseEntity.badRequest().body(
                new ExceptionResponse(400, "Bad Request", message)
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleGenericException(Exception e) {
        log.error("Unexpected error", e);

        return ResponseEntity.internalServerError().body(
                new ExceptionResponse(500, "Internal Server Error", "Unexpected error occurred")
        );
    }
}
