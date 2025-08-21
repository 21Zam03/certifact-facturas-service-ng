package com.certicom.certifact_facturas_service_ng.service;

import com.certicom.certifact_facturas_service_ng.dto.response.ResponseSunat;

public interface SendSunatService {

    ResponseSunat sendBillPaymentVoucher(String fileName, String contentFileBase64, String rucEmisor);

}
