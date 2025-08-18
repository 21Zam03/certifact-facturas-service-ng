package com.certicom.certifact_facturas_service_ng.validation.validators;

import com.certicom.certifact_facturas_service_ng.dto.request.AnticipoRequest;
import com.certicom.certifact_facturas_service_ng.util.CamposEntrada;
import com.certicom.certifact_facturas_service_ng.validation.anottations.AnticipoValidation;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AnticipoValidator extends CamposEntrada<Object> implements ConstraintValidator<AnticipoValidation, AnticipoRequest> {

    @Override
    public boolean isValid(AnticipoRequest anticipoRequest, ConstraintValidatorContext context) {
        Pair<Boolean, String> resultado;
        resultado = validarSerie(anticipoRequest.getSerieAnticipo());
        if (!resultado.getLeft()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(resultado.getRight())
                    .addPropertyNode("serieAnticipo").addConstraintViolation();
            return resultado.getLeft();
        }
        return true;
    }

    private Pair<Boolean, String> validarSerie(String serie) {
        String mensaje = "";
        if (StringUtils.isBlank(serie)) {
            mensaje = "El campo [" + serieAnticipoLabel + "] es obligatorio.";
            return Pair.of(false, mensaje);
        }
        if (!StringUtils.isAlphanumeric(serie)) {
            mensaje = "El campo [" + serieAnticipoLabel + "] recibe caracteres alfabeticos y numericos.";
            return Pair.of(false, mensaje);
        }
        if (StringUtils.length(serie) != 4) {
            mensaje = "El campo [" + serieAnticipoLabel + "] debe ser alfanumerico de 4 caracteres.";
            return Pair.of(false, mensaje);
        }
        return Pair.of(true, mensaje);
    }

    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        return null;
    }

}
