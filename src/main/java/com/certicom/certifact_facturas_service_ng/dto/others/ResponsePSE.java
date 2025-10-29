package com.certicom.certifact_facturas_service_ng.dto.others;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponsePSE {

    private Boolean estado;
    private String mensaje;
    private String nombre;

    private String urlPdf;
    private String urlPdfTicket;
    private String urlPdfA4;
    private String urlXml;

    private String ticket;

    private String estadoSunat;
    private String urlCdr;

    private Object respuesta;

    private String codigoHash;

    private Integer intentosGetStatus;

}
