package com.certicom.certifact_facturas_service_ng.validation;

import com.certicom.certifact_facturas_service_ng.feign.ComprobanteFeign;
import com.certicom.certifact_facturas_service_ng.util.ConstantesParametro;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RucActivoValidator implements ConstraintValidator<RucActivo, String> {

    @Value("${json.payment_voucher.input.rucEmisor}")
    private String rucEmisorEntrada;

    private final ComprobanteFeign comprobanteFeign;

    @Override
    public boolean isValid(String rucEmisor, ConstraintValidatorContext context) {
        if(rucEmisor == null || rucEmisor.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "El campo ["+rucEmisorEntrada+"] no puede ser null o vacio"
            ).addConstraintViolation();
            return false;
        }

        String estado = comprobanteFeign.obtenerEstadoEmpresaPorRuc(rucEmisor);

        if(estado == null || estado.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "El estado del cliente con el ruc=["+rucEmisor+"] no existe"
            ).addConstraintViolation();
            return false;
        }

        boolean esActivo = ConstantesParametro.REGISTRO_ACTIVO.equals(estado);
        if(!esActivo) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "El RUC emisor [" + rucEmisor +"] no se encuentra habilitado para ejecutar operaciones al API-REST."
            ).addConstraintViolation();
        }
        return esActivo;
    }

}
