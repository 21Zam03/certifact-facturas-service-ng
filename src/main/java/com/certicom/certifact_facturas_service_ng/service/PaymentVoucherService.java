package com.certicom.certifact_facturas_service_ng.service;

import com.certicom.certifact_facturas_service_ng.dto.model.PaymentVoucherDto;
import com.certicom.certifact_facturas_service_ng.exceptions.ServiceException;

import java.util.Map;

public interface PaymentVoucherService {

    Map<String, Object> findPaymentVoucherWithFilter(
            String filtroDesde, String filtroHasta,
            String filtroTipoComprobante, String filtroRuc, String filtroSerie, Integer filtroNumero,
            Integer pageNumber, Integer perPage, Integer estadoSunat, Long idUsuario);

    /*METODO PARA GUARDAR O EDITAR UN COMPROBANTE*/
    Map<String, Object> generatePaymentVoucher(
            PaymentVoucherDto paymentVoucherDto, boolean isEdit, Long idUsuario
    );

    /*NO DEBERIA ESTAR - solo por ahora*/
    PaymentVoucherDto prepareComprobanteForEnvioSunatInter(String ruc, String tipo, String serie, Integer numero) throws ServiceException;


}
