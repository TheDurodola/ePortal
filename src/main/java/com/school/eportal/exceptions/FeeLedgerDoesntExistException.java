package com.school.eportal.exceptions;

public class FeeLedgerDoesntExistException extends RuntimeException {
    public FeeLedgerDoesntExistException(String message) {
        super(message);
    }
}
