package com.certicom.certifact_facturas_service_ng.enums;


public enum ComunicationSunatEnum {

    SUCCESS("00"),
    SUCCESS_WITHOUT_CONTENT_CDR("01"),
    SUCCESS_WITH_ERROR_CONTENT("02"),
    SUCCESS_WITH_WARNING("05"),
    PENDING("06"),
    WITHOUT_CONNECTION("03"),
    ERROR_INTERNO_WS_API("04");

    private ComunicationSunatEnum (String estado) {
        this.estado = estado;
    }

    private final String estado;

    public String getEstado() {
        return estado;
    }

    public static ComunicationSunatEnum getEnum(String estado) {

        switch(estado) {
            case "00": return SUCCESS;
            case "01": return SUCCESS_WITHOUT_CONTENT_CDR;
            case "02": return SUCCESS_WITH_ERROR_CONTENT;
            case "05": return SUCCESS_WITH_WARNING;
            case "06": return PENDING;
            case "03": return WITHOUT_CONNECTION;
            case "04": return ERROR_INTERNO_WS_API;

            default : throw new IllegalArgumentException("No esta definido el estado ["+estado+"]");
        }
    }

}
