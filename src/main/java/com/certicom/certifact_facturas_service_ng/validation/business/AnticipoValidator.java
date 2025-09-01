package com.certicom.certifact_facturas_service_ng.validation.business;

import com.certicom.certifact_facturas_service_ng.dto.others.Anticipo;
import com.certicom.certifact_facturas_service_ng.exceptions.ValidationException;
import com.certicom.certifact_facturas_service_ng.util.CamposEntrada;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;

@Component
public class AnticipoValidator extends CamposEntrada<Object> {

    public void validateAnticipo(Anticipo anticipo) {
        validateSerie(anticipo.getSerieAnticipo());
        validateNumero(anticipo.getNumeroAnticipo());
        validateMontoAnticipado(anticipo.getMontoAnticipo());
        validateTipoDocumentoAnticipo(anticipo.getTipoDocAnticipo());
    }

    private void validateTipoDocumentoAnticipo(String tipoDocumento) {
        if (StringUtils.isBlank(tipoDocumento)) {
            throw new ValidationException("El campo [" + tipoDocumentoAnticipoLabel + "] es obligatorio.");
        }
    }

    private void validateMontoAnticipado(BigDecimal montoAnticipado) {
        if (montoAnticipado == null) {
            throw new ValidationException("El campo [" + montoAnticipadoLabel + "] es obligatorio.");
        }
    }

    private void validateNumero(Integer numero) {
        if (numero == null) {
            throw new ValidationException("El campo [" + numeroAnticipoLabel + "] es obligatorio.");
        }
        if (numero < 1) {
            throw new ValidationException("El campo [" + numeroAnticipoLabel + "] debe ser mayor que cero.");
        }
        if (StringUtils.length(numero.toString()) > 8) {
            throw new ValidationException("El campo [" + numeroAnticipoLabel + "] debe tener como maximo 8 digitos.");
        }
    }

    private void validateSerie(String serie) {
        if (StringUtils.isBlank(serie)) {
            throw new ValidationException("El campo [" + serieAnticipoLabel + "] es obligatorio.");
        }
        if (!StringUtils.isAlphanumeric(serie)) {
            throw new ValidationException("El campo [" + serieAnticipoLabel + "] recibe caracteres alfabeticos y numericos.");
        }
        if (StringUtils.length(serie) != 4) {
            throw new ValidationException("El campo [" + serieAnticipoLabel + "] debe ser alfanumerico de 4 caracteres.");
        }
    }


    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        return null;
    }
}
