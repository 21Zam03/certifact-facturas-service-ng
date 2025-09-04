package com.certicom.certifact_facturas_service_ng.dto.others;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class GetStatusCdrDto implements Serializable {

    private String ruc;
    private String tipoComprobante;
    private String serie;
    private Integer numero;
    private Long idPaymentVoucher;

}
