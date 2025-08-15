package com.certicom.certifact_facturas_service_ng.validation.validators;

import com.certicom.certifact_facturas_service_ng.validation.anottations.NumeroComprobante;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NumeroComprobanteValidator implements ConstraintValidator<NumeroComprobante, Integer> {

    @Value("${json.payment_voucher.input.numero}")
    private String numeroEntrada;

    @Override
    public boolean isValid(Integer numero, ConstraintValidatorContext context) {
        if (numero == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "El campo ["+numeroEntrada+"] es obligatorio."
            ).addConstraintViolation();
            return false;
        }
        if (numero < 1) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "El campo ["+numeroEntrada+"] debe ser mayor que cero."
            ).addConstraintViolation();
            return false;
        }
        if (StringUtils.length(numero.toString()) > 8) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "El campo ["+numeroEntrada+"] debe tener como maximo 8 digitos."
            ).addConstraintViolation();
            return false;
        }
        return true;
    }
}
