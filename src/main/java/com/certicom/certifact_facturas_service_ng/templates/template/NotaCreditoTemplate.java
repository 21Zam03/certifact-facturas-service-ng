package com.certicom.certifact_facturas_service_ng.templates.template;

import com.certicom.certifact_facturas_service_ng.dto.PaymentVoucherDto;
import com.certicom.certifact_facturas_service_ng.exceptions.TemplateException;
import org.springframework.stereotype.Component;

@Component
public class NotaCreditoTemplate {

    public String construirNotaCredito(PaymentVoucherDto creditNote) throws TemplateException {
        return "";
    }

}
