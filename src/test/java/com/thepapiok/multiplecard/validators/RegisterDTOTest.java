package com.thepapiok.multiplecard.validators;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.thepapiok.multiplecard.dto.RegisterDTO;
import jakarta.validation.*;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class RegisterDTOTest {
  private static ValidatorFactory validatorFactory;
  private static Validator validator;

  @BeforeAll
  public static void setUp() {
    validatorFactory = Validation.buildDefaultValidatorFactory();
    validator = validatorFactory.getValidator();
  }

  @AfterAll
  public static void close() {
    validatorFactory.close();
  }

  @Test
  public void shouldSuccessAtValidationFirstName() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setFirstName("Test");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, "firstName");

    assertTrue(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationFirstNameWhenNoStartWithUpperCase() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setFirstName("test");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, "firstName");

    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationFirstNameWhenContainsDigits() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setFirstName("Test1");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, "firstName");

    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationFirstNameWhenContainsSpecialSymbol() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setFirstName("Test!");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, "firstName");

    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationFirstNameWhenContainsTwoParts() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setFirstName("Test Test");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, "firstName");

    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldSuccessAtValidationLastNameCityAndStreet() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setLastName("Test");
    registerDTO.setCity("Test");
    registerDTO.setStreet("Test");
    Set<ConstraintViolation<RegisterDTO>> violations;

    violations = validator.validateProperty(registerDTO, "lastName");
    assertTrue(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, "city");
    assertTrue(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, "street");
    assertTrue(violations.isEmpty());
  }

  @Test
  public void shouldSuccessAtValidationLastNameCityAndStreetWithTwoParts1() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setLastName("Test Test");
    registerDTO.setCity("Test Test");
    registerDTO.setStreet("Test Test");
    Set<ConstraintViolation<RegisterDTO>> violations;

    violations = validator.validateProperty(registerDTO, "lastName");
    assertTrue(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, "city");
    assertTrue(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, "street");
    assertTrue(violations.isEmpty());
  }

  @Test
  public void shouldSuccessAtValidationLastNameCityAndStreetWithTwoParts2() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setLastName("Test-Test");
    registerDTO.setCity("Test-Test");
    registerDTO.setStreet("Test-Test");
    Set<ConstraintViolation<RegisterDTO>> violations;

    violations = validator.validateProperty(registerDTO, "lastName");
    assertTrue(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, "city");
    assertTrue(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, "street");
    assertTrue(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationLastNameCityAndStreetWhenNoStartWithUpperCase() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setLastName("test");
    registerDTO.setLastName("test");
    registerDTO.setLastName("test");
    Set<ConstraintViolation<RegisterDTO>> violations;

    violations = validator.validateProperty(registerDTO, "lastName");
    assertFalse(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, "city");
    assertFalse(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, "street");
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationLastNameCityAndStreetWhenContainsDigits() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setLastName("Te1st");
    registerDTO.setCity("Te1st");
    registerDTO.setStreet("Te1st");
    Set<ConstraintViolation<RegisterDTO>> violations;

    violations = validator.validateProperty(registerDTO, "lastName");
    assertFalse(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, "city");
    assertFalse(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, "street");
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationLastNameCityAndStreetWhenContainsSpecialSymbols() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setLastName("Te@st");
    registerDTO.setCity("Te@st");
    registerDTO.setStreet("Te@st");
    Set<ConstraintViolation<RegisterDTO>> violations;

    violations = validator.validateProperty(registerDTO, "lastName");
    assertFalse(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, "city");
    assertFalse(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, "street");
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationLastNameCityAndStreetWhenSecondPartNoStartWithUpperCase1() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setLastName("Test test");
    registerDTO.setCity("Test test");
    registerDTO.setStreet("Test test");
    Set<ConstraintViolation<RegisterDTO>> violations;

    violations = validator.validateProperty(registerDTO, "lastName");
    assertFalse(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, "city");
    assertFalse(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, "street");
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationLastNameCityAndStreetWhenSecondPartNoStartWithUpperCase2() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setLastName("Test-test");
    registerDTO.setCity("Test-test");
    registerDTO.setStreet("Test-test");
    Set<ConstraintViolation<RegisterDTO>> violations;

    violations = validator.validateProperty(registerDTO, "lastName");
    assertFalse(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, "city");
    assertFalse(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, "street");
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationLastNameCityAndStreetWhenSecondPartContainsDigits1() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setLastName("Test Te1st");
    registerDTO.setCity("Test Te1st");
    registerDTO.setStreet("Test Te1st");
    Set<ConstraintViolation<RegisterDTO>> violations;

    violations = validator.validateProperty(registerDTO, "lastName");
    assertFalse(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, "city");
    assertFalse(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, "street");
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationLastNameCityAndStreetWhenSecondPartContainsDigits2() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setLastName("Test-Te1st");
    registerDTO.setCity("Test-Te1st");
    registerDTO.setStreet("Test-Te1st");
    Set<ConstraintViolation<RegisterDTO>> violations;

    violations = validator.validateProperty(registerDTO, "lastName");
    assertFalse(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, "city");
    assertFalse(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, "street");
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationLastNameCityAndStreetWhenSecondPartContainsSpecialSymbols1() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setLastName("Test Te!st");
    registerDTO.setCity("Test Te!st");
    registerDTO.setStreet("Test Te!st");
    Set<ConstraintViolation<RegisterDTO>> violations;

    violations = validator.validateProperty(registerDTO, "lastName");
    assertFalse(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, "city");
    assertFalse(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, "street");
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationLastNameCityAndStreetWhenSecondPartContainsSpecialSymbols2() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setLastName("Test-Te!st");
    registerDTO.setCity("Test-Te!st");
    registerDTO.setStreet("Test-Te!st");
    Set<ConstraintViolation<RegisterDTO>> violations;

    violations = validator.validateProperty(registerDTO, "lastName");
    assertFalse(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, "city");
    assertFalse(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, "street");
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldSuccessAtValidationHouseNumber1() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setHouseNumber("1");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, "houseNumber");

    assertTrue(violations.isEmpty());
  }

  @Test
  public void shouldSuccessAtValidationHouseNumber2() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setHouseNumber("1B");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, "houseNumber");

    assertTrue(violations.isEmpty());
  }

  @Test
  public void shouldSuccessAtValidationHouseNumber3() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setHouseNumber("1/2");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, "houseNumber");

    assertTrue(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationHouseNumberWhenLetterIsNotAtEnd() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setHouseNumber("B1");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, "houseNumber");

    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationHouseNumberWhenContainsSpecialSymbols() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setHouseNumber("1!");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, "houseNumber");

    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationHouseNumberWhenContainsSlashButNoNumberAtEnd1() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setHouseNumber("1/");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, "houseNumber");

    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationHouseNumberWhenContainsSlashButNoNumberAtEnd2() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setHouseNumber("1//");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, "houseNumber");

    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationHouseNumberWhenContainsSlashButNoNumberAtEnd3() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setHouseNumber("1/!");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, "houseNumber");

    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationHouseNumberWhenContainsSlashButNoNumberAtEnd4() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setHouseNumber("1/B");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, "houseNumber");

    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationHouseNumberWhenContainsMoreThanTwoParts1() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setHouseNumber("1/111/11");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, "houseNumber");

    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationHouseNumberWhenContainsMoreThanTwoParts2() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setHouseNumber("1B111C");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, "houseNumber");

    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldSuccessAtValidationApartmentNumber1() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setApartmentNumber("156");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, "apartmentNumber");

    assertTrue(violations.isEmpty());
  }

  @Test
  public void shouldSuccessAtValidationApartmentNumber2() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setApartmentNumber("");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, "apartmentNumber");

    assertTrue(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationApartmentNumberWhenStartWith0() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setApartmentNumber("01");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, "apartmentNumber");

    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationApartmentNumberWhenContainsLetters() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setApartmentNumber("1B");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, "apartmentNumber");

    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationApartmentNumberWhenContainsSpecialSymbols() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setApartmentNumber("1!");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, "apartmentNumber");

    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationApartmentNumberWhenMoreThanOnePart() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setApartmentNumber("1 1");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, "apartmentNumber");

    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldSuccessAtValidationPostalCode() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPostalCode("01-432");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, "postalCode");

    assertTrue(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationPostalCodeWhenNotContainsMinusAt3Char1() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPostalCode("01 432");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, "postalCode");

    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationPostalCodeWhenNotContainsMinusAt3Char2() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPostalCode("011432");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, "postalCode");

    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationPostalCodeWhenNotContainsMinusAt3Char3() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPostalCode("0-1432");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, "postalCode");

    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationPostalCodeWhenContainsLetters() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPostalCode("01-B32");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, "postalCode");

    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationPostalCodeWhenContainsSpecialSymbols() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPostalCode("01-!32");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, "postalCode");

    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationPostalCodeWhenToLessSize() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPostalCode("01-32");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, "postalCode");

    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldSuccessAtValidationPhone1() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPhone("123123123");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, "phone");

    assertTrue(violations.isEmpty());
  }

  @Test
  public void shouldSuccessAtValidationPhone2() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPhone("1 2 3 1 2 3123");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, "phone");

    assertTrue(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationPhoneWhenContainsLetters() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPhone("123123123B");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, "phone");

    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationPhoneWhenContainsSpecialSymbols() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPhone("123123123!");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, "phone");

    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationPhoneWhenStartWith0() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPhone("0123123123");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, "phone");

    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldSuccessAtValidationPasswordAndRetypedPassword() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPassword("Test123!");
    registerDTO.setRetypedPassword("Test123!");
    Set<ConstraintViolation<RegisterDTO>> violations;

    violations = validator.validateProperty(registerDTO, "password");
    assertTrue(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, "retypedPassword");
    assertTrue(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationPasswordAndRetypedPasswordWhenNoContainsLowerCaseLetter() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPassword("TTTT123!");
    registerDTO.setRetypedPassword("TTTT123!");
    Set<ConstraintViolation<RegisterDTO>> violations;

    violations = validator.validateProperty(registerDTO, "password");
    assertFalse(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, "retypedPassword");
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationPasswordAndRetypedPasswordWhenNoContainsUpperCaseLetter() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPassword("tttt123!");
    registerDTO.setRetypedPassword("tttt123!");
    Set<ConstraintViolation<RegisterDTO>> violations;

    violations = validator.validateProperty(registerDTO, "password");
    assertFalse(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, "retypedPassword");
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationPasswordAndRetypedPasswordWhenNoContainsDigit() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPassword("Ttttttt!");
    registerDTO.setRetypedPassword("Ttttttt!");
    Set<ConstraintViolation<RegisterDTO>> violations;

    violations = validator.validateProperty(registerDTO, "password");
    assertFalse(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, "retypedPassword");
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationPasswordAndRetypedPasswordWhenNoContainsSpecialSymbol() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPassword("Ttttttt1");
    registerDTO.setRetypedPassword("Ttttttt1");
    Set<ConstraintViolation<RegisterDTO>> violations;

    violations = validator.validateProperty(registerDTO, "password");
    assertFalse(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, "retypedPassword");
    assertFalse(violations.isEmpty());
  }
}
