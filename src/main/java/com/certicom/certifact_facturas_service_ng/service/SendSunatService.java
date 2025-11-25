package com.certicom.certifact_facturas_service_ng.service;

import com.certicom.certifact_facturas_service_ng.dto.others.GetStatusCdrDto;
import com.certicom.certifact_facturas_service_ng.dto.PaymentVoucherDto;
import com.certicom.certifact_facturas_service_ng.dto.others.ResponseSunat;
import com.certicom.certifact_facturas_service_ng.exceptions.ServiceException;


public interface SendSunatService {

    PaymentVoucherDto prepareComprobanteForEnvioSunatInter(String ruc, String tipo, String serie, Integer numero) throws ServiceException;
    ResponseSunat sendBillPaymentVoucher(String fileName, String contentFileBase64, String rucEmisor);
    ResponseSunat getStatusCDR(GetStatusCdrDto statusDto, String rucEmisor);
    ResponseSunat getStatus(String nroTicket, String tipoResumen, String rucEmisor);
    ResponseSunat sendSummary(String fileName, String contentFileBase64, String rucEmisor);

}
