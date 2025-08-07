package com.certicom.certifact_facturas_service_ng.config;

import com.certicom.certifact_facturas_service_ng.dto.response.ErrorResponse;
import com.certicom.certifact_facturas_service_ng.exceptions.ExcepcionInterno;
import com.certicom.certifact_facturas_service_ng.exceptions.ExcepcionNegocio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

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
