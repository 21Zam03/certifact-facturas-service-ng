package com.certicom.certifact_facturas_service_ng.enums;

public enum EstadoComprobanteEnum {

    REGISTRADO("Registrado", "01"),
    ACEPTADO("Aceptado", "02"),
    ACEPTADO_POR_VERIFICAR("Aceptado por verificar", "03"),
    ACEPTADO_ADVERTENCIA("Aceptado con advertencia", "04"),
    RECHAZADO("Rechazado", "05"),
    ERROR("Error", "06"),
    PROCESO_ENVIO("Proceso envio", "07"),
    ANULADO("Anulado", "08"),
    PENDIENTE_ANULACION("Pendiente de anulacion", "09");

    private EstadoComprobanteEnum(String descripcion, String codigo) {
        this.descripcion = descripcion;
        this.codigo = codigo;
    }
    public static EstadoComprobanteEnum getEstadoComprobante(String codigo) {
        switch(codigo) {
            case "01": return EstadoComprobanteEnum.REGISTRADO;
            case "02": return EstadoComprobanteEnum.ACEPTADO;
            case "03": return EstadoComprobanteEnum.ACEPTADO_POR_VERIFICAR;
            case "04": return EstadoComprobanteEnum.ACEPTADO_ADVERTENCIA;
            case "05": return EstadoComprobanteEnum.RECHAZADO;
            case "06": return EstadoComprobanteEnum.ERROR;
            case "07": return EstadoComprobanteEnum.PROCESO_ENVIO;
            case "08": return EstadoComprobanteEnum.ANULADO;
            case "09": return EstadoComprobanteEnum.PENDIENTE_ANULACION;

            default : throw new IllegalArgumentException("No esta definido dicho codigo ["+codigo+"]");
        }
    }
    private final String descripcion;
    private final String codigo;

    public String getDescripcion() {
        return descripcion;
    }
    public String getCodigo() {
        return codigo;
    }

}
