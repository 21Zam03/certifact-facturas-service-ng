package com.certicom.certifact_facturas_service_ng.service;

import com.certicom.certifact_facturas_service_ng.model.PaymentVoucherModel;
import com.certicom.certifact_facturas_service_ng.dto.others.Voided;
import com.certicom.certifact_facturas_service_ng.exceptions.SignedException;
import com.certicom.certifact_facturas_service_ng.exceptions.TemplateException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public interface TemplateService {

    public Map<String, String> buildPaymentVoucherSignOse(PaymentVoucherModel paymentVoucherModel);
    public Map<String, String> buildPaymentVoucherSignOseBliz(PaymentVoucherModel paymentVoucherModel);
    public Map<String, String> buildPaymentVoucherSignCerti(PaymentVoucherModel paymentVoucherModel) throws TemplateException, SignedException, IOException, NoSuchAlgorithmException;
    public Map<String, String> buildPaymentVoucherSign(PaymentVoucherModel paymentVoucherModel);

    public Map<String, String> buildVoidedDocumentsSign(Voided voided) throws TemplateException, SignedException, IOException, NoSuchAlgorithmException;
    public Map<String, String> buildVoidedDocumentsSignCerti(Voided voided) throws TemplateException, SignedException, IOException, NoSuchAlgorithmException;

}
