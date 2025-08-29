package com.certicom.certifact_facturas_service_ng.service;

import com.certicom.certifact_facturas_service_ng.dto.model.PaymentVoucher;
import com.certicom.certifact_facturas_service_ng.dto.model.Voided;
import com.certicom.certifact_facturas_service_ng.exceptions.SignedException;
import com.certicom.certifact_facturas_service_ng.exceptions.TemplateException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public interface TemplateService {

    public Map<String, String> buildPaymentVoucherSignOse(PaymentVoucher paymentVoucher);
    public Map<String, String> buildPaymentVoucherSignOseBliz(PaymentVoucher paymentVoucher);
    public Map<String, String> buildPaymentVoucherSignCerti(PaymentVoucher paymentVoucher) throws TemplateException, SignedException, IOException, NoSuchAlgorithmException;
    public Map<String, String> buildPaymentVoucherSign(PaymentVoucher paymentVoucher);

    public Map<String, String> buildVoidedDocumentsSign(Voided voided) throws TemplateException, SignedException, IOException, NoSuchAlgorithmException;
    public Map<String, String> buildVoidedDocumentsSignCerti(Voided voided) throws TemplateException, SignedException, IOException, NoSuchAlgorithmException;

}
