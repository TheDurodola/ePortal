package com.school.eportal.utils;

import com.school.eportal.dtos.responses.ExceptionResponse;
import com.school.eportal.exceptions.AccountNotFoundException;
import com.school.eportal.exceptions.InvalidBulkRegistration;
import com.school.eportal.security.exceptions.AuthenticationNotSupportedException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = AccountNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(@NonNull AccountNotFoundException e) {
        ExceptionResponse exception = new ExceptionResponse(HttpStatus.NOT_FOUND.value(),
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception);
    }

    @ExceptionHandler(value = InvalidBulkRegistration.class)
    public ResponseEntity<ExceptionResponse> handleException(@NonNull InvalidBulkRegistration e) {
        ExceptionResponse exception = new ExceptionResponse(HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception);
    }


    @ExceptionHandler(value = AuthenticationNotSupportedException.class)
    public ResponseEntity<ExceptionResponse> handleException(@NonNull AuthenticationNotSupportedException e) {
        ExceptionResponse exception = new ExceptionResponse(HttpStatus.UNAUTHORIZED.value(),
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exception);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        var fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a, b) -> a));

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "One or more fields are invalid");
        problem.setTitle("Validation Failed");
        problem.setProperty("errors", fieldErrors);

        return problem;
    }


}
