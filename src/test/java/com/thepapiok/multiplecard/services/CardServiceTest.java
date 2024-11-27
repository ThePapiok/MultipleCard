package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.zxing.WriterException;
import com.mongodb.MongoWriteException;
import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Card;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.CardRepository;
import java.io.IOException;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;

public class CardServiceTest {
  private static final ObjectId TEST_ID = new ObjectId("123456789012345678901234");
  private static final String TEST_CARD_ID = "523456729012145678901235";
  private static final ObjectId TEST_CARD_OBJECT_ID = new ObjectId(TEST_CARD_ID);
  private static final String TEST_PHONE = "12323234112";
  private static final String TEST_ENCRYPTED_PIN = "asdf21312sdfdafasdf";
  private static final String TEST_CARD_NAME = "card";
  private static final String TEST_PIN = "1111";
  @Mock private AccountRepository accountRepository;
  @Mock private CardRepository cardRepository;
  @Mock private CloudinaryService cloudinaryService;
  @Mock private MongoTemplate mongoTemplate;
  @Mock private MongoTransactionManager mongoTransactionManager;
  @Mock private QrCodeService qrCodeService;
  private CardService cardService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    cardService =
        new CardService(
            accountRepository,
            cardRepository,
            cloudinaryService,
            mongoTemplate,
            mongoTransactionManager,
            qrCodeService);
  }

  @Test
  public void shouldSuccessAtGetCard() {
    Account account = new Account();
    account.setId(TEST_ID);
    Card expectedCard = new Card();

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(cardRepository.findCardByUserId(TEST_ID)).thenReturn(expectedCard);

    assertEquals(expectedCard, cardService.getCard(TEST_PHONE));
  }

  @Test
  public void shouldSuccessAtCreateCardWhenNoCard() throws IOException, WriterException {
    shouldSuccess(null);
  }

  @Test
  public void shouldSuccessAtCreateCardWithCard() throws IOException, WriterException {
    Card card = new Card();
    card.setId(TEST_CARD_OBJECT_ID);

    shouldSuccess(card);
    verify(cloudinaryService).deleteImage(TEST_CARD_ID);
    verify(mongoTemplate).remove(card);
  }

  @Test
  public void shouldFailAtCreateCardWhenGetException() throws IOException, WriterException {
    Account account = new Account();
    account.setId(TEST_ID);
    Card card = new Card();
    card.setId(TEST_CARD_OBJECT_ID);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(cardRepository.findCardByUserId(TEST_ID)).thenReturn(card);
    doThrow(IOException.class).when(cloudinaryService).deleteImage(TEST_CARD_ID);

    assertFalse(
        cardService.createCard(TEST_PHONE, TEST_CARD_ID, TEST_ENCRYPTED_PIN, TEST_CARD_NAME));
  }

  private void shouldSuccess(Card givenCard) throws IOException, WriterException {
    byte[] bytes = new byte[0];
    Card expectedCard = new Card();
    expectedCard.setName(TEST_CARD_NAME);
    expectedCard.setPin(TEST_ENCRYPTED_PIN);
    expectedCard.setAttempts(0);
    expectedCard.setUserId(TEST_ID);
    expectedCard.setImageUrl("");
    expectedCard.setId(TEST_CARD_OBJECT_ID);
    expectedCard.setImageUrl("dasdas1231231@sdfasdfds");

    Account account = new Account();
    account.setId(TEST_ID);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(cardRepository.findCardByUserId(TEST_ID)).thenReturn(givenCard);
    when(qrCodeService.generateQrCode("nullcards?id=" + TEST_CARD_ID)).thenReturn(bytes);
    when(cloudinaryService.addImage(bytes, TEST_CARD_ID)).thenReturn("dasdas1231231@sdfasdfds");

    assertTrue(
        cardService.createCard(TEST_PHONE, TEST_CARD_ID, TEST_ENCRYPTED_PIN, TEST_CARD_NAME));
    verify(mongoTemplate).save(expectedCard);
  }

  @Test
  public void shouldSuccessAtBlockCard() {
    final int maxAttempts = 3;
    Account account = new Account();
    account.setId(TEST_ID);
    Card card = new Card();
    card.setUserId(TEST_ID);
    Card expectedCard = new Card();
    expectedCard.setUserId(TEST_ID);
    expectedCard.setAttempts(maxAttempts);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(cardRepository.findCardByUserId(TEST_ID)).thenReturn(card);

    assertTrue(cardService.blockCard(TEST_PHONE));
    verify(cardRepository).save(expectedCard);
  }

  @Test
  public void shouldFailAtBlockCardWhenGetException() {
    final int maxAttempts = 3;
    Account account = new Account();
    account.setId(TEST_ID);
    Card card = new Card();
    card.setUserId(TEST_ID);
    Card expectedCard = new Card();
    expectedCard.setUserId(TEST_ID);
    expectedCard.setAttempts(maxAttempts);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(cardRepository.findCardByUserId(TEST_ID)).thenReturn(card);
    doThrow(MongoWriteException.class).when(cardRepository).save(expectedCard);

    assertFalse(cardService.blockCard(TEST_PHONE));
    verify(cardRepository).save(expectedCard);
  }

  @Test
  public void shouldSuccessAtIsBlocked() {
    Account account = new Account();
    account.setId(TEST_ID);
    Card card = new Card();
    card.setUserId(TEST_ID);
    card.setAttempts(0);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(cardRepository.findCardByUserId(TEST_ID)).thenReturn(card);

    assertTrue(cardService.isBlocked(TEST_PHONE));
  }

  @Test
  public void shouldFailAtIsBlocked() {
    final int maxAttempts = 3;
    Account account = new Account();
    account.setId(TEST_ID);
    Card card = new Card();
    card.setUserId(TEST_ID);
    card.setAttempts(maxAttempts);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(cardRepository.findCardByUserId(TEST_ID)).thenReturn(card);

    assertFalse(cardService.isBlocked(TEST_PHONE));
  }

  @Test
  public void shouldReturnTrueAtCardExistsWhenCardFound() {
    when(cardRepository.existsCardById(TEST_CARD_OBJECT_ID)).thenReturn(true);

    assertTrue(cardService.cardExists(TEST_CARD_ID.toString()));
  }

  @Test
  public void shouldReturnFalseAtCardExistsWhenCardNotFound() {
    when(cardRepository.existsCardById(TEST_CARD_OBJECT_ID)).thenReturn(false);

    assertFalse(cardService.cardExists(TEST_CARD_ID.toString()));
  }

  @Test
  public void shouldReturnFalseAtCardExistsWhenBadId() {
    assertFalse(cardService.cardExists("1234"));
  }
}
