package com.certicom.certifact_facturas_service_ng.util;

import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.beans.factory.annotation.Value;

public abstract class CamposEntrada<T> extends JsonDeserializer<T> {

    @Value("${json.payment_voucher.input.tipoComprobante}")
    protected String tipoComprobanteLabel;

}
