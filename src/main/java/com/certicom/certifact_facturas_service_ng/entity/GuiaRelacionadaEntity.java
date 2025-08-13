package com.certicom.certifact_facturas_service_ng.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GuiaRelacionadaEntity {

    private Long idGuiaPayment;
    private String codigoTipoGuia;
    private String serieNumeroGuia;
    private Long idguiaremision;

}
