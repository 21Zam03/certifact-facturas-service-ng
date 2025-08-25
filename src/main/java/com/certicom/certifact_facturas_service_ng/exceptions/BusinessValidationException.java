package com.certicom.certifact_facturas_service_ng.exceptions;

public class BusinessValidationException extends RuntimeException {

    private String errorCode;

    public BusinessValidationException(String message) {
        super(message);
    }

    public BusinessValidationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
