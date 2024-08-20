package com.thepapiok.multiplecard.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Constraint(validatedBy = IsCountryValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface IsCountry {
  String message() default "Ten kraj nie jest dozwolony";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
