package com.certicom.certifact_facturas_service_ng.service;

import com.certicom.certifact_facturas_service_ng.dto.model.PaymentVoucherDto;
import com.certicom.certifact_facturas_service_ng.exceptions.SignedException;
import com.certicom.certifact_facturas_service_ng.exceptions.TemplateException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public interface PlantillaService {

    public Map<String, String> buildPaymentVoucherSignOse(PaymentVoucherDto paymentVoucherDto);
    public Map<String, String> buildPaymentVoucherSignOseBliz(PaymentVoucherDto paymentVoucherDto);
    public Map<String, String> buildPaymentVoucherSignCerti(PaymentVoucherDto paymentVoucherDto) throws TemplateException, SignedException, IOException, NoSuchAlgorithmException;
    public Map<String, String> buildPaymentVoucherSign(PaymentVoucherDto paymentVoucherDto);

}
