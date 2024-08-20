package com.thepapiok.multiplecard.validators;

import com.thepapiok.multiplecard.collections.Country;
import com.thepapiok.multiplecard.services.CountryService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class IsCountryValidator implements ConstraintValidator<IsCountry, String> {

  private final CountryService countryService;

  @Autowired
  public IsCountryValidator(CountryService countryService) {
    this.countryService = countryService;
  }

  @Override
  public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
    List<String> names = countryService.getAll().stream().map(Country::getName).toList();
    return names.contains(s);
  }
}
