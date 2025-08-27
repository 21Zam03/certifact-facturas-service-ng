package com.certicom.certifact_facturas_service_ng.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CuotasPaymentVoucherEntity {

    private Long id;
    private Integer numero;
    private BigDecimal monto;
    private String fecha;
    //private PaymentVoucherEntity paymentVoucher;

}
