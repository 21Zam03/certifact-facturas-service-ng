package com.certicom.certifact_facturas_service_ng.validation.anottations;

import com.certicom.certifact_facturas_service_ng.validation.validators.TipoComprobanteFacturaValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TipoComprobanteFacturaValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface TipoComprobanteFactura {

    String message() default "El Tipo de comprobante es diferente de 01 - factura";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
