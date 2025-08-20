package com.certicom.certifact_facturas_service_ng.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnticipoEntity {

    private String identificadorPago;
    private String serieAnticipo;
    private Integer numeroAnticipo;
    private String tipoDocumentoAnticipo;
    private BigDecimal montoAnticipo;

}
