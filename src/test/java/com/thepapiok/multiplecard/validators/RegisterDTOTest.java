package com.thepapiok.multiplecard.validators;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.thepapiok.multiplecard.dto.RegisterDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class RegisterDTOTest {
  private static final String TEST_TEXT = "Test";
  private static final String TEST_TEXT_TWO_PARTS = "Test Test";
  private static final String TEST_TEXT_LOWER_CASE = "test";
  private static final String TEST_HOUSE_APARTMENT_NUMBER_WITH_LETTER = "1B";
  private static final String TEST_HOUSE_APARTMENT_NUMBER_WITH_SPECIAL_SYMBOL = "1!";

  private static final String PASSWORD_FIELD = "password";
  private static final String RETYPED_PASSWORD_FIELD = "retypedPassword";
  private static final String EMAIL_FIELD = "email";
  private static final String PHONE_FIELD = "phone";
  private static final String POSTAL_CODE_FIELD = "postalCode";
  private static final String APARTMENT_NUMBER_FIELD = "apartmentNumber";
  private static final String HOUSE_NUMBER_FIELD = "houseNumber";
  private static final String PROVINCE_FIELD = "province";
  private static final String STREET_FIELD = "street";
  private static final String CITY_FIELD = "city";
  private static final String LAST_NAME_FIELD = "lastName";
  private static final String FIRST_NAME_FIELD = "firstName";

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
    registerDTO.setFirstName(TEST_TEXT);
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, FIRST_NAME_FIELD);

    assertTrue(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationFirstNameWhenNoStartWithUpperCase() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setFirstName(TEST_TEXT_LOWER_CASE);

    validationFalseFirstName(registerDTO);
  }

  @Test
  public void shouldFailAtValidationFirstNameWhenContainsDigits() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setFirstName("Test1");

    validationFalseFirstName(registerDTO);
  }

  @Test
  public void shouldFailAtValidationFirstNameWhenContainsSpecialSymbol() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setFirstName("Test!");

    validationFalseFirstName(registerDTO);
  }

  @Test
  public void shouldFailAtValidationFirstNameWhenContainsTwoParts() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setFirstName(TEST_TEXT_TWO_PARTS);

    validationFalseFirstName(registerDTO);
  }

  private void validationFalseFirstName(RegisterDTO registerDTO) {
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, FIRST_NAME_FIELD);

    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldSuccessAtValidationLastNameCityStreetAndProvince() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setLastName(TEST_TEXT);
    registerDTO.setCity(TEST_TEXT);
    registerDTO.setStreet(TEST_TEXT);
    registerDTO.setProvince(TEST_TEXT);

    validationTrueLastNameCityStreetAndProvince(registerDTO);
  }

  @Test
  public void shouldSuccessAtValidationLastNameCityStreetAndProvinceWithTwoParts1() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setLastName(TEST_TEXT_TWO_PARTS);
    registerDTO.setCity(TEST_TEXT_TWO_PARTS);
    registerDTO.setStreet(TEST_TEXT_TWO_PARTS);
    registerDTO.setProvince(TEST_TEXT_TWO_PARTS);

    validationTrueLastNameCityStreetAndProvince(registerDTO);
  }

  @Test
  public void shouldSuccessAtValidationLastNameCityStreetAndProvinceWithTwoParts2() {
    final String testText = "Test-Test";
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setLastName(testText);
    registerDTO.setCity(testText);
    registerDTO.setStreet(testText);
    registerDTO.setProvince(testText);

    validationTrueLastNameCityStreetAndProvince(registerDTO);
  }

  private void validationTrueLastNameCityStreetAndProvince(RegisterDTO registerDTO) {
    Set<ConstraintViolation<RegisterDTO>> violations;

    violations = validator.validateProperty(registerDTO, LAST_NAME_FIELD);
    assertTrue(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, CITY_FIELD);
    assertTrue(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, STREET_FIELD);
    assertTrue(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, PROVINCE_FIELD);
    assertTrue(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationLastNameCityStreetAndProvinceWhenNoStartWithUpperCase() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setLastName(TEST_TEXT_LOWER_CASE);
    registerDTO.setCity(TEST_TEXT_LOWER_CASE);
    registerDTO.setStreet(TEST_TEXT_LOWER_CASE);
    registerDTO.setProvince(TEST_TEXT_LOWER_CASE);

    validationFalseLastNameCityStreetAndProvince(registerDTO);
  }

  @Test
  public void shouldFailAtValidationLastNameCityStreetAndProvinceWhenContainsDigits() {
    final String testText = "Te1st";
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setLastName(testText);
    registerDTO.setCity(testText);
    registerDTO.setStreet(testText);
    registerDTO.setProvince(testText);

    validationFalseLastNameCityStreetAndProvince(registerDTO);
  }

  @Test
  public void shouldFailAtValidationLastNameCityStreetAndProvinceWhenContainsSpecialSymbols() {
    final String testText = "Te@st";
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setLastName(testText);
    registerDTO.setCity(testText);
    registerDTO.setStreet(testText);
    registerDTO.setProvince(testText);

    validationFalseLastNameCityStreetAndProvince(registerDTO);
  }

  @Test
  public void
      shouldFailAtValidationLastNameCityStreetAndProvinceWhenSecondPartNoStartWithUpperCase1() {
    final String testText = "Test test";
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setLastName(testText);
    registerDTO.setCity(testText);
    registerDTO.setStreet(testText);
    registerDTO.setProvince(testText);

    validationFalseLastNameCityStreetAndProvince(registerDTO);
  }

  @Test
  public void
      shouldFailAtValidationLastNameCityStreetAndProvinceWhenSecondPartNoStartWithUpperCase2() {
    final String testText = "Test-test";
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setLastName(testText);
    registerDTO.setCity(testText);
    registerDTO.setStreet(testText);
    registerDTO.setProvince(testText);

    validationFalseLastNameCityStreetAndProvince(registerDTO);
  }

  @Test
  public void shouldFailAtValidationLastNameCityStreetAndProvinceWhenSecondPartContainsDigits1() {
    final String testText = "Test Te1st";
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setLastName(testText);
    registerDTO.setCity(testText);
    registerDTO.setStreet(testText);
    registerDTO.setProvince(testText);

    validationFalseLastNameCityStreetAndProvince(registerDTO);
  }

  @Test
  public void shouldFailAtValidationLastNameCityStreetAndProvinceWhenSecondPartContainsDigits2() {
    final String testText = "Test-Te1st";
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setLastName(testText);
    registerDTO.setCity(testText);
    registerDTO.setStreet(testText);
    registerDTO.setProvince(testText);

    validationFalseLastNameCityStreetAndProvince(registerDTO);
  }

  @Test
  public void
      shouldFailAtValidationLastNameCityStreetAndProvinceWhenSecondPartContainsSpecialSymbols1() {
    final String testText = "Test Te!st";
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setLastName(testText);
    registerDTO.setCity(testText);
    registerDTO.setStreet(testText);
    registerDTO.setProvince(testText);

    validationFalseLastNameCityStreetAndProvince(registerDTO);
  }

  @Test
  public void
      shouldFailAtValidationLastNameCityStreetAndProvinceWhenSecondPartContainsSpecialSymbols2() {
    final String testText = "Test-Te!st";
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setLastName(testText);
    registerDTO.setCity(testText);
    registerDTO.setStreet(testText);
    registerDTO.setProvince(testText);

    validationFalseLastNameCityStreetAndProvince(registerDTO);
  }

  private void validationFalseLastNameCityStreetAndProvince(RegisterDTO registerDTO) {
    Set<ConstraintViolation<RegisterDTO>> violations;
    violations = validator.validateProperty(registerDTO, LAST_NAME_FIELD);
    assertFalse(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, CITY_FIELD);
    assertFalse(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, STREET_FIELD);
    assertFalse(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, PROVINCE_FIELD);
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldSuccessAtValidationHouseNumber1() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setHouseNumber("1");

    validationTrueHouseNumber(registerDTO);
  }

  @Test
  public void shouldSuccessAtValidationHouseNumber2() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setHouseNumber(TEST_HOUSE_APARTMENT_NUMBER_WITH_LETTER);

    validationTrueHouseNumber(registerDTO);
  }

  @Test
  public void shouldSuccessAtValidationHouseNumber3() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setHouseNumber("1/2");

    validationTrueHouseNumber(registerDTO);
  }

  public void validationTrueHouseNumber(RegisterDTO registerDTO) {
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, HOUSE_NUMBER_FIELD);
    assertTrue(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationHouseNumberWhenLetterIsNotAtEnd() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setHouseNumber("B1");

    validationFalseHouseNumber(registerDTO);
  }

  @Test
  public void shouldFailAtValidationHouseNumberWhenContainsSpecialSymbols() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setHouseNumber(TEST_HOUSE_APARTMENT_NUMBER_WITH_SPECIAL_SYMBOL);

    validationFalseHouseNumber(registerDTO);
  }

  @Test
  public void shouldFailAtValidationHouseNumberWhenContainsSlashButNoNumberAtEnd1() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setHouseNumber("1/");

    validationFalseHouseNumber(registerDTO);
  }

  @Test
  public void shouldFailAtValidationHouseNumberWhenContainsSlashButNoNumberAtEnd2() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setHouseNumber("1//");

    validationFalseHouseNumber(registerDTO);
  }

  @Test
  public void shouldFailAtValidationHouseNumberWhenContainsSlashButNoNumberAtEnd3() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setHouseNumber("1/!");

    validationFalseHouseNumber(registerDTO);
  }

  @Test
  public void shouldFailAtValidationHouseNumberWhenContainsSlashButNoNumberAtEnd4() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setHouseNumber("1/B");

    validationFalseHouseNumber(registerDTO);
  }

  @Test
  public void shouldFailAtValidationHouseNumberWhenContainsMoreThanTwoParts1() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setHouseNumber("1/111/11");

    validationFalseHouseNumber(registerDTO);
  }

  @Test
  public void shouldFailAtValidationHouseNumberWhenContainsMoreThanTwoParts2() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setHouseNumber("1B111C");

    validationFalseHouseNumber(registerDTO);
  }

  public void validationFalseHouseNumber(RegisterDTO registerDTO) {
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, HOUSE_NUMBER_FIELD);
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldSuccessAtValidationApartmentNumber1() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setApartmentNumber("156");

    validationTrueApartmentNumber(registerDTO);
  }

  @Test
  public void shouldSuccessAtValidationApartmentNumber2() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setApartmentNumber("");

    validationTrueApartmentNumber(registerDTO);
  }

  public void validationTrueApartmentNumber(RegisterDTO registerDTO) {
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, APARTMENT_NUMBER_FIELD);
    assertTrue(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationApartmentNumberWhenStartWith0() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setApartmentNumber("01");

    validationFalseApartmentNumber(registerDTO);
  }

  @Test
  public void shouldFailAtValidationApartmentNumberWhenContainsLetters() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setApartmentNumber(TEST_HOUSE_APARTMENT_NUMBER_WITH_LETTER);

    validationFalseApartmentNumber(registerDTO);
  }

  @Test
  public void shouldFailAtValidationApartmentNumberWhenContainsSpecialSymbols() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setApartmentNumber(TEST_HOUSE_APARTMENT_NUMBER_WITH_SPECIAL_SYMBOL);

    validationFalseApartmentNumber(registerDTO);
  }

  @Test
  public void shouldFailAtValidationApartmentNumberWhenMoreThanOnePart() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setApartmentNumber("1 1");

    validationFalseApartmentNumber(registerDTO);
  }

  public void validationFalseApartmentNumber(RegisterDTO registerDTO) {
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, APARTMENT_NUMBER_FIELD);
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldSuccessAtValidationPostalCode() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPostalCode("01-432");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, POSTAL_CODE_FIELD);

    assertTrue(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationPostalCodeWhenNotContainsMinusAt3Char1() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPostalCode("01 432");

    validationFalsePostalCode(registerDTO);
  }

  @Test
  public void shouldFailAtValidationPostalCodeWhenNotContainsMinusAt3Char2() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPostalCode("011432");

    validationFalsePostalCode(registerDTO);
  }

  @Test
  public void shouldFailAtValidationPostalCodeWhenNotContainsMinusAt3Char3() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPostalCode("0-1432");

    validationFalsePostalCode(registerDTO);
  }

  @Test
  public void shouldFailAtValidationPostalCodeWhenContainsLetters() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPostalCode("01-B32");

    validationFalsePostalCode(registerDTO);
  }

  @Test
  public void shouldFailAtValidationPostalCodeWhenContainsSpecialSymbols() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPostalCode("01-!32");

    validationFalsePostalCode(registerDTO);
  }

  @Test
  public void shouldFailAtValidationPostalCodeWhenToLessSize() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPostalCode("01-32");

    validationFalsePostalCode(registerDTO);
  }

  public void validationFalsePostalCode(RegisterDTO registerDTO) {
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, POSTAL_CODE_FIELD);
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldSuccessAtValidationPhone1() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPhone("123123123");

    validationTruePhone(registerDTO);
  }

  @Test
  public void shouldSuccessAtValidationPhone2() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPhone("1 2 3 1 2 3123");

    validationTruePhone(registerDTO);
  }

  public void validationTruePhone(RegisterDTO registerDTO) {
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, PHONE_FIELD);
    assertTrue(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationPhoneWhenContainsLetters() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPhone("123123123B");

    validationFalsePhone(registerDTO);
  }

  @Test
  public void shouldFailAtValidationPhoneWhenContainsSpecialSymbols() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPhone("123123123!");

    validationFalsePhone(registerDTO);
  }

  @Test
  public void shouldFailAtValidationPhoneWhenStartWith0() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPhone("0123123123");

    validationFalsePhone(registerDTO);
  }

  public void validationFalsePhone(RegisterDTO registerDTO) {
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, PHONE_FIELD);
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldSuccessAtValidationPasswordAndRetypedPassword() {
    final String testPassword = "Test123!";
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPassword(testPassword);
    registerDTO.setRetypedPassword(testPassword);
    Set<ConstraintViolation<RegisterDTO>> violations;

    violations = validator.validateProperty(registerDTO, PASSWORD_FIELD);
    assertTrue(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, RETYPED_PASSWORD_FIELD);
    assertTrue(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationPasswordAndRetypedPasswordWhenNoContainsLowerCaseLetter() {
    final String testPassword = "TTTT123!";
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPassword(testPassword);
    registerDTO.setRetypedPassword(testPassword);

    validationFalsePasswordAndRetypedPassword(registerDTO);
  }

  @Test
  public void shouldFailAtValidationPasswordAndRetypedPasswordWhenNoContainsUpperCaseLetter() {
    final String testPassword = "tttt123!";
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPassword(testPassword);
    registerDTO.setRetypedPassword(testPassword);

    validationFalsePasswordAndRetypedPassword(registerDTO);
  }

  @Test
  public void shouldFailAtValidationPasswordAndRetypedPasswordWhenNoContainsDigit() {
    final String testPassword = "Ttttttt!";
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPassword(testPassword);
    registerDTO.setRetypedPassword(testPassword);

    validationFalsePasswordAndRetypedPassword(registerDTO);
  }

  @Test
  public void shouldFailAtValidationPasswordAndRetypedPasswordWhenNoContainsSpecialSymbol() {
    final String testPassword = "Ttttttt1";
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setPassword(testPassword);
    registerDTO.setRetypedPassword(testPassword);

    validationFalsePasswordAndRetypedPassword(registerDTO);
  }

  public void validationFalsePasswordAndRetypedPassword(RegisterDTO registerDTO) {
    Set<ConstraintViolation<RegisterDTO>> violations;
    violations = validator.validateProperty(registerDTO, PASSWORD_FIELD);
    assertFalse(violations.isEmpty());
    violations = validator.validateProperty(registerDTO, RETYPED_PASSWORD_FIELD);
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldSuccessAtValidationEmail() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setEmail("Em.-1_ail@T1e.pL");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, EMAIL_FIELD);

    assertTrue(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationEmailWhenNotContainsAt() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setEmail("emailte.pl");

    validationFalseEmail(registerDTO);
  }

  @Test
  public void shouldFailAtValidationEmailWhenNotContainsDot() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setEmail("email@tepl");

    validationFalseEmail(registerDTO);
  }

  @Test
  public void shouldFailAtValidationEmailWhenContainsMoreThanOneAt() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setEmail("em@ail@te.pl");

    validationFalseEmail(registerDTO);
  }

  @Test
  public void shouldFailAtValidationEmailWhenContainsDigitsAfterDot() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setEmail("email@te.p1l");

    validationFalseEmail(registerDTO);
  }

  @Test
  public void shouldFailAtValidationEmailWhenWordAfterDotIsTooShort() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setEmail("email@te.p");

    validationFalseEmail(registerDTO);
  }

  @Test
  public void shouldFailAtValidationEmailWhenWordAfterDotIsTooLong() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setEmail("email@te.psdaf");

    validationFalseEmail(registerDTO);
  }

  public void validationFalseEmail(RegisterDTO registerDTO) {
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, EMAIL_FIELD);
    assertFalse(violations.isEmpty());
  }
}
