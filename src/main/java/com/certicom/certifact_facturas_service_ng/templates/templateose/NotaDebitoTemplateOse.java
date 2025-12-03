package com.certicom.certifact_facturas_service_ng.templates.templateose;

import com.certicom.certifact_facturas_service_ng.dto.PaymentVoucherDto;
import com.certicom.certifact_facturas_service_ng.dto.others.*;
import com.certicom.certifact_facturas_service_ng.enums.AfectacionIgvEnum;
import com.certicom.certifact_facturas_service_ng.exceptions.TemplateException;
import com.certicom.certifact_facturas_service_ng.templates.sunat.TemplateSunat;
import com.certicom.certifact_facturas_service_ng.util.UtilFormat;
import com.certicom.certifact_facturas_service_ng.util.UtilGenerateLetraNumber;
import com.certicom.certifact_facturas_service_ng.validation.ConstantesSunat;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.certicom.certifact_facturas_service_ng.util.UtilXml.appendChild;
import static com.certicom.certifact_facturas_service_ng.util.UtilXml.formatXML;

@Component
public class NotaDebitoTemplateOse extends TemplateSunat {

    HashMap<Integer,String> mapTransaccion = new HashMap<Integer, String>();
    private static final String CUOTA0 = "Cuota000";

    public NotaDebitoTemplateOse() {
        this.mapTransaccion.put(1,"Contado");
        this.mapTransaccion.put(2,"Credito");
    }

    public String buildDebitNote(PaymentVoucherDto creditNote) throws TemplateException {
        return null;
    }

}
