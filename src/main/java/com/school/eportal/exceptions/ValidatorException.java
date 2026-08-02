package com.school.eportal.exceptions;

import java.io.IOException;

public class ValidatorException extends RuntimeException {
    public ValidatorException(IOException e) {
        super(e);
    }
}
