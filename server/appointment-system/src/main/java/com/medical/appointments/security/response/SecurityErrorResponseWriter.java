package com.medical.appointments.security.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.appointments.exception.ExceptionResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecurityErrorResponseWriter {

    private final ObjectMapper objectMapper;

    public void write(HttpServletResponse response, HttpStatus status) throws IOException {

        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ExceptionResponse body = new ExceptionResponse(
                status.value(),
                status.getReasonPhrase(),
                status.getReasonPhrase()
        );

        objectMapper.writeValue(response.getWriter(), body);
    }
}
