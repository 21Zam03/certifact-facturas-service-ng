package com.certicom.certifact_facturas_service_ng.service;

import com.certicom.certifact_facturas_service_ng.dto.others.GetStatusCdrDto;
import com.certicom.certifact_facturas_service_ng.model.PaymentVoucherModel;
import com.certicom.certifact_facturas_service_ng.dto.response.ResponseSunat;
import com.certicom.certifact_facturas_service_ng.exceptions.ServiceException;


public interface SendSunatService {

    PaymentVoucherModel prepareComprobanteForEnvioSunatInter(String ruc, String tipo, String serie, Integer numero) throws ServiceException;
    ResponseSunat sendBillPaymentVoucher(String fileName, String contentFileBase64, String rucEmisor);
    ResponseSunat getStatusCDR(GetStatusCdrDto statusDto, String rucEmisor);

    ResponseSunat sendSummary(String fileName, String contentFileBase64, String rucEmisor);

}
