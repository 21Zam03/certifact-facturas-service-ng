package com.certicom.certifact_facturas_service_ng.service;

import com.certicom.certifact_facturas_service_ng.model.PaymentVoucher;

import java.util.Map;

public interface PaymentVoucherService {

    Map<String, Object> findPaymentVoucherWithFilter(
            String filtroDesde, String filtroHasta,
            String filtroTipoComprobante, String filtroRuc, String filtroSerie, Integer filtroNumero,
            Integer pageNumber, Integer perPage, Integer estadoSunat, Long idUsuario);

    /*METODO PARA GUARDAR O EDITAR UN COMPROBANTE*/
    Map<String, Object> generatePaymentVoucher(
            PaymentVoucher paymentVoucher, boolean isEdit, Long idUsuario
    );

}
