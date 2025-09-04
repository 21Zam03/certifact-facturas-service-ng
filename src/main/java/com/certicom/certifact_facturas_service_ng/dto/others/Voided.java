package com.certicom.certifact_facturas_service_ng.dto.others;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Voided {

    private String fechaBaja;
    private String rucEmisor;
    private List<VoidedLine> lines;

    private String id;
    private Integer correlativoGeneracionDia;
    private String fechaGeneracion;

    private String denominacionEmisor;
    private String nombreComercialEmisor;
    private String tipoDocumentoEmisor;

    private String estadoComprobante;

}
