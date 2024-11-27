package com.thepapiok.multiplecard.services;

import com.google.zxing.WriterException;
import com.thepapiok.multiplecard.collections.Card;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.CardRepository;
import java.io.IOException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
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

  @Value("${app.url}")
  private String appUrl;

  @Autowired
  public CardService(
      AccountRepository accountRepository,
      CardRepository cardRepository,
      CloudinaryService cloudinaryService,
      MongoTemplate mongoTemplate,
      MongoTransactionManager mongoTransactionManager,
      QrCodeService qrCodeService) {
    this.accountRepository = accountRepository;
    this.cardRepository = cardRepository;
    this.cloudinaryService = cloudinaryService;
    this.mongoTemplate = mongoTemplate;
    this.mongoTransactionManager = mongoTransactionManager;
    this.qrCodeService = qrCodeService;
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
              Card card = new Card();
              card.setName(name);
              card.setPin(encryptedPin);
              card.setId(new ObjectId(cardId));
              card.setAttempts(0);
              card.setUserId(accountRepository.findIdByPhone(phone).getId());
              card.setImageUrl("");
              try {
                card.setImageUrl(
                    cloudinaryService.addImage(
                        qrCodeService.generateQrCode(appUrl + "cards?id=" + cardId), cardId));
              } catch (WriterException | IOException e) {
                throw new RuntimeException(e);
              }
              mongoTemplate.save(card);
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
    return getCard(phone).getAttempts() != maxAttempts;
  }

  public boolean cardExists(String cardId) {
    try {
      return cardRepository.existsCardById(new ObjectId(cardId));
    } catch (Exception e) {
      return false;
    }
  }
}
