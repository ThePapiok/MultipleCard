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
  private static final String PRICE_PARAM = "price";
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
    addProductDTO.setName("Produkt PRODUCT produkt");

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
  public void shouldSuccessAtValidationPrice() {
    AddProductDTO addProductDTO = new AddProductDTO();
    addProductDTO.setPrice("1231.12zł");

    Set<ConstraintViolation<AddProductDTO>> violations =
        validator.validateProperty(addProductDTO, PRICE_PARAM);
    assertTrue(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationPriceWhenContainsAnotherSpecialSymbols() {
    AddProductDTO addProductDTO = new AddProductDTO();
    addProductDTO.setPrice("1!31.12zł");

    Set<ConstraintViolation<AddProductDTO>> violations =
        validator.validateProperty(addProductDTO, PRICE_PARAM);
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationPriceWhenDontContainsCurrency() {
    AddProductDTO addProductDTO = new AddProductDTO();
    addProductDTO.setPrice("131.12");

    Set<ConstraintViolation<AddProductDTO>> violations =
        validator.validateProperty(addProductDTO, PRICE_PARAM);
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationPriceWhenContainsLetters() {
    AddProductDTO addProductDTO = new AddProductDTO();
    addProductDTO.setPrice("1A31.12zł");

    Set<ConstraintViolation<AddProductDTO>> violations =
        validator.validateProperty(addProductDTO, PRICE_PARAM);
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationPriceWhenNoContainsDot() {
    AddProductDTO addProductDTO = new AddProductDTO();
    addProductDTO.setPrice("113112zł");

    Set<ConstraintViolation<AddProductDTO>> violations =
        validator.validateProperty(addProductDTO, PRICE_PARAM);
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationPriceWhenFractionalPartIsTooLong() {
    AddProductDTO addProductDTO = new AddProductDTO();
    addProductDTO.setPrice("1131.122zł");

    Set<ConstraintViolation<AddProductDTO>> violations =
        validator.validateProperty(addProductDTO, PRICE_PARAM);
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationPriceWhenFractionalPartIsTooShort() {
    AddProductDTO addProductDTO = new AddProductDTO();
    addProductDTO.setPrice("1131.2zł");

    Set<ConstraintViolation<AddProductDTO>> violations =
        validator.validateProperty(addProductDTO, PRICE_PARAM);
    assertFalse(violations.isEmpty());
  }
}
