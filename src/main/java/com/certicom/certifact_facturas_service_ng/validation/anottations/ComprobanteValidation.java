package com.certicom.certifact_facturas_service_ng.validation.anottations;

import com.certicom.certifact_facturas_service_ng.validation.validators.ComprobanteValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ComprobanteValidator.class)
@Documented
public @interface ComprobanteValidation {

    String message() default "El comprobante tiene campos mal formateados";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
