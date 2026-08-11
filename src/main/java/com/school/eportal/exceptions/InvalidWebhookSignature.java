package com.school.eportal.exceptions;

public class InvalidWebhookSignature extends RuntimeException {
    public InvalidWebhookSignature(String message) {
        super(message);
    }
}
