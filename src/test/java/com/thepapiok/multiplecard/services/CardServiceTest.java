package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Card;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.CardRepository;
import com.thepapiok.multiplecard.repositories.UserRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CardServiceTest {
  @Mock private AccountRepository accountRepository;
  @Mock private UserRepository userRepository;
  @Mock private CardRepository cardRepository;
  private CardService cardService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    cardService = new CardService(accountRepository, userRepository, cardRepository);
  }

  @Test
  public void shouldSuccessAtGetCard() {
    final String phone = "12323234112";
    final ObjectId objectId = new ObjectId("123456789012345678901234");
    Account account = new Account();
    account.setId(objectId);
    Card expectedCard = new Card();

    when(accountRepository.findIdByPhone(phone)).thenReturn(account);
    when(cardRepository.findCardByUserId(objectId)).thenReturn(expectedCard);

    assertEquals(expectedCard, cardService.getCard(phone));
  }
}
