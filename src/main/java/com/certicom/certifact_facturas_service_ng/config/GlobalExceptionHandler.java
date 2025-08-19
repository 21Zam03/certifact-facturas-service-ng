package com.certicom.certifact_facturas_service_ng.config;

import com.certicom.certifact_facturas_service_ng.dto.response.ErrorResponse;
import com.certicom.certifact_facturas_service_ng.exceptions.ExcepcionInterno;
import com.certicom.certifact_facturas_service_ng.exceptions.ExcepcionNegocio;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("estado", false);

        List<String> mensajes = new ArrayList<>();

        // Errores de campo (ej. @NotNull, @Size, @Valid en anticipo.serieAnticipo, etc.)
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            mensajes.add(String.format("Campo [%s]: %s", error.getField(), error.getDefaultMessage()));
        });

        // Errores de clase (ej. validaciones cruzadas en ComprobanteRequest)
        ex.getBindingResult().getGlobalErrors().forEach(error -> {
            String mensaje = error.getDefaultMessage();
            if (mensaje == null || mensaje.isEmpty()) {
                mensaje = "El comprobante tiene campos mal formateados"; // fallback
            }
            mensajes.add(String.format("Objeto [%s]: %s", error.getObjectName(), mensaje));
        });

        errors.put("mensajes", mensajes);

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
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
