package com.certicom.certifact_facturas_service_ng.enums;

public enum AfectacionIgvEnum {

    GRAVADO_OPERACION_ONOROSA			("10", "1000"),
    GRAVADO_RETIRO_PREMIO	 			("11", "9996"),
    GRAVADO_RETIRO_DONACION	 			("12", "9996"),
    GRAVADO_RETIRO			 			("13", "9996"),
    GRAVADO_RETIRO_PUBLICIDAD			("14", "9996"),
    GRAVADO_BONIFICACIONES	 		   	("15", "9996"),
    GRAVADO_RETIRO_ENTREGA_TRABAJADORES	("16", "9996"),
    GRAVADO_IVAP			 			("17", "1016"),
    GRAVADO_GRATUITA	 				("17", "9996"),
    EXONERADO_OPERACION_ONOROSA		   	("20", "9997"),
    EXONERADO_TRANSFERENCIA_GRATUITA	("21", "9996"),
    INAFECTO_OPERACION_ONOROSA			("30", "9998"),
    INAFECTO_RETIRO_BONIFICACION		("31", "9996"),
    INAFECTO_RETIRO						("32", "9996"),
    IANFECTO_RETIRO_MUESTRA_MEDICAS		("33", "9996"),
    INAFECTO_RETIRO_CONVENIO_COLECTIVO	("34", "9996"),
    INAFECTO_RETIRO_PREMIO				("35", "9996"),
    INAFECTO_RETIRO_PUBLICIDAD			("36", "9996"),
    INAFECTO_TRANSFERENCIA_GRATUITA		("37", "9996"),
    EXPORTACION_BIENES_SERVICIOS		("40", "9995"),
    EXPORTACION_GRATUITA				("40", "9996");

    private AfectacionIgvEnum(String codigo, String codigoTributo) {
        this.codigo = codigo;
        this.codigoTributo = codigoTributo;
    }
    public static AfectacionIgvEnum getEstadoComprobante(String codigo, Boolean isGratuito) {
        switch(codigo) {
            case "10": return GRAVADO_OPERACION_ONOROSA;
            case "11": return GRAVADO_RETIRO_PREMIO;
            case "12": return GRAVADO_RETIRO_DONACION;
            case "13": return GRAVADO_RETIRO;
            case "14": return GRAVADO_RETIRO_PUBLICIDAD;
            case "15": return GRAVADO_BONIFICACIONES;
            case "16": return GRAVADO_RETIRO_ENTREGA_TRABAJADORES;
            case "17": if(isGratuito) {
                return GRAVADO_GRATUITA;
            } else {
                return GRAVADO_IVAP;
            }
            case "20": return EXONERADO_OPERACION_ONOROSA;
            case "21": return EXONERADO_TRANSFERENCIA_GRATUITA;
            case "30": return INAFECTO_OPERACION_ONOROSA;
            case "31": return INAFECTO_RETIRO_BONIFICACION;
            case "32": return INAFECTO_RETIRO;
            case "33": return IANFECTO_RETIRO_MUESTRA_MEDICAS;
            case "34": return INAFECTO_RETIRO_CONVENIO_COLECTIVO;
            case "35": return INAFECTO_RETIRO_PREMIO;
            case "36": return INAFECTO_RETIRO_PUBLICIDAD;
            case "37": return INAFECTO_TRANSFERENCIA_GRATUITA;
            case "40": if(isGratuito) {
                return EXPORTACION_GRATUITA;
            } else {
                return EXPORTACION_BIENES_SERVICIOS;
            }

            default : throw new IllegalArgumentException("No esta definido dicho codigo ["+codigo+"]");
        }
    }

    private final String codigo;
    private final String codigoTributo;

    public String getCodigoTributo() {
        return codigoTributo;
    }
    public String getCodigo() {
        return codigo;
    }

}
