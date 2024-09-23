package com.thepapiok.multiplecard.services;

import com.google.zxing.WriterException;
import com.thepapiok.multiplecard.collections.Card;
import com.thepapiok.multiplecard.dto.OrderCardDTO;
import com.thepapiok.multiplecard.misc.CardConverter;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.CardRepository;
import java.io.IOException;
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
  private final CardConverter cardConverter;
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
      CardConverter cardConverter,
      CloudinaryService cloudinaryService,
      MongoTemplate mongoTemplate,
      MongoTransactionManager mongoTransactionManager,
      QrCodeService qrCodeService) {
    this.accountRepository = accountRepository;
    this.cardRepository = cardRepository;
    this.cardConverter = cardConverter;
    this.cloudinaryService = cloudinaryService;
    this.mongoTemplate = mongoTemplate;
    this.mongoTransactionManager = mongoTransactionManager;
    this.qrCodeService = qrCodeService;
  }

  public Card getCard(String phone) {
    return cardRepository.findCardByUserId(accountRepository.findIdByPhone(phone).getId());
  }

  public boolean createCard(OrderCardDTO cardDTO, String phone) {
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
              String id;
              Card card = cardConverter.getEntity(cardDTO);
              card.setAttempts(0);
              card.setUserId(accountRepository.findIdByPhone(phone).getId());
              card.setImageUrl("");
              card = mongoTemplate.save(card);
              id = card.getId().toString();
              try {
                card.setImageUrl(
                    cloudinaryService.addImage(
                        qrCodeService.generateQrCode(appUrl + "card?id=" + id), id));
              } catch (WriterException | IOException e) {
                throw new RuntimeException(e);
              }
              mongoTemplate.save(card);
            }
          });

    } catch (Exception e) {
      System.out.println(e);
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
}
