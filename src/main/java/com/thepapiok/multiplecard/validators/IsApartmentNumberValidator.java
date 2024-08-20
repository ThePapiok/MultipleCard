package com.thepapiok.multiplecard.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IsApartmentNumberValidator implements ConstraintValidator<IsApartmentNumber, String> {
  private int min;
  private int max;
  private String regexp;

  @Override
  public void initialize(IsApartmentNumber constraintAnnotation) {
    min = constraintAnnotation.min();
    max = constraintAnnotation.max();
    regexp = constraintAnnotation.regexp();
  }

  @Override
  public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
    if (s.length() == 0) {
      return true;
    }
    return s.matches(regexp) && s.length() >= min && s.length() <= max;
  }
}
