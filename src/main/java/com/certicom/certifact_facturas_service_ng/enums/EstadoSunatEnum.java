package com.certicom.certifact_facturas_service_ng.enums;

public enum EstadoSunatEnum {

    ACEPTADO("Aceptado", "ACEPT"),
    RECHAZADO("Rechazado", "RECHA"),
    ANULADO("Anulado", "ANULA"),
    NO_ENVIADO("No enviado", "N_ENV");

    private EstadoSunatEnum(String descripcion, String abreviado) {
        this.descripcion = descripcion;
        this.abreviado = abreviado;
    }

    private final String descripcion;
    private final String abreviado;

    public String getDescripcion() {
        return descripcion;
    }
    public String getAbreviado() {
        return abreviado;
    }

}
