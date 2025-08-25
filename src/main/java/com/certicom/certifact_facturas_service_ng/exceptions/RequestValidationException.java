package com.certicom.certifact_facturas_service_ng.exceptions;

public class RequestValidationException extends RuntimeException {
  public RequestValidationException(String message) {
    super(message);
  }
}
