package com.school.eportal.utils;

import com.school.eportal.exceptions.*;
import com.school.eportal.security.exceptions.AuthenticationNotSupportedException;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = AcademicSessionDoesntExistException.class)
    public ProblemDetail handleException(@NonNull AcademicSessionDoesntExistException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(value = AccountNotFoundException.class)
    public ProblemDetail handleException(@NonNull AccountNotFoundException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(value = DepartmentPathException.class)
    public ProblemDetail handleException(@NonNull DepartmentPathException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(value = DoesntBelongToaDepartmentException.class)
    public ProblemDetail handleException(@NonNull DoesntBelongToaDepartmentException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(value = EmptyCellException.class)
    public ProblemDetail handleException(@NonNull EmptyCellException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(value = ExcelParserException.class)
    public ProblemDetail handleException(@NonNull ExcelParserException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(value = FeeLedgerDoesntExistException.class)
    public ProblemDetail handleException(@NonNull FeeLedgerDoesntExistException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(value = FeeTransactionDoesntExistException.class)
    public ProblemDetail handleException(@NonNull FeeTransactionDoesntExistException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(value = InactiveAccountStatusException.class)
    public ProblemDetail handleException(@NonNull InactiveAccountStatusException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(value = InvalidAccountStatusException.class)
    public ProblemDetail handleException(@NonNull InvalidAccountStatusException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(value = InvalidAmountException.class)
    public ProblemDetail handleException(@NonNull InvalidAmountException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(value = InvalidBulkRegistration.class)
    public ProblemDetail handleException(@NonNull InvalidBulkRegistration e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(value = InvalidCellValueException.class)
    public ProblemDetail handleException(@NonNull InvalidCellValueException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(value = InvalidClassroomException.class)
    public ProblemDetail handleException(@NonNull InvalidClassroomException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(value = InvalidDateOfBirthException.class)
    public ProblemDetail handleException(@NonNull InvalidDateOfBirthException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(value = InvalidFileTypeException.class)
    public ProblemDetail handleException(@NonNull InvalidFileTypeException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(value = InvalidPasswordException.class)
    public ProblemDetail handleException(@NonNull InvalidPasswordException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(value = InvalidPercentageException.class)
    public ProblemDetail handleException(@NonNull InvalidPercentageException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(value = InvalidPreRegistrationException.class)
    public ProblemDetail handleException(@NonNull InvalidPreRegistrationException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(value = InvalidRoleException.class)
    public ProblemDetail handleException(@NonNull InvalidRoleException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(value = InvalidSchoolSessionException.class)
    public ProblemDetail handleException(@NonNull InvalidSchoolSessionException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(value = InvalidSessionException.class)
    public ProblemDetail handleException(@NonNull InvalidSessionException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(value = InvalidUserException.class)
    public ProblemDetail handleException(@NonNull InvalidUserException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(value = InvalidUsernameException.class)
    public ProblemDetail handleException(@NonNull InvalidUsernameException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(value = InvalidWebhookSignature.class)
    public ProblemDetail handleException(@NonNull InvalidWebhookSignature e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(value = NoSuchClassroomException.class)
    public ProblemDetail handleException(@NonNull NoSuchClassroomException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(value = OutstandingSchoolFeesException.class)
    public ProblemDetail handleException(@NonNull OutstandingSchoolFeesException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(value = ParentChildRelationshipException.class)
    public ProblemDetail handleException(@NonNull ParentChildRelationshipException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(value = ParsingException.class)
    public ProblemDetail handleException(@NonNull ParsingException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(value = SchoolFeesException.class)
    public ProblemDetail handleException(@NonNull SchoolFeesException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(value = TransactionAlreadyExistsException.class)
    public ProblemDetail handleException(@NonNull TransactionAlreadyExistsException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    public ProblemDetail handleException(@NonNull UserNotFoundException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(value = ValidatorException.class)
    public ProblemDetail handleException(@NonNull ValidatorException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
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
    public ProblemDetail handleException(@NonNull AuthenticationNotSupportedException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, e.getMessage());
    }
}
