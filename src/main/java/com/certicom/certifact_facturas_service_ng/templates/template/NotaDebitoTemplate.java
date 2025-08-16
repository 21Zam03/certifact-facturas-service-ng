package com.certicom.certifact_facturas_service_ng.templates.template;

import com.certicom.certifact_facturas_service_ng.dto.model.ComprobanteDto;
import com.certicom.certifact_facturas_service_ng.exceptions.PlantillaException;
import org.springframework.stereotype.Component;

@Component
public class NotaDebitoTemplate {

    public String construirNotaDebito(ComprobanteDto debitNote) throws PlantillaException {
        return "";
    }


}
