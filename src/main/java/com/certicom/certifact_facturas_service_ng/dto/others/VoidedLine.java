package com.certicom.certifact_facturas_service_ng.dto.others;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoidedLine {

    private Integer numeroItem;
    private Integer numeroDocumento;
    private String serieDocumento;
    private String tipoComprobante;
    private String razon;

}
