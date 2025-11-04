package com.certicom.certifact_facturas_service_ng.util;

public class LogMessages {

    public static String currentMethod() {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }

    public static final String ERROR_UNEXPECTED = "Error inesperado";

    public static final String ERROR_HTTP = "Error al comunicarse con el servicio externo";
    public static final String ERROR_HTTP_RED = "Error de conexión con el servicio externo";
    public static final String ERROR_HTTP_SERVER = "Error del servicio externo";

}
