package com.thepapiok.multiplecard.validators;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.thepapiok.multiplecard.dto.AddProductDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AddProductDTOTest {
  private static final String NAME_PARAM = "name";
  private static final String BARCODE_PARAM = "barcode";
  private static final String AMOUNT_PARAM = "amount";
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
  public void shouldSuccessAtValidationNameWith1Part() {
    AddProductDTO addProductDTO = new AddProductDTO();
    addProductDTO.setName("Produkt");

    Set<ConstraintViolation<AddProductDTO>> violations =
        validator.validateProperty(addProductDTO, NAME_PARAM);
    assertTrue(violations.isEmpty());
  }

  @Test
  public void shouldSuccessAtValidationNameWithMoreThan1Part() {
    AddProductDTO addProductDTO = new AddProductDTO();
    addProductDTO.setName("Produkt produkt produkt");

    Set<ConstraintViolation<AddProductDTO>> violations =
        validator.validateProperty(addProductDTO, NAME_PARAM);
    assertTrue(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationNameWhenContainsDigits() {
    AddProductDTO addProductDTO = new AddProductDTO();
    addProductDTO.setName("Produkt1");

    Set<ConstraintViolation<AddProductDTO>> violations =
        validator.validateProperty(addProductDTO, NAME_PARAM);
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationNameWhenContainsSpecialSymbols() {
    AddProductDTO addProductDTO = new AddProductDTO();
    addProductDTO.setName("Produkt!");

    Set<ConstraintViolation<AddProductDTO>> violations =
        validator.validateProperty(addProductDTO, NAME_PARAM);
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationNameWhenNoStartWithUpperCase() {
    AddProductDTO addProductDTO = new AddProductDTO();
    addProductDTO.setName("produkt");

    Set<ConstraintViolation<AddProductDTO>> violations =
        validator.validateProperty(addProductDTO, NAME_PARAM);
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldSuccessAtValidationBarcode() {
    AddProductDTO addProductDTO = new AddProductDTO();
    addProductDTO.setBarcode("1234567890123");

    Set<ConstraintViolation<AddProductDTO>> violations =
        validator.validateProperty(addProductDTO, BARCODE_PARAM);
    assertTrue(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationBarcodeWhenContainsLetters() {
    AddProductDTO addProductDTO = new AddProductDTO();
    addProductDTO.setBarcode("123456789012A");

    Set<ConstraintViolation<AddProductDTO>> violations =
        validator.validateProperty(addProductDTO, BARCODE_PARAM);
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationBarcodeWhenContainsSpecialSymbols() {
    AddProductDTO addProductDTO = new AddProductDTO();
    addProductDTO.setBarcode("123456789012!");

    Set<ConstraintViolation<AddProductDTO>> violations =
        validator.validateProperty(addProductDTO, BARCODE_PARAM);
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationBarcodeWhenLengthIsTooSmall() {
    AddProductDTO addProductDTO = new AddProductDTO();
    addProductDTO.setBarcode("123456789012");

    Set<ConstraintViolation<AddProductDTO>> violations =
        validator.validateProperty(addProductDTO, BARCODE_PARAM);
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldSuccessAtValidationAmount() {
    AddProductDTO addProductDTO = new AddProductDTO();
    addProductDTO.setAmount("1231.12zł");

    Set<ConstraintViolation<AddProductDTO>> violations =
        validator.validateProperty(addProductDTO, AMOUNT_PARAM);
    assertTrue(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationAmountWhenContainsAnotherSpecialSymbols() {
    AddProductDTO addProductDTO = new AddProductDTO();
    addProductDTO.setAmount("1!31.12zł");

    Set<ConstraintViolation<AddProductDTO>> violations =
        validator.validateProperty(addProductDTO, AMOUNT_PARAM);
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationAmountWhenDontContainsCurrency() {
    AddProductDTO addProductDTO = new AddProductDTO();
    addProductDTO.setAmount("131.12");

    Set<ConstraintViolation<AddProductDTO>> violations =
        validator.validateProperty(addProductDTO, AMOUNT_PARAM);
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationAmountWhenContainsLetters() {
    AddProductDTO addProductDTO = new AddProductDTO();
    addProductDTO.setAmount("1A31.12zł");

    Set<ConstraintViolation<AddProductDTO>> violations =
        validator.validateProperty(addProductDTO, AMOUNT_PARAM);
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationAmountWhenNoContainsDot() {
    AddProductDTO addProductDTO = new AddProductDTO();
    addProductDTO.setAmount("113112zł");

    Set<ConstraintViolation<AddProductDTO>> violations =
        validator.validateProperty(addProductDTO, AMOUNT_PARAM);
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationAmountWhenFractionalPartIsTooLong() {
    AddProductDTO addProductDTO = new AddProductDTO();
    addProductDTO.setAmount("1131.122zł");

    Set<ConstraintViolation<AddProductDTO>> violations =
        validator.validateProperty(addProductDTO, AMOUNT_PARAM);
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationAmountWhenFractionalPartIsTooShort() {
    AddProductDTO addProductDTO = new AddProductDTO();
    addProductDTO.setAmount("1131.2zł");

    Set<ConstraintViolation<AddProductDTO>> violations =
        validator.validateProperty(addProductDTO, AMOUNT_PARAM);
    assertFalse(violations.isEmpty());
  }
}
