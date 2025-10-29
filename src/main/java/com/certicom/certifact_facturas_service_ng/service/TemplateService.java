package com.certicom.certifact_facturas_service_ng.service;

import com.certicom.certifact_facturas_service_ng.dto.PaymentVoucherDto;
import com.certicom.certifact_facturas_service_ng.dto.others.Voided;
import com.certicom.certifact_facturas_service_ng.exceptions.SignedException;
import com.certicom.certifact_facturas_service_ng.exceptions.TemplateException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public interface TemplateService {

    public Map<String, String> buildPaymentVoucherSignOse(PaymentVoucherDto paymentVoucherDto);
    public Map<String, String> buildPaymentVoucherSignOseBliz(PaymentVoucherDto paymentVoucherDto);
    public Map<String, String> buildPaymentVoucherSignCerti(PaymentVoucherDto paymentVoucherDto) throws TemplateException, SignedException, IOException, NoSuchAlgorithmException;
    public Map<String, String> buildPaymentVoucherSign(PaymentVoucherDto paymentVoucherDto);

    public Map<String, String> buildVoidedDocumentsSign(Voided voided) throws TemplateException, SignedException, IOException, NoSuchAlgorithmException;
    public Map<String, String> buildVoidedDocumentsSignCerti(Voided voided) throws TemplateException, SignedException, IOException, NoSuchAlgorithmException;

}
