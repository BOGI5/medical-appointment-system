package com.medical.appointments.security.response;

import com.medical.appointments.exception.ExceptionResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecurityErrorResponseWriter {

    private final JsonMapper jsonMapper;

    public void write(HttpServletResponse response, HttpStatus status, String message) throws IOException {

        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ExceptionResponse exceptionResponse = new ExceptionResponse(
                status.value(),
                status.getReasonPhrase(),
                message
        );

        String body = jsonMapper.writeValueAsString(exceptionResponse);

        response.getWriter().write(body);
    }
}
