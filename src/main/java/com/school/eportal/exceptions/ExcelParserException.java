package com.school.eportal.exceptions;

import java.io.IOException;

public class ExcelParserException extends RuntimeException {
    public ExcelParserException(IOException message) {
        super(message);
    }
}
