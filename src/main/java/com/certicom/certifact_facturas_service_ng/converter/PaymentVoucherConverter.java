package com.certicom.certifact_facturas_service_ng.converter;

import com.certicom.certifact_facturas_service_ng.dto.PaymentVoucherDto;
import com.certicom.certifact_facturas_service_ng.request.PaymentVoucherRequest;

public class PaymentVoucherConverter {

    public static PaymentVoucherDto requestToModel(PaymentVoucherRequest paymentVoucherRequest) {
        return PaymentVoucherDto.builder()
                .ublVersion("2.1")
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
                .tipoDocumentoEmisor(paymentVoucherRequest.getTipoDocumentoEmisor())
                .tipoDocumentoReceptor(paymentVoucherRequest.getTipoDocumentoReceptor())
                .numeroDocumentoReceptor(paymentVoucherRequest.getNumeroDocumentoReceptor())
                .denominacionReceptor(paymentVoucherRequest.getDenominacionReceptor())
                .direccionReceptor(paymentVoucherRequest.getDireccionReceptor())
                .emailReceptor(paymentVoucherRequest.getEmailReceptor())
                .totalValorVentaGravada(paymentVoucherRequest.getTotalValorVentaGravada())
                .totalValorVentaInafecta(paymentVoucherRequest.getTotalValorVentaInafecta())
                .totalValorVentaGratuita(paymentVoucherRequest.getTotalValorVentaGratuita())
                .totalValorVentaExportacion(paymentVoucherRequest.getTotalValorVentaExportacion())
                .totalValorVentaExonerada(paymentVoucherRequest.getTotalValorVentaExonerada())
                .totalValorVentaGravadaIVAP(paymentVoucherRequest.getTotalValorVentaGravadaIVAP())
                .totalIgv(paymentVoucherRequest.getTotalIgv())
                .importeTotalVenta(paymentVoucherRequest.getImporteTotalVenta())
                .tipoComprobanteAfectado(paymentVoucherRequest.getTipoComprobanteAfectado())
                .serieAfectado(paymentVoucherRequest.getSerieAfectado())
                .numeroAfectado(paymentVoucherRequest.getNumeroAfectado())
                .codigoTipoNotaCredito(paymentVoucherRequest.getCodigoTipoNotaCredito())
                .motivoNota(paymentVoucherRequest.getMotivoNota())
                .anticipos(paymentVoucherRequest.getAnticipos())
                .camposAdicionales(paymentVoucherRequest.getCamposAdicionales())
                .cuotas(paymentVoucherRequest.getCuotas())
                .items(paymentVoucherRequest.getItems())
                .guiasRelacionadas(paymentVoucherRequest.getGuiasRelacionadas())
                .totalImpOperGratuita(paymentVoucherRequest.getTotalImpOperGratuita())
                .build();
    }
/*
    public static PaymentVoucherDto EntityToDto(PaymentVoucherEntity paymentVoucherEntity) {
        return PaymentVoucherDto.builder()
                .ublVersion("2.1")
                .rucEmisor(paymentVoucherEntity.getRucEmisor())
                .tipoComprobante(paymentVoucherEntity.getTipoComprobante())
                .serie(paymentVoucherEntity.getSerie())
                .numero(paymentVoucherEntity.getNumero())
                .fechaEmision(paymentVoucherEntity.getFechaEmision())
                .horaEmision(paymentVoucherEntity.getHoraEmision())
                .fechaEmision(paymentVoucherEntity.getFechaEmision())
                .codigoMoneda(paymentVoucherEntity.getCodigoMoneda())
                .fechaVencimiento(paymentVoucherEntity.getFechaVencimiento())
                .codigoTipoOperacion(paymentVoucherEntity.getTipoOperacion())
                //.tipoDocumentoEmisor("")
                .tipoDocumentoReceptor(paymentVoucherEntity.getTipoDocumentoReceptor())
                .numeroDocumentoReceptor(paymentVoucherEntity.getNumeroDocumentoReceptor())
                .denominacionReceptor(paymentVoucherEntity.getDenominacionReceptor())
                .direccionReceptor(paymentVoucherEntity.getDireccionReceptor())
                .emailReceptor(paymentVoucherEntity.getEmailReceptor())
                .totalValorVentaGravada(paymentVoucherEntity.getTotalValorVentaGravada())
                .totalIgv(paymentVoucherEntity.getTotalIgv())
                .importeTotalVenta(paymentVoucherEntity.getImporteTotalVenta())
                .anticipos(paymentVoucherEntity.getAnticipos())
                .camposAdicionales(paymentVoucherEntity.getCamposAdicionales())
                .cuotas(paymentVoucherEntity.getCuotas())
                .items(paymentVoucherEntity.getItems())
                .guiasRelacionadas(paymentVoucherEntity.getGuiasRelacionadas())
                .build();
    }*/
}
