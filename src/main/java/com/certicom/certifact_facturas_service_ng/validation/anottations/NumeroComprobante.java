package com.certicom.certifact_facturas_service_ng.validation.anottations;

import com.certicom.certifact_facturas_service_ng.validation.validators.NumeroComprobanteValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NumeroComprobanteValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface NumeroComprobante {

    String message() default "El numero esta en mal formato";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
