package com.gatherdotech.fleetmanagement.exception;

import java.util.Map;

public class DomainValidationException extends RuntimeException {
    private final Map<String, String> fieldErrors;
    private final String code;

    public DomainValidationException(String message, Map<String, String> fieldErrors) {
        super(message);
        this.fieldErrors = fieldErrors;
        this.code = null;
    }

    public DomainValidationException(String code, String message, Map<String, String> fieldErrors) {
        super(message);
        this.fieldErrors = fieldErrors;
        this.code = code;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    public String getCode() {
        return code;
    }
}
