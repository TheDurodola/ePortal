package com.school.eportal.exceptions;

public class FeeTransactionDoesntExistException extends RuntimeException {
    public FeeTransactionDoesntExistException(String message) {
        super(message);
    }
}
