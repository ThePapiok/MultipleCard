package com.thepapiok.multiplecard.validators;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.thepapiok.multiplecard.dto.AddressDTO;
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
  private static final String CALLING_CODE_FIELD = "callingCode";

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
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setCity(TEST_TEXT);
    addressDTO.setStreet(TEST_TEXT);
    addressDTO.setProvince(TEST_TEXT);
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setLastName(TEST_TEXT);
    registerDTO.setAddress(addressDTO);

    validationTrueLastNameCityStreetAndProvince(registerDTO);
  }

  @Test
  public void shouldSuccessAtValidationLastNameCityStreetAndProvinceWithTwoParts1() {
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setCity(TEST_TEXT_TWO_PARTS);
    addressDTO.setStreet(TEST_TEXT_TWO_PARTS);
    addressDTO.setProvince(TEST_TEXT_TWO_PARTS);
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setLastName(TEST_TEXT_TWO_PARTS);
    registerDTO.setAddress(addressDTO);

    validationTrueLastNameCityStreetAndProvince(registerDTO);
  }

  @Test
  public void shouldSuccessAtValidationLastNameCityStreetAndProvinceWithTwoParts2() {
    final String testText = "Test-Test";
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setCity(testText);
    addressDTO.setStreet(testText);
    addressDTO.setProvince(testText);
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setLastName(testText);
    registerDTO.setAddress(addressDTO);

    validationTrueLastNameCityStreetAndProvince(registerDTO);
  }

  private void validationTrueLastNameCityStreetAndProvince(RegisterDTO registerDTO) {
    Set<ConstraintViolation<RegisterDTO>> violations;
    Set<ConstraintViolation<AddressDTO>> violationsAddress;
    AddressDTO addressDTO = registerDTO.getAddress();

    violations = validator.validateProperty(registerDTO, LAST_NAME_FIELD);
    assertTrue(violations.isEmpty());
    violationsAddress = validator.validateProperty(addressDTO, CITY_FIELD);
    assertTrue(violationsAddress.isEmpty());
    violationsAddress = validator.validateProperty(addressDTO, STREET_FIELD);
    assertTrue(violationsAddress.isEmpty());
    violationsAddress = validator.validateProperty(addressDTO, PROVINCE_FIELD);
    assertTrue(violationsAddress.isEmpty());
  }

  @Test
  public void shouldFailAtValidationLastNameCityStreetAndProvinceWhenNoStartWithUpperCase() {
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setCity(TEST_TEXT_LOWER_CASE);
    addressDTO.setStreet(TEST_TEXT_LOWER_CASE);
    addressDTO.setProvince(TEST_TEXT_LOWER_CASE);
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setLastName(TEST_TEXT_LOWER_CASE);
    registerDTO.setAddress(addressDTO);

    validationFalseLastNameCityStreetAndProvince(registerDTO);
  }

  @Test
  public void shouldFailAtValidationLastNameCityStreetAndProvinceWhenContainsDigits() {
    final String testText = "Te1st";
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setCity(testText);
    addressDTO.setStreet(testText);
    addressDTO.setProvince(testText);
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setLastName(testText);
    registerDTO.setAddress(addressDTO);

    validationFalseLastNameCityStreetAndProvince(registerDTO);
  }

  @Test
  public void shouldFailAtValidationLastNameCityStreetAndProvinceWhenContainsSpecialSymbols() {
    final String testText = "Te@st";
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setCity(testText);
    addressDTO.setStreet(testText);
    addressDTO.setProvince(testText);
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setLastName(testText);
    registerDTO.setAddress(addressDTO);

    validationFalseLastNameCityStreetAndProvince(registerDTO);
  }

  @Test
  public void
      shouldFailAtValidationLastNameCityStreetAndProvinceWhenSecondPartNoStartWithUpperCase1() {
    final String testText = "Test test";
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setCity(testText);
    addressDTO.setStreet(testText);
    addressDTO.setProvince(testText);
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setLastName(testText);
    registerDTO.setAddress(addressDTO);

    validationFalseLastNameCityStreetAndProvince(registerDTO);
  }

  @Test
  public void
      shouldFailAtValidationLastNameCityStreetAndProvinceWhenSecondPartNoStartWithUpperCase2() {
    final String testText = "Test-test";
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setCity(testText);
    addressDTO.setStreet(testText);
    addressDTO.setProvince(testText);
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setLastName(testText);
    registerDTO.setAddress(addressDTO);

    validationFalseLastNameCityStreetAndProvince(registerDTO);
  }

  @Test
  public void shouldFailAtValidationLastNameCityStreetAndProvinceWhenSecondPartContainsDigits1() {
    final String testText = "Test Te1st";
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setCity(testText);
    addressDTO.setStreet(testText);
    addressDTO.setProvince(testText);
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setLastName(testText);
    registerDTO.setAddress(addressDTO);

    validationFalseLastNameCityStreetAndProvince(registerDTO);
  }

  @Test
  public void shouldFailAtValidationLastNameCityStreetAndProvinceWhenSecondPartContainsDigits2() {
    final String testText = "Test-Te1st";
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setCity(testText);
    addressDTO.setStreet(testText);
    addressDTO.setProvince(testText);
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setLastName(testText);
    registerDTO.setAddress(addressDTO);

    validationFalseLastNameCityStreetAndProvince(registerDTO);
  }

  @Test
  public void
      shouldFailAtValidationLastNameCityStreetAndProvinceWhenSecondPartContainsSpecialSymbols1() {
    final String testText = "Test Te!st";
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setCity(testText);
    addressDTO.setStreet(testText);
    addressDTO.setProvince(testText);
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setLastName(testText);
    registerDTO.setAddress(addressDTO);

    validationFalseLastNameCityStreetAndProvince(registerDTO);
  }

  @Test
  public void
      shouldFailAtValidationLastNameCityStreetAndProvinceWhenSecondPartContainsSpecialSymbols2() {
    final String testText = "Test-Te!st";
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setCity(testText);
    addressDTO.setStreet(testText);
    addressDTO.setProvince(testText);
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setLastName(testText);
    registerDTO.setAddress(addressDTO);

    validationFalseLastNameCityStreetAndProvince(registerDTO);
  }

  private void validationFalseLastNameCityStreetAndProvince(RegisterDTO registerDTO) {
    Set<ConstraintViolation<RegisterDTO>> violations;
    Set<ConstraintViolation<AddressDTO>> violationsAddress;
    AddressDTO addressDTO = registerDTO.getAddress();

    violations = validator.validateProperty(registerDTO, LAST_NAME_FIELD);
    assertFalse(violations.isEmpty());
    violationsAddress = validator.validateProperty(addressDTO, CITY_FIELD);
    assertFalse(violationsAddress.isEmpty());
    violationsAddress = validator.validateProperty(addressDTO, STREET_FIELD);
    assertFalse(violationsAddress.isEmpty());
    violationsAddress = validator.validateProperty(addressDTO, PROVINCE_FIELD);
    assertFalse(violationsAddress.isEmpty());
  }

  @Test
  public void shouldSuccessAtValidationHouseNumber1() {
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setHouseNumber("1");
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setAddress(addressDTO);

    validationTrueHouseNumber(registerDTO);
  }

  @Test
  public void shouldSuccessAtValidationHouseNumber2() {
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setHouseNumber(TEST_HOUSE_APARTMENT_NUMBER_WITH_LETTER);
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setAddress(addressDTO);

    validationTrueHouseNumber(registerDTO);
  }

  @Test
  public void shouldSuccessAtValidationHouseNumber3() {
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setHouseNumber("1/2");
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setAddress(addressDTO);

    validationTrueHouseNumber(registerDTO);
  }

  public void validationTrueHouseNumber(RegisterDTO registerDTO) {
    Set<ConstraintViolation<AddressDTO>> violations =
        validator.validateProperty(registerDTO.getAddress(), HOUSE_NUMBER_FIELD);
    assertTrue(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationHouseNumberWhenLetterIsNotAtEnd() {
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setHouseNumber("B1");
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setAddress(addressDTO);

    validationFalseHouseNumber(registerDTO);
  }

  @Test
  public void shouldFailAtValidationHouseNumberWhenContainsSpecialSymbols() {
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setHouseNumber(TEST_HOUSE_APARTMENT_NUMBER_WITH_SPECIAL_SYMBOL);
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setAddress(addressDTO);

    validationFalseHouseNumber(registerDTO);
  }

  @Test
  public void shouldFailAtValidationHouseNumberWhenContainsSlashButNoNumberAtEnd1() {
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setHouseNumber("1/");
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setAddress(addressDTO);

    validationFalseHouseNumber(registerDTO);
  }

  @Test
  public void shouldFailAtValidationHouseNumberWhenContainsSlashButNoNumberAtEnd2() {
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setHouseNumber("1//");
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setAddress(addressDTO);

    validationFalseHouseNumber(registerDTO);
  }

  @Test
  public void shouldFailAtValidationHouseNumberWhenContainsSlashButNoNumberAtEnd3() {
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setHouseNumber("1/!");
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setAddress(addressDTO);

    validationFalseHouseNumber(registerDTO);
  }

  @Test
  public void shouldFailAtValidationHouseNumberWhenContainsSlashButNoNumberAtEnd4() {
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setHouseNumber("1/B");
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setAddress(addressDTO);

    validationFalseHouseNumber(registerDTO);
  }

  @Test
  public void shouldFailAtValidationHouseNumberWhenContainsMoreThanTwoParts1() {
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setHouseNumber("1/111/11");
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setAddress(addressDTO);

    validationFalseHouseNumber(registerDTO);
  }

  @Test
  public void shouldFailAtValidationHouseNumberWhenContainsMoreThanTwoParts2() {
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setHouseNumber("1B111C");
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setAddress(addressDTO);

    validationFalseHouseNumber(registerDTO);
  }

  public void validationFalseHouseNumber(RegisterDTO registerDTO) {
    Set<ConstraintViolation<AddressDTO>> violations =
        validator.validateProperty(registerDTO.getAddress(), HOUSE_NUMBER_FIELD);
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldSuccessAtValidationApartmentNumber1() {
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setApartmentNumber("156");
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setAddress(addressDTO);

    validationTrueApartmentNumber(registerDTO);
  }

  @Test
  public void shouldSuccessAtValidationApartmentNumber2() {
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setApartmentNumber("");
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setAddress(addressDTO);

    validationTrueApartmentNumber(registerDTO);
  }

  public void validationTrueApartmentNumber(RegisterDTO registerDTO) {
    Set<ConstraintViolation<AddressDTO>> violations =
        validator.validateProperty(registerDTO.getAddress(), APARTMENT_NUMBER_FIELD);
    assertTrue(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationApartmentNumberWhenStartWith0() {
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setApartmentNumber("01");
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setAddress(addressDTO);

    validationFalseApartmentNumber(registerDTO);
  }

  @Test
  public void shouldFailAtValidationApartmentNumberWhenContainsLetters() {
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setApartmentNumber(TEST_HOUSE_APARTMENT_NUMBER_WITH_LETTER);
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setAddress(addressDTO);

    validationFalseApartmentNumber(registerDTO);
  }

  @Test
  public void shouldFailAtValidationApartmentNumberWhenContainsSpecialSymbols() {
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setApartmentNumber(TEST_HOUSE_APARTMENT_NUMBER_WITH_SPECIAL_SYMBOL);
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setAddress(addressDTO);

    validationFalseApartmentNumber(registerDTO);
  }

  @Test
  public void shouldFailAtValidationApartmentNumberWhenMoreThanOnePart() {
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setApartmentNumber("1 1");
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setAddress(addressDTO);

    validationFalseApartmentNumber(registerDTO);
  }

  public void validationFalseApartmentNumber(RegisterDTO registerDTO) {
    Set<ConstraintViolation<AddressDTO>> violations =
        validator.validateProperty(registerDTO.getAddress(), APARTMENT_NUMBER_FIELD);
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldSuccessAtValidationPostalCode() {
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setPostalCode("01-432");
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setAddress(addressDTO);
    Set<ConstraintViolation<AddressDTO>> violations =
        validator.validateProperty(registerDTO.getAddress(), POSTAL_CODE_FIELD);

    assertTrue(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationPostalCodeWhenNotContainsMinusAt3Char1() {
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setPostalCode("01 432");
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setAddress(addressDTO);

    validationFalsePostalCode(registerDTO);
  }

  @Test
  public void shouldFailAtValidationPostalCodeWhenNotContainsMinusAt3Char2() {
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setPostalCode("011432");
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setAddress(addressDTO);

    validationFalsePostalCode(registerDTO);
  }

  @Test
  public void shouldFailAtValidationPostalCodeWhenNotContainsMinusAt3Char3() {
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setPostalCode("0-1432");
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setAddress(addressDTO);

    validationFalsePostalCode(registerDTO);
  }

  @Test
  public void shouldFailAtValidationPostalCodeWhenContainsLetters() {
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setPostalCode("01-B32");
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setAddress(addressDTO);

    validationFalsePostalCode(registerDTO);
  }

  @Test
  public void shouldFailAtValidationPostalCodeWhenContainsSpecialSymbols() {
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setPostalCode("01-!32");
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setAddress(addressDTO);

    validationFalsePostalCode(registerDTO);
  }

  @Test
  public void shouldFailAtValidationPostalCodeWhenToLessSize() {
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setPostalCode("01-32");
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setAddress(addressDTO);

    validationFalsePostalCode(registerDTO);
  }

  public void validationFalsePostalCode(RegisterDTO registerDTO) {
    Set<ConstraintViolation<AddressDTO>> violations =
        validator.validateProperty(registerDTO.getAddress(), POSTAL_CODE_FIELD);
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

  @Test
  public void shouldSuccessAtValidationCallingCode() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setCallingCode("+48");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, CALLING_CODE_FIELD);

    assertTrue(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationCallingCodeWhenNoPlus() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setCallingCode("48");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, CALLING_CODE_FIELD);

    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationCallingCodeWhenContainsLetters() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setCallingCode("+48b");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, CALLING_CODE_FIELD);

    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationCallingCodeWhenContainsSpecialSymbols() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setCallingCode("+48!");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, CALLING_CODE_FIELD);

    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationCallingCodeWhenTooLong() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setCallingCode("+482134");
    Set<ConstraintViolation<RegisterDTO>> violations =
        validator.validateProperty(registerDTO, CALLING_CODE_FIELD);

    assertFalse(violations.isEmpty());
  }
}
