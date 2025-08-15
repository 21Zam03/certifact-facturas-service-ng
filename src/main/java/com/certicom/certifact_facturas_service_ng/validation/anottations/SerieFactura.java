package com.certicom.certifact_facturas_service_ng.validation.anottations;

import com.certicom.certifact_facturas_service_ng.validation.validators.SerieFacturaValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SerieFacturaValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface SerieFactura {

    String message() default "La serie esta en mal formato";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
