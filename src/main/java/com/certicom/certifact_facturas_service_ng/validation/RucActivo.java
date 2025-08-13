package com.certicom.certifact_facturas_service_ng.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RucActivoValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface RucActivo {

    String message() default "El RUC no se encuentra habilitado para ejecutar operaciones al API-REST.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
