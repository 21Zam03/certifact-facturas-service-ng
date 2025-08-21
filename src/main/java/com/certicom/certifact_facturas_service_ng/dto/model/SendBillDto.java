package com.certicom.certifact_facturas_service_ng.dto.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SendBillDto {

    private String ruc;
    private Long idPaymentVoucher;
    private String nameDocument;
    private Boolean envioAutomaticoSunat;

}
