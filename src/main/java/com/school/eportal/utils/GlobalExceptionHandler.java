package com.school.eportal.utils;

import com.school.eportal.dtos.responses.ExceptionResponse;
import com.school.eportal.exceptions.*;
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

    @ExceptionHandler(value = DepartmentPathException.class)
    public ResponseEntity<ExceptionResponse> handleException(@NonNull DepartmentPathException e) {
        ExceptionResponse exception = new ExceptionResponse(HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception);
    }

    @ExceptionHandler(value = ExcelParserException.class)
    public ResponseEntity<ExceptionResponse> handleException(@NonNull ExcelParserException e) {
        ExceptionResponse exception = new ExceptionResponse(HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception);
    }

    @ExceptionHandler(value = InvalidAccountStatusException.class)
    public ResponseEntity<ExceptionResponse> handleException(@NonNull InvalidAccountStatusException e) {
        ExceptionResponse exception = new ExceptionResponse(HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception);
    }

    @ExceptionHandler(value = InvalidBulkRegistration.class)
    public ResponseEntity<ExceptionResponse> handleException(@NonNull InvalidBulkRegistration e) {
        ExceptionResponse exception = new ExceptionResponse(HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception);
    }

    @ExceptionHandler(value = InvalidClassroomException.class)
    public ResponseEntity<ExceptionResponse> handleException(@NonNull InvalidClassroomException e) {
        ExceptionResponse exception = new ExceptionResponse(HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception);
    }

    @ExceptionHandler(value = InvalidDateOfBirthException.class)
    public ResponseEntity<ExceptionResponse> handleException(@NonNull InvalidDateOfBirthException e) {
        ExceptionResponse exception = new ExceptionResponse(HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception);
    }

    @ExceptionHandler(value = InvalidFileTypeException.class)
    public ResponseEntity<ExceptionResponse> handleException(@NonNull InvalidFileTypeException e) {
        ExceptionResponse exception = new ExceptionResponse(HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception);
    }

    @ExceptionHandler(value = InvalidPreRegistrationException.class)
    public ResponseEntity<ExceptionResponse> handleException(@NonNull InvalidPreRegistrationException e) {
        ExceptionResponse exception = new ExceptionResponse(HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception);
    }

    @ExceptionHandler(value = InvalidUsernameException.class)
    public ResponseEntity<ExceptionResponse> handleException(@NonNull InvalidUsernameException e) {
        ExceptionResponse exception = new ExceptionResponse(HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception);
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(@NonNull UserNotFoundException e) {
        ExceptionResponse exception = new ExceptionResponse(HttpStatus.UNAUTHORIZED.value(),
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exception);
    }

    @ExceptionHandler(value = ValidatorException.class)
    public ResponseEntity<ExceptionResponse> handleException(@NonNull ValidatorException e) {
        ExceptionResponse exception = new ExceptionResponse(HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(@NonNull MethodArgumentNotValidException ex) {
        var fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a, b) -> a));

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "One or more fields are invalid");
        problem.setTitle("Validation Failed");
        problem.setProperty("errors", fieldErrors);

        return problem;
    }

    @ExceptionHandler(value = AuthenticationNotSupportedException.class)
    public ResponseEntity<ExceptionResponse> handleException(@NonNull AuthenticationNotSupportedException e) {
        ExceptionResponse exception = new ExceptionResponse(HttpStatus.UNAUTHORIZED.value(),
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exception);
    }
}
