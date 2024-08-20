package com.thepapiok.multiplecard.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IsApartmentNumberValidator.class)
public @interface IsApartmentNumber {
  String message() default "Jeżeli podano to długość między 1-5 i same cyfry";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String regexp();

  int min();

  int max();
}
