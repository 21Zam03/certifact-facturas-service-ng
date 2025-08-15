package com.certicom.certifact_facturas_service_ng.validation.validators;

import com.certicom.certifact_facturas_service_ng.validation.anottations.SerieFactura;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SerieFacturaValidator implements ConstraintValidator<SerieFactura, String> {

    @Value("${json.payment_voucher.input.serie}")
    private String serieEntrada;

    @Override
    public boolean isValid(String serie, ConstraintValidatorContext context) {
        if(StringUtils.isBlank(serie)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "El campo ["+serieEntrada+"] es obligatorio."
            ).addConstraintViolation();
            return false;
        }

        if (!StringUtils.isAlphanumeric(serie)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "El campo ["+serieEntrada+"] recibe caracteres del alfabeto y números."
            ).addConstraintViolation();
            return false;
        }

        if (StringUtils.length(serie) != 4) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "El campo ["+serieEntrada+"] debe ser alfanumerico de 4 caracteres."
            ).addConstraintViolation();
            return false;
        }

        String primeraLetra = StringUtils.substring(serie, 0, 1).toUpperCase();
        if(!primeraLetra.equals("F") && !StringUtils.isNumeric(serie)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "El campo ["+serieEntrada+"] puede ser numerico o empezar con el caracter F."
            ).addConstraintViolation();
            return false;
        }
        return true;
    }

}
