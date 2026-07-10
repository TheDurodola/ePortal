package com.school.eportal.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;

    public ExceptionResponse(String validationFailed, Map<String, String> errors) {

    }
}
