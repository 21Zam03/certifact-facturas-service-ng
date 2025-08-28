package com.certicom.certifact_facturas_service_ng.validation.business;

import com.certicom.certifact_facturas_service_ng.dto.others.VoucherAnnular;
import com.certicom.certifact_facturas_service_ng.util.CamposEntrada;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class VoucherAnnularValidator extends CamposEntrada<Object> {

    public void validateVoucherAnnular(List<VoucherAnnular> documentos, String rucEmisor) {

    }

    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        return null;
    }


}
