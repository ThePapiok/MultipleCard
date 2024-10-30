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
  public void shouldSuccessAtAmount() {
    PromotionDTO promotionDTO = new PromotionDTO();
    promotionDTO.setAmount("12.12zł (30.12zł)");

    Set<ConstraintViolation<PromotionDTO>> violations =
        validator.validateProperty(promotionDTO, AMOUNT_PARAM);
    assertTrue(violations.isEmpty());
  }

  @Test
  public void shouldFailAtAmountWhenNotContainsOriginalAmount() {
    PromotionDTO promotionDTO = new PromotionDTO();
    promotionDTO.setAmount("12.12zł");

    Set<ConstraintViolation<PromotionDTO>> violations =
        validator.validateProperty(promotionDTO, AMOUNT_PARAM);
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtAmountWhenNotContainsCurrency() {
    PromotionDTO promotionDTO = new PromotionDTO();
    promotionDTO.setAmount("12.12 (30.12)");

    Set<ConstraintViolation<PromotionDTO>> violations =
        validator.validateProperty(promotionDTO, AMOUNT_PARAM);
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtAmountWhenNotContainsDecimalPart() {
    PromotionDTO promotionDTO = new PromotionDTO();
    promotionDTO.setAmount("12zł (30zł)");

    Set<ConstraintViolation<PromotionDTO>> violations =
        validator.validateProperty(promotionDTO, AMOUNT_PARAM);
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtAmountWhenContainsSpecialSymbols() {
    PromotionDTO promotionDTO = new PromotionDTO();
    promotionDTO.setAmount("1!.12zł (30.12zł)");

    Set<ConstraintViolation<PromotionDTO>> violations =
        validator.validateProperty(promotionDTO, AMOUNT_PARAM);
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtAmountWhenContainsLetters() {
    PromotionDTO promotionDTO = new PromotionDTO();
    promotionDTO.setAmount("1A.12zł (30.12zł)");

    Set<ConstraintViolation<PromotionDTO>> violations =
        validator.validateProperty(promotionDTO, AMOUNT_PARAM);
    assertFalse(violations.isEmpty());
  }
}
