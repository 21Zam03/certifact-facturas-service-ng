package com.certicom.certifact_facturas_service_ng.service;

import com.certicom.certifact_facturas_service_ng.dto.PaymentVoucherDto;
import com.certicom.certifact_facturas_service_ng.dto.others.InfoEstadoSunat;

import java.util.List;
import java.util.Map;

public interface PaymentVoucherService {

    Map<String, Object> findPaymentVoucherWithFilter(
            String filtroDesde, String filtroHasta,
            String filtroTipoComprobante, String filtroRuc, String filtroSerie, Integer filtroNumero,
            Integer pageNumber, Integer perPage, Integer estadoSunat, Long idUsuario);

    //Map<String, Object> generatePaymentVoucher(PaymentVoucherModel paymentVoucherModel, boolean isEdit, Long idUsuario);

    Map<String, Object> createPaymentVoucher(PaymentVoucherDto paymentVoucherDto, Long idUsuario);

    Map<String, Object> updatePaymentVoucher(PaymentVoucherDto paymentVoucherDto, Long idUsuario);

    Integer getSiguienteNumeroComprobante(String tipoDocumento, String serie, String ruc);

    List<PaymentVoucherDto> findComprobanteByAnticipo(String filtroNumDoc, String ruc);

    List<PaymentVoucherDto> findComprobanteByCredito(String filtroNumDoc, String ruc);

    List<InfoEstadoSunat> getEstadoSunatByListaIdsInter(List<Long> idsPaymentVouchers);
}
