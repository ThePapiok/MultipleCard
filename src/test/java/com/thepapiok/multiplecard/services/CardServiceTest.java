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
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.SearchCardDTO;
import com.thepapiok.multiplecard.misc.CustomMultipartFile;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.CardRepository;
import com.thepapiok.multiplecard.repositories.UserRepository;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

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
  @Mock private ImageService imageService;
  @Mock private EmailService emailService;
  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;
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
            qrCodeService,
            imageService,
            emailService,
            userRepository,
            passwordEncoder);
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
  public void shouldSuccessAtCreateCardWhenNoCard()
      throws IOException, WriterException, MessagingException {
    shouldSuccess(null);
  }

  @Test
  public void shouldSuccessAtCreateCardWithCard()
      throws IOException, WriterException, MessagingException {
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

  private void shouldSuccess(Card givenCard)
      throws IOException, WriterException, MessagingException {
    byte[] bytes = new byte[0];
    byte[] imageFrontBytes = new byte[0];
    byte[] imageBackBytes = new byte[0];
    Account account = new Account();
    account.setId(TEST_ID);
    account.setPhone(TEST_PHONE);
    User user = new User();
    user.setId(TEST_ID);
    Card expectedCard = new Card();
    expectedCard.setName(TEST_CARD_NAME);
    expectedCard.setPin(TEST_ENCRYPTED_PIN);
    expectedCard.setAttempts(0);
    expectedCard.setUserId(TEST_ID);
    expectedCard.setImageUrl("");
    expectedCard.setId(TEST_CARD_OBJECT_ID);
    expectedCard.setImageUrl("dasdas1231231@sdfasdfds");
    User expectedUser = new User();
    expectedUser.setId(TEST_ID);
    expectedUser.setCardId(TEST_CARD_OBJECT_ID);
    CustomMultipartFile customMultipartFile1 =
        new CustomMultipartFile("file1", "file1", "png", imageFrontBytes);
    CustomMultipartFile customMultipartFile2 =
        new CustomMultipartFile("file2", "file2", "png", imageBackBytes);
    List<CustomMultipartFile> cardImage = List.of(customMultipartFile1, customMultipartFile2);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(cardRepository.findCardByUserId(TEST_ID)).thenReturn(givenCard);
    when(qrCodeService.generateQrCode("nullcards?id=" + TEST_CARD_ID)).thenReturn(bytes);
    when(cloudinaryService.addImage(bytes, TEST_CARD_ID)).thenReturn("dasdas1231231@sdfasdfds");
    when(imageService.generateImage(bytes, TEST_CARD_NAME, TEST_CARD_ID)).thenReturn(cardImage);
    when(accountRepository.findByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(user));
    when(mongoTemplate.save(expectedCard)).thenReturn(expectedCard);

    assertTrue(
        cardService.createCard(TEST_PHONE, TEST_CARD_ID, TEST_ENCRYPTED_PIN, TEST_CARD_NAME));
    verify(mongoTemplate).save(expectedCard);
    verify(emailService).sendCardImage(cardImage, TEST_CARD_ID, account, user);
    verify(mongoTemplate).save(expectedUser);
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
  public void shouldReturnFalseAtIsBlockedWhenIsNotBlocked() {
    Account account = new Account();
    account.setId(TEST_ID);
    Card card = new Card();
    card.setUserId(TEST_ID);
    card.setAttempts(0);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(cardRepository.findCardByUserId(TEST_ID)).thenReturn(card);

    assertFalse(cardService.isBlocked(TEST_PHONE));
  }

  @Test
  public void shouldReturnTrueAtIsBlockedWhenIsBlocked() {
    final int maxAttempts = 3;
    Account account = new Account();
    account.setId(TEST_ID);
    Card card = new Card();
    card.setUserId(TEST_ID);
    card.setAttempts(maxAttempts);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(cardRepository.findCardByUserId(TEST_ID)).thenReturn(card);

    assertTrue(cardService.isBlocked(TEST_PHONE));
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

  @Test
  public void shouldReturnFalseAtIsOwnerWhenCardNotFound() {
    when(cardRepository.findById(TEST_CARD_OBJECT_ID)).thenReturn(Optional.empty());

    assertFalse(cardService.isOwner(TEST_PHONE, TEST_CARD_ID));
  }

  @Test
  public void shouldReturnFalseAtIsOwnerWhenIsNotOwner() {
    Card card = new Card();
    card.setUserId(TEST_ID);
    Account account = new Account();
    account.setPhone("123123123");

    when(cardRepository.findById(TEST_CARD_OBJECT_ID)).thenReturn(Optional.of(card));
    when(accountRepository.findPhoneById(TEST_ID)).thenReturn(account);

    assertFalse(cardService.isOwner(TEST_PHONE, TEST_CARD_ID));
  }

  @Test
  public void shouldReturnFalseAtIsOwnerWhenIsOwner() {
    Card card = new Card();
    card.setUserId(TEST_ID);
    Account account = new Account();
    account.setPhone(TEST_PHONE);

    when(cardRepository.findById(TEST_CARD_OBJECT_ID)).thenReturn(Optional.of(card));
    when(accountRepository.findPhoneById(TEST_ID)).thenReturn(account);

    assertTrue(cardService.isOwner(TEST_PHONE, TEST_CARD_ID));
  }

  @Test
  public void shouldReturnTrueAtIsBlockedObjectIdWhenCardIsEmpty() {
    when(cardRepository.findById(TEST_CARD_OBJECT_ID)).thenReturn(Optional.empty());

    assertTrue(cardService.isBlocked(TEST_CARD_OBJECT_ID));
  }

  @Test
  public void shouldReturnTrueAtIsBlockedObjectIdWhenIsBlocked() {
    final int testAttempts = 3;
    Card card = new Card();
    card.setAttempts(testAttempts);
    card.setId(TEST_CARD_OBJECT_ID);

    when(cardRepository.findById(TEST_CARD_OBJECT_ID)).thenReturn(Optional.of(card));

    assertTrue(cardService.isBlocked(TEST_CARD_OBJECT_ID));
  }

  @Test
  public void shouldReturnFalseAtIsBlockedObjectIdWhenIsNotBlocked() {
    final int testAttempts = 1;
    Card card = new Card();
    card.setAttempts(testAttempts);
    card.setId(TEST_CARD_OBJECT_ID);

    when(cardRepository.findById(TEST_CARD_OBJECT_ID)).thenReturn(Optional.of(card));

    assertFalse(cardService.isBlocked(TEST_CARD_OBJECT_ID));
  }

  @Test
  public void shouldReturnTrueAtCheckIdAndNameIsValidWhenEverythingOk() {
    SearchCardDTO searchCardDTO = new SearchCardDTO();
    searchCardDTO.setCardId(TEST_CARD_ID);
    searchCardDTO.setCardName(TEST_CARD_NAME);

    when(cardRepository.existsCardByIdAndName(TEST_CARD_OBJECT_ID, TEST_CARD_NAME))
        .thenReturn(true);

    assertTrue(cardService.checkIdAndNameIsValid(searchCardDTO));
  }

  @Test
  public void shouldReturnFalseAtCheckIdAndNameIsValidWhenIsNotValid() {
    SearchCardDTO searchCardDTO = new SearchCardDTO();
    searchCardDTO.setCardId(TEST_CARD_ID);
    searchCardDTO.setCardName(TEST_CARD_NAME + "A");

    when(cardRepository.existsCardByIdAndName(TEST_CARD_OBJECT_ID, TEST_CARD_NAME + "A"))
        .thenReturn(false);

    assertFalse(cardService.checkIdAndNameIsValid(searchCardDTO));
  }

  @Test
  public void shouldReturnFalseAtCheckPinWhenCardNotFound() {
    when(cardRepository.findById(TEST_CARD_OBJECT_ID)).thenReturn(Optional.empty());

    assertFalse(cardService.checkPin(TEST_CARD_ID, TEST_PIN));
  }

  @Test
  public void shouldReturnTrueAtCheckPinWhenPinMatches() {
    Card card = new Card();
    card.setAttempts(0);
    card.setPin(TEST_ENCRYPTED_PIN);

    when(cardRepository.findById(TEST_CARD_OBJECT_ID)).thenReturn(Optional.of(card));
    when(passwordEncoder.matches(TEST_PIN, TEST_ENCRYPTED_PIN)).thenReturn(true);

    assertTrue(cardService.checkPin(TEST_CARD_ID, TEST_PIN));
  }

  @Test
  public void shouldReturnFalseAtCheckPinWhenPinNotMatches() {
    Card card = new Card();
    card.setAttempts(0);
    card.setPin(TEST_ENCRYPTED_PIN);
    Card expectedCard = new Card();
    expectedCard.setAttempts(1);
    expectedCard.setPin(TEST_ENCRYPTED_PIN);

    when(cardRepository.findById(TEST_CARD_OBJECT_ID)).thenReturn(Optional.of(card));
    when(passwordEncoder.matches(TEST_PIN, TEST_ENCRYPTED_PIN)).thenReturn(false);

    assertFalse(cardService.checkPin(TEST_CARD_ID, TEST_PIN));
    verify(cardRepository).save(expectedCard);
  }
}
