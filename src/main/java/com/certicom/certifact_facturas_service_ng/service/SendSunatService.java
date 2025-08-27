package com.certicom.certifact_facturas_service_ng.service;

import com.certicom.certifact_facturas_service_ng.dto.model.GetStatusCdrDto;
import com.certicom.certifact_facturas_service_ng.dto.model.PaymentVoucherDto;
import com.certicom.certifact_facturas_service_ng.dto.response.ResponseSunat;
import com.certicom.certifact_facturas_service_ng.exceptions.ServiceException;

public interface SendSunatService {

    PaymentVoucherDto prepareComprobanteForEnvioSunatInter(String ruc, String tipo, String serie, Integer numero) throws ServiceException;
    ResponseSunat sendBillPaymentVoucher(String fileName, String contentFileBase64, String rucEmisor);
    ResponseSunat getStatusCDR(GetStatusCdrDto statusDto, String rucEmisor);

}
