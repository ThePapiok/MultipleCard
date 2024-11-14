package com.thepapiok.multiplecard.validators;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.thepapiok.multiplecard.dto.ResetPasswordDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ResetPasswordDTOTest {
  private static final String CODE_PARAM = "code";
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
  public void shouldSuccessAtValidationCode() {
    ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
    resetPasswordDTO.setCode("123 123");
    Set<ConstraintViolation<ResetPasswordDTO>> violations =
        validator.validateProperty(resetPasswordDTO, CODE_PARAM);

    assertTrue(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationCodeWhenNotContainsSpace() {
    ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
    resetPasswordDTO.setCode("123123");
    Set<ConstraintViolation<ResetPasswordDTO>> violations =
        validator.validateProperty(resetPasswordDTO, CODE_PARAM);

    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationCodeWhenToShortLength() {
    ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
    resetPasswordDTO.setCode("123 12");
    Set<ConstraintViolation<ResetPasswordDTO>> violations =
        validator.validateProperty(resetPasswordDTO, CODE_PARAM);

    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationCodeWhenToLongLength() {
    ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
    resetPasswordDTO.setCode("123 1233");
    Set<ConstraintViolation<ResetPasswordDTO>> violations =
        validator.validateProperty(resetPasswordDTO, CODE_PARAM);

    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationCodeWhenContainsLetter() {
    ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
    resetPasswordDTO.setCode("123 1A3");
    Set<ConstraintViolation<ResetPasswordDTO>> violations =
        validator.validateProperty(resetPasswordDTO, CODE_PARAM);

    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationCodeWhenContainsSpecialSymbol() {
    ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
    resetPasswordDTO.setCode("123 1!3");
    Set<ConstraintViolation<ResetPasswordDTO>> violations =
        validator.validateProperty(resetPasswordDTO, CODE_PARAM);

    assertFalse(violations.isEmpty());
  }
}
