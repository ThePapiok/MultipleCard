package com.thepapiok.multiplecard.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.thepapiok.multiplecard.collections.Card;
import com.thepapiok.multiplecard.dto.OrderCardDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CardConverterTest {

  @Mock private PasswordEncoder passwordEncoder;
  private CardConverter cardConverter;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    cardConverter = new CardConverter(passwordEncoder);
  }

  @Test
  public void shouldSuccessAtGetEntity() {
    final String testPin = "1213";
    final String testEncodePin = "123sdfsdfasdf123";
    final String testName = "name";
    OrderCardDTO orderCardDTO = new OrderCardDTO();
    orderCardDTO.setPin(testPin);
    orderCardDTO.setName(testName);
    Card expectedCard = new Card();
    expectedCard.setPin(testEncodePin);
    expectedCard.setName(testName);

    when(passwordEncoder.encode(testPin)).thenReturn(testEncodePin);

    assertEquals(expectedCard, cardConverter.getEntity(orderCardDTO));
  }
}
