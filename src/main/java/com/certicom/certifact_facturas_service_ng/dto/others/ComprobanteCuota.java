package com.certicom.certifact_facturas_service_ng.dto.others;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComprobanteCuota implements Serializable {

    private Long idCuotas;
    private Integer numero;
    private BigDecimal monto;
    private String fecha;
    private Long idPaymentVoucher;

}
