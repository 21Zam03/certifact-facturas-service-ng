package com.certicom.certifact_facturas_service_ng.templates.template;

import com.certicom.certifact_facturas_service_ng.dto.model.PaymentVoucher;
import com.certicom.certifact_facturas_service_ng.exceptions.TemplateException;
import org.springframework.stereotype.Component;

@Component
public class NotaDebitoTemplate {

    public String construirNotaDebito(PaymentVoucher debitNote) throws TemplateException {
        return "";
    }


}
