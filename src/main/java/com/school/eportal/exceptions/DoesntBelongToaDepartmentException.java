package com.school.eportal.exceptions;

public class DoesntBelongToaDepartmentException extends Exception {
    public DoesntBelongToaDepartmentException(String message) {
        super(message);
    }
}
