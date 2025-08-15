package com.certicom.certifact_facturas_service_ng.validation.validators;

import com.certicom.certifact_facturas_service_ng.validation.ConstantesSunat;
import com.certicom.certifact_facturas_service_ng.validation.anottations.TipoComprobanteFactura;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TipoComprobanteFacturaValidator implements ConstraintValidator<TipoComprobanteFactura, String> {

    @Value("${json.payment_voucher.input.tipoComprobante}")
    private String tipoComprobanteEntrada;

    @Override
    public boolean isValid(String tipoComprobante, ConstraintValidatorContext context) {
        if(tipoComprobante == null || tipoComprobante.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "El campo ["+tipoComprobanteEntrada+"] es obligatorio"
            ).addConstraintViolation();
            return false;
        }

        if(!tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "El campo ["+tipoComprobanteEntrada+"] contiene un valor no valido o no es de tipo factura - [01]"
            ).addConstraintViolation();
            return false;
        }
        return true;
    }

}
