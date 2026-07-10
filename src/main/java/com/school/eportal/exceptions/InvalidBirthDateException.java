package com.school.eportal.exceptions;

public class InvalidBirthDateException extends RuntimeException {
    public InvalidBirthDateException(String message) {
        super(message);
    }
}
