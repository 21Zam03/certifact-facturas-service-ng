package com.certicom.certifact_facturas_service_ng.formatter;

import com.certicom.certifact_facturas_service_ng.dto.model.PaymentVoucherDto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PaymentVoucherFormatter {

    public static void formatPaymentVoucher(PaymentVoucherDto paymentVoucherDto) {
        if (paymentVoucherDto.getTotalValorVentaGravada() != null && paymentVoucherDto.getTotalValorVentaGravada().compareTo(new BigDecimal(0)) == 0) {
            paymentVoucherDto.setTotalValorVentaGravada(null);
        }
        if (paymentVoucherDto.getTotalValorVentaGratuita() != null && paymentVoucherDto.getTotalValorVentaGratuita().compareTo(new BigDecimal(0)) == 0) {
            paymentVoucherDto.setTotalValorVentaGratuita(null);
        }
        if (paymentVoucherDto.getTotalValorVentaExonerada() != null && paymentVoucherDto.getTotalValorVentaExonerada().compareTo(new BigDecimal(0)) == 0) {
            paymentVoucherDto.setTotalValorVentaExonerada(null);
        }
        if (paymentVoucherDto.getTotalValorVentaExportacion() != null && paymentVoucherDto.getTotalValorVentaExportacion().compareTo(new BigDecimal(0)) == 0) {
            paymentVoucherDto.setTotalValorVentaExportacion(null);
        }
        if (paymentVoucherDto.getTotalValorVentaInafecta() != null && paymentVoucherDto.getTotalValorVentaInafecta().compareTo(new BigDecimal(0)) == 0) {
            paymentVoucherDto.setTotalValorVentaInafecta(null);
        }
        if (paymentVoucherDto.getTotalIgv() != null && paymentVoucherDto.getTotalIgv().compareTo(new BigDecimal(0)) == 0) {
            paymentVoucherDto.setTotalIgv(null);
        }
        if (paymentVoucherDto.getMontoDetraccion() != null) {
            paymentVoucherDto.setMontoDetraccion(paymentVoucherDto.getMontoDetraccion().setScale(2, RoundingMode.CEILING));
        }
        if (paymentVoucherDto.getTipoTransaccion() == null) {
            paymentVoucherDto.setTipoTransaccion(BigDecimal.ONE);
        }
    }

}
