package com.certicom.certifact_facturas_service_ng.config;

import com.certicom.certifact_facturas_service_ng.dto.others.ErrorResponse;
import com.certicom.certifact_facturas_service_ng.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DeserializerException.class)
    public ResponseEntity<ErrorResponse> handleDeserializadorException(DeserializerException ex) {
        return ResponseEntity.badRequest().body(ErrorResponse.builder()
                        .code("DESERIALIZATION_ERROR")
                        .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleDeserializadorException(ServiceException ex) {
        return ResponseEntity.badRequest().body(ErrorResponse.builder()
                .code("SERVICE_EXCEPTION_ERROR")
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler(SignedException.class)
    public ResponseEntity<ErrorResponse> handleDeserializadorException(SignedException ex) {
        return ResponseEntity.badRequest().body(ErrorResponse.builder()
                .code("SIGNE_EXCEPTION_ERROR")
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler(TemplateException.class)
    public ResponseEntity<ErrorResponse> handleDeserializadorException(TemplateException ex) {
        return ResponseEntity.badRequest().body(ErrorResponse.builder()
                .code("TEMPLATE_ERROR")
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleBusinessValidationException(ValidationException ex) {
        ErrorResponse response = ErrorResponse.builder()
                .code(ex.getErrorCode() != null ? ex.getErrorCode() : "BUSINESS_VALIDATION_ERROR")
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("UNEXPECTED_ERROR", "Ocurrió un error inesperado: "+ ex.getMessage()));
    }

}
