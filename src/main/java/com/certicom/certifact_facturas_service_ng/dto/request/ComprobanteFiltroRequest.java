package com.certicom.certifact_facturas_service_ng.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ComprobanteFiltroRequest {

    private String filtroDesde;
    private String filtroHasta;
    private String filtroTipoComprobante;
    private String filtroRuc;
    private String filtroSerie;
    private Integer filtroNumero;
    private Integer pageNumber;
    private Integer perPage;
    private Integer estadoSunat;
    private Integer offset;

    private Long idUsuario;
}
