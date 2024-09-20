package com.thepapiok.multiplecard.misc;

import com.thepapiok.multiplecard.collections.Card;
import com.thepapiok.multiplecard.dto.OrderCardDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CardConverter {

  private final PasswordEncoder passwordEncoder;

  @Autowired
  public CardConverter(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  public Card getEntity(OrderCardDTO cardDTO) {
    Card card = new Card();
    card.setPin(passwordEncoder.encode(cardDTO.getPin()));
    card.setName(cardDTO.getName());
    return card;
  }
}
