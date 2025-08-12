package com.certicom.certifact_facturas_service_ng.exceptions;

public class ExcepcionCamposValidacion extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String mensajeValidacion;

    public ExcepcionCamposValidacion(String mensajeValidacion) {
        super();
        this.mensajeValidacion = mensajeValidacion;
    }

    public String getMensajeValidacion() {
        return mensajeValidacion;
    }

    public void setMensajeValidacion(String mensajeValidacion) {
        this.mensajeValidacion = mensajeValidacion;
    }

}
