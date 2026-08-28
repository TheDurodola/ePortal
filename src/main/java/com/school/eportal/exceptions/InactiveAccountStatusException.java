package com.school.eportal.exceptions;

public class InactiveAccountStatusException extends RuntimeException {
    public InactiveAccountStatusException(String message) {
        super(message);
    }
}
