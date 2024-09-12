package com.thepapiok.multiplecard.services;

import com.thepapiok.multiplecard.collections.Card;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardService {
  private final AccountRepository accountRepository;
  private final UserRepository userRepository;

  @Autowired
  public CardService(AccountRepository accountRepository, UserRepository userRepository) {
    this.accountRepository = accountRepository;
    this.userRepository = userRepository;
  }

  public Card getCard(String phone) {
    return userRepository.findCardById(accountRepository.findIdByPhone(phone).getId()).getCard();
  }
}
