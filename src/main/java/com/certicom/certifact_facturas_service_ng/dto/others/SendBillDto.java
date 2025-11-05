package com.certicom.certifact_facturas_service_ng.dto.others;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SendBillDto implements Serializable {

    private String ruc;
    private Long idPaymentVoucher;
    private String nameDocument;
    private Boolean envioAutomaticoSunat;

}
