package com.certicom.certifact_facturas_service_ng.enums;

public enum EstadoVoucherTmpEnum {

    PENDIENTE("P"),
    ERROR("E"),
    VERIFICAR("V"),
    BLOQUEO("B");

    private EstadoVoucherTmpEnum(String estado) {
        this.estado = estado;
    }

    private final String estado;

    public String getEstado() {
        return estado;
    }

}
