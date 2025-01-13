package com.thepapiok.multiplecard.services;

import com.google.zxing.WriterException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class CardService {
  private final AccountRepository accountRepository;
  private final CardRepository cardRepository;
  private final CloudinaryService cloudinaryService;
  private final MongoTemplate mongoTemplate;
  private final MongoTransactionManager mongoTransactionManager;
  private final QrCodeService qrCodeService;
  private final ImageService imageService;
  private final EmailService emailService;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${app.url}")
  private String appUrl;

  @Autowired
  public CardService(
      AccountRepository accountRepository,
      CardRepository cardRepository,
      CloudinaryService cloudinaryService,
      MongoTemplate mongoTemplate,
      MongoTransactionManager mongoTransactionManager,
      QrCodeService qrCodeService,
      ImageService imageService,
      EmailService emailService,
      UserRepository userRepository,
      PasswordEncoder passwordEncoder) {
    this.accountRepository = accountRepository;
    this.cardRepository = cardRepository;
    this.cloudinaryService = cloudinaryService;
    this.mongoTemplate = mongoTemplate;
    this.mongoTransactionManager = mongoTransactionManager;
    this.qrCodeService = qrCodeService;
    this.imageService = imageService;
    this.emailService = emailService;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public Card getCard(String phone) {
    return cardRepository.findCardByUserId(accountRepository.findIdByPhone(phone).getId());
  }

  public boolean createCard(String phone, String cardId, String encryptedPin, String name) {
    TransactionTemplate transactionTemplate = new TransactionTemplate(mongoTransactionManager);
    try {
      transactionTemplate.execute(
          new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
              Card existsCard = getCard(phone);
              if (existsCard != null) {
                try {
                  cloudinaryService.deleteImage(existsCard.getId().toString());
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
                mongoTemplate.remove(existsCard);
              }
              Account account = accountRepository.findByPhone(phone);
              Card card = new Card();
              card.setName(name);
              card.setPin(encryptedPin);
              card.setId(new ObjectId(cardId));
              card.setAttempts(0);
              card.setUserId(account.getId());
              card.setImageUrl("");
              try {
                byte[] qrCode = qrCodeService.generateQrCode(appUrl + "cards?id=" + cardId);
                card.setImageUrl(cloudinaryService.addImage(qrCode, cardId));
                List<CustomMultipartFile> cardImage =
                    imageService.generateImage(qrCode, name, cardId);
                Optional<User> optionalUser = userRepository.findById(account.getId());
                if (optionalUser.isEmpty()) {
                  throw new RuntimeException();
                }
                User user = optionalUser.get();
                emailService.sendCardImage(cardImage, cardId, account, user);
                card = mongoTemplate.save(card);
                user.setCardId(card.getId());
                mongoTemplate.save(user);
              } catch (WriterException | IOException e) {
                throw new RuntimeException(e);
              } catch (MessagingException e) {
                throw new RuntimeException(e);
              }
            }
          });

    } catch (Exception e) {
      return false;
    }
    return true;
  }

  public boolean blockCard(String phone) {
    try {
      final int maxAttempts = 3;
      Card card = getCard(phone);
      card.setAttempts(maxAttempts);
      cardRepository.save(card);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  public boolean isBlocked(String phone) {
    final int maxAttempts = 3;
    return getCard(phone).getAttempts() == maxAttempts;
  }

  public boolean isBlocked(ObjectId cardID) {
    final int maxAttempts = 3;
    Optional<Card> optionalCard = cardRepository.findById(cardID);
    if (optionalCard.isEmpty()) {
      return true;
    }
    Card card = optionalCard.get();
    return card.getAttempts() == maxAttempts;
  }

  public boolean cardExists(String cardId) {
    try {
      return cardRepository.existsCardById(new ObjectId(cardId));
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isOwner(String phone, String cardId) {
    Optional<Card> optionalCard = cardRepository.findById(new ObjectId(cardId));
    if (optionalCard.isEmpty()) {
      return false;
    }
    Card card = optionalCard.get();
    return accountRepository.findPhoneById(card.getUserId()).getPhone().equals(phone);
  }

  public boolean checkIdAndNameIsValid(SearchCardDTO searchCardDTO) {
    return cardRepository.existsCardByIdAndName(
        new ObjectId(searchCardDTO.getCardId()), searchCardDTO.getCardName());
  }

  public boolean checkPin(String cardId, String pin) {
    Optional<Card> optionalCard = cardRepository.findById(new ObjectId(cardId));
    if (optionalCard.isEmpty()) {
      return false;
    }
    Card card = optionalCard.get();
    boolean result = passwordEncoder.matches(pin, card.getPin());
    if (!result) {
      card.setAttempts(card.getAttempts() + 1);
      cardRepository.save(card);
      return false;
    }
    return true;
  }
}
