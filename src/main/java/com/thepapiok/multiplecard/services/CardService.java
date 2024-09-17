package com.thepapiok.multiplecard.services;

import com.thepapiok.multiplecard.collections.Card;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.CardRepository;
import com.thepapiok.multiplecard.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardService {
  private final AccountRepository accountRepository;
  private final UserRepository userRepository;
  private final CardRepository cardRepository;

  @Autowired
  public CardService(
      AccountRepository accountRepository,
      UserRepository userRepository,
      CardRepository cardRepository) {
    this.accountRepository = accountRepository;
    this.userRepository = userRepository;
    this.cardRepository = cardRepository;
  }

  public Card getCard(String phone) {
    return cardRepository.findCardByUserId(accountRepository.findIdByPhone(phone).getId());
  }
}
