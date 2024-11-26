package com.thepapiok.multiplecard.validators;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.thepapiok.multiplecard.dto.PromotionDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PromotionDTOTest {
  private static final String NEW_PRICE_PARAM = "newPrice";
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
  public void shouldSuccessAtNewPrice() {
    PromotionDTO promotionDTO = new PromotionDTO();
    promotionDTO.setNewPrice("12.12zł (30.12zł)");

    Set<ConstraintViolation<PromotionDTO>> violations =
        validator.validateProperty(promotionDTO, NEW_PRICE_PARAM);
    assertTrue(violations.isEmpty());
  }

  @Test
  public void shouldFailAtNewPriceWhenNotContainsOriginalPrice() {
    PromotionDTO promotionDTO = new PromotionDTO();
    promotionDTO.setNewPrice("12.12zł");

    Set<ConstraintViolation<PromotionDTO>> violations =
        validator.validateProperty(promotionDTO, NEW_PRICE_PARAM);
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtNewPriceWhenNotContainsCurrency() {
    PromotionDTO promotionDTO = new PromotionDTO();
    promotionDTO.setNewPrice("12.12 (30.12)");

    Set<ConstraintViolation<PromotionDTO>> violations =
        validator.validateProperty(promotionDTO, NEW_PRICE_PARAM);
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtNewPriceWhenNotContainsDecimalPart() {
    PromotionDTO promotionDTO = new PromotionDTO();
    promotionDTO.setNewPrice("12zł (30zł)");

    Set<ConstraintViolation<PromotionDTO>> violations =
        validator.validateProperty(promotionDTO, NEW_PRICE_PARAM);
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtNewPriceWhenContainsSpecialSymbols() {
    PromotionDTO promotionDTO = new PromotionDTO();
    promotionDTO.setNewPrice("1!.12zł (30.12zł)");

    Set<ConstraintViolation<PromotionDTO>> violations =
        validator.validateProperty(promotionDTO, NEW_PRICE_PARAM);
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtNewPriceWhenContainsLetters() {
    PromotionDTO promotionDTO = new PromotionDTO();
    promotionDTO.setNewPrice("1A.12zł (30.12zł)");

    Set<ConstraintViolation<PromotionDTO>> violations =
        validator.validateProperty(promotionDTO, NEW_PRICE_PARAM);
    assertFalse(violations.isEmpty());
  }
}
