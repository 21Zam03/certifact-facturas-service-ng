package com.certicom.certifact_facturas_service_ng.dto.others;

import com.certicom.certifact_facturas_service_ng.enums.ComunicationSunatEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseSunat {

    private String contentBase64;
    private String statusCode;
    private boolean success;
    private String message;
    private String nameDocument;
    private String ticket;
    private String rucEmisor;
    private ComunicationSunatEnum estadoComunicacionSunat;
    private String indCdr;
    private String numError;
    protected int cod;

}
