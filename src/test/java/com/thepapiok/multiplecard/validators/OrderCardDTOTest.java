package com.thepapiok.multiplecard.validators;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.thepapiok.multiplecard.dto.OrderCardDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class OrderCardDTOTest {
  private static final String NAME_PARAM = "name";
  private static final String PIN_PARAM = "pin";
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
  public void shouldSuccessAtValidationName() {
    OrderCardDTO orderCardDTO = new OrderCardDTO();
    orderCardDTO.setName("daZż0dz1Ń");

    Set<ConstraintViolation<OrderCardDTO>> violations =
        validator.validateProperty(orderCardDTO, NAME_PARAM);
    assertTrue(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationNameWhenContainsSpecialSymbol() {
    OrderCardDTO orderCardDTO = new OrderCardDTO();
    orderCardDTO.setName("daZż0dz!Ń");

    Set<ConstraintViolation<OrderCardDTO>> violations =
        validator.validateProperty(orderCardDTO, NAME_PARAM);
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldSuccessAtValidationPin() {
    OrderCardDTO orderCardDTO = new OrderCardDTO();
    orderCardDTO.setPin("1234");

    Set<ConstraintViolation<OrderCardDTO>> violations =
        validator.validateProperty(orderCardDTO, PIN_PARAM);
    assertTrue(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationPinWhenContainsSpecialSymbols() {
    OrderCardDTO orderCardDTO = new OrderCardDTO();
    orderCardDTO.setPin("123!");

    Set<ConstraintViolation<OrderCardDTO>> violations =
        validator.validateProperty(orderCardDTO, PIN_PARAM);
    assertFalse(violations.isEmpty());
  }

  @Test
  public void shouldFailAtValidationPinWhenContainsLetters() {
    OrderCardDTO orderCardDTO = new OrderCardDTO();
    orderCardDTO.setPin("123A");

    Set<ConstraintViolation<OrderCardDTO>> violations =
        validator.validateProperty(orderCardDTO, PIN_PARAM);
    assertFalse(violations.isEmpty());
  }
}
