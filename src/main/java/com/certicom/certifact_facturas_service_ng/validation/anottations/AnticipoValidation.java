package com.certicom.certifact_facturas_service_ng.validation.anottations;

import com.certicom.certifact_facturas_service_ng.validation.validators.AnticipoValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AnticipoValidator.class)
@Documented
public @interface AnticipoValidation {

    String message() default "El anticipo tiene campos mal formateados";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
