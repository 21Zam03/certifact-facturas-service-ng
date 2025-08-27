package com.certicom.certifact_facturas_service_ng.exceptions;

public class ValidationException extends RuntimeException {

    private String errorCode;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
