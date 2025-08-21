package com.certicom.certifact_facturas_service_ng.converter;

import com.certicom.certifact_facturas_service_ng.dto.model.PaymentVoucherDto;
import com.certicom.certifact_facturas_service_ng.dto.request.PaymentVoucherRequest;

public class PaymentVoucherConverter {

    public static PaymentVoucherDto requestToDto(PaymentVoucherRequest paymentVoucherRequest) {
        return PaymentVoucherDto.builder()
                .rucEmisor(paymentVoucherRequest.getRucEmisor())
                .tipoComprobante(paymentVoucherRequest.getTipoComprobante())
                .serie(paymentVoucherRequest.getSerie())
                .numero(paymentVoucherRequest.getNumero())
                .fechaEmision(paymentVoucherRequest.getFechaEmision())
                .horaEmision(paymentVoucherRequest.getHoraEmision())
                .fechaEmision(paymentVoucherRequest.getFechaEmision())
                .codigoMoneda(paymentVoucherRequest.getCodigoMoneda())
                .fechaVencimiento(paymentVoucherRequest.getFechaVencimiento())
                .codigoTipoOperacion(paymentVoucherRequest.getCodigoTipoOperacion())
                .tipoDocumentoEmisor(paymentVoucherRequest.getTipoDocumentoReceptor())
                .tipoDocumentoReceptor(paymentVoucherRequest.getTipoDocumentoReceptor())
                .numeroDocumentoReceptor(paymentVoucherRequest.getNumeroDocumentoReceptor())
                .denominacionReceptor(paymentVoucherRequest.getDenominacionReceptor())
                .direccionReceptor(paymentVoucherRequest.getDireccionReceptor())
                .emailReceptor(paymentVoucherRequest.getEmailReceptor())
                .totalValorVentaGravada(paymentVoucherRequest.getTotalValorVentaGravada())
                .totalIgv(paymentVoucherRequest.getTotalIgv())
                .importeTotalVenta(paymentVoucherRequest.getImporteTotalVenta())
                .anticipos(paymentVoucherRequest.getAnticipos())
                .camposAdicionales(paymentVoucherRequest.getCamposAdicionales())
                .cuotas(paymentVoucherRequest.getCuotas())
                .items(paymentVoucherRequest.getItems())
                .guiasRelacionadas(paymentVoucherRequest.getGuiasRelacionadas())
                .build();
    }

}
