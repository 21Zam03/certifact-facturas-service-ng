package com.certicom.certifact_facturas_service_ng.config;

import com.certicom.certifact_facturas_service_ng.dto.response.ErrorResponse;
import com.certicom.certifact_facturas_service_ng.exceptions.BusinessValidationException;
import com.certicom.certifact_facturas_service_ng.exceptions.DeserializadorException;
import com.certicom.certifact_facturas_service_ng.exceptions.ExcepcionInterno;
import com.certicom.certifact_facturas_service_ng.exceptions.ExcepcionNegocio;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DeserializadorException.class)
    public ResponseEntity<ErrorResponse> handleDeserializadorException(DeserializadorException ex) {
        return ResponseEntity.badRequest().body(ErrorResponse.builder()
                        .code("DESERIALIZATION_ERROR")
                        .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler(BusinessValidationException.class)
    public ResponseEntity<ErrorResponse> handleBusinessValidationException(BusinessValidationException ex) {
        ErrorResponse response = ErrorResponse.builder()
                .code(ex.getErrorCode() != null ? ex.getErrorCode() : "BUSINESS_VALIDATION_ERROR")
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExcepcionNegocio.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(ExcepcionNegocio ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("BUSINESS_ERROR", ex.getMessage()));
    }

    @ExceptionHandler(ExcepcionInterno.class)
    public ResponseEntity<ErrorResponse> handleInternalException(ExcepcionInterno ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("UNEXPECTED_ERROR", "Ocurrió un error inesperado"));
    }

}
