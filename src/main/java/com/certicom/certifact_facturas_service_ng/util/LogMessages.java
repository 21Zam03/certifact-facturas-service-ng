package com.certicom.certifact_facturas_service_ng.util;

public class LogMessages {

    public static final String PAYMENT_VOUCHER_UPDATED = "Se actualizó la entidad payment_voucher exitosamente, identificador: {}";

    public static final String PROCESS_FAILED_LOG = "Error en el proceso, error: {}";
    public static final String PROCESS_FAILED_EXCEPTION = "Error en el proceso, error:";
    public static final String ERROR_EXCEPTION = "Error: ";

    //LOGS HACIENDO CONSULTAS A BASE DE DATOS
    public static final String ENTITY_NOT_FOUND_EXCEPTION = "Entidad no encontrada con id: ";

    public static final String ERROR_UNEXPECTED = "Error inesperado en la capa de servicio";
    public static final String ERROR_DATABASE = "Error en base de datos";
    public static final String ERROR_VALIDATION = "Error de validación";
    public static final String SUCCESS_GET = "Consulta exitosa";

    public static String currentMethod() {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }

}
