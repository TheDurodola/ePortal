package com.school.eportal.exceptions;

public class AcademicSessionDoesntExistException extends RuntimeException {
    public AcademicSessionDoesntExistException(String message) {
        super(message);
    }
}
