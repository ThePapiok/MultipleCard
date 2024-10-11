package com.thepapiok.multiplecard.services;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Role;
import com.thepapiok.multiplecard.collections.Shop;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.RegisterDTO;
import com.thepapiok.multiplecard.dto.RegisterShopDTO;
import com.thepapiok.multiplecard.misc.AccountConverter;
import com.thepapiok.multiplecard.misc.ShopConverter;
import com.thepapiok.multiplecard.misc.UserConverter;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AuthenticationService {

  private final AccountRepository accountRepository;
  private final UserConverter userConverter;
  private final AccountConverter accountConverter;
  private final PasswordEncoder passwordEncoder;
  private final MongoTransactionManager mongoTransactionManager;
  private final MongoTemplate mongoTemplate;
  private final ShopConverter shopConverter;
  private final CloudinaryService cloudinaryService;
  private final EmailService emailService;
  private Random random;

  @Autowired
  public AuthenticationService(
      AccountRepository accountRepository,
      UserConverter userConverter,
      AccountConverter accountConverter,
      PasswordEncoder passwordEncoder,
      MongoTransactionManager mongoTransactionManager,
      MongoTemplate mongoTemplate,
      ShopConverter shopConverter,
      CloudinaryService cloudinaryService,
      EmailService emailService) {
    this.accountRepository = accountRepository;
    this.userConverter = userConverter;
    this.accountConverter = accountConverter;
    this.passwordEncoder = passwordEncoder;
    this.mongoTransactionManager = mongoTransactionManager;
    this.mongoTemplate = mongoTemplate;
    this.shopConverter = shopConverter;
    this.cloudinaryService = cloudinaryService;
    this.emailService = emailService;
    random = new Random();
  }

  public boolean createUser(RegisterDTO register) {
    TransactionTemplate transactionTemplate = new TransactionTemplate(mongoTransactionManager);
    try {
      transactionTemplate.execute(
          new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
              User user = userConverter.getEntity(register);
              user.setCardId(null);
              user.setPoints(0);
              user.setReview(null);
              user = mongoTemplate.save(user);
              Account account = accountConverter.getEntity(register);
              account.setId(user.getId());
              account.setRole(Role.ROLE_USER);
              account.setActive(true);
              account.setBanned(false);
              mongoTemplate.save(account);
            }
          });
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  public boolean phoneExists(String phone) {
    return accountRepository.existsByPhone(phone);
  }

  public boolean emailExists(String email) {
    return accountRepository.existsByEmail(email);
  }

  public String getVerificationNumber() {
    final int bound = 10;
    final int forBound = 3;
    StringBuilder verificationNumber = new StringBuilder();
    for (int i = 1; i <= forBound; i++) {
      verificationNumber.append(random.nextInt(bound));
    }
    verificationNumber.append(" ");
    for (int i = 1; i <= forBound; i++) {
      verificationNumber.append(random.nextInt(bound));
    }
    return verificationNumber.toString();
  }

  @Profile("test")
  public void setRandom(Random random) {
    this.random = random;
  }

  public boolean changePassword(String phone, String password) {
    try {
      Account account = accountRepository.findByPhone(phone);
      account.setPassword(passwordEncoder.encode(password));
      accountRepository.save(account);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public boolean getAccountByPhone(String phone) {
    return accountRepository.findByPhone(phone) != null;
  }

  public boolean checkPassword(String password, String phone) {
    return passwordEncoder.matches(
        password, accountRepository.findPasswordByPhone(phone).getPassword());
  }

  public boolean createShop(
      RegisterShopDTO registerShopDTO,
      String filePath,
      List<MultipartFile> fileList,
      Locale locale) {
    TransactionTemplate transactionTemplate = new TransactionTemplate(mongoTransactionManager);
    final String[] id = new String[1];
    try {
      transactionTemplate.execute(
          new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
              Shop shop = shopConverter.getEntity(registerShopDTO);
              shop.setTotalAmount(0L);
              shop = mongoTemplate.save(shop);
              try {
                Path path = Path.of(filePath);
                shop.setImageUrl(
                    cloudinaryService.addImage(
                        Files.readAllBytes(path), shop.getId().toHexString()));
                Files.deleteIfExists(path);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
              mongoTemplate.save(shop);
              id[0] = shop.getId().toString();
              Account account = accountConverter.getEntity(registerShopDTO);
              account.setId(shop.getId());
              account.setRole(Role.ROLE_SHOP);
              account.setActive(false);
              account.setBanned(false);
              mongoTemplate.save(account);
              try {
                emailService.sendEmailWithAttachment(shop, account, locale, fileList);
              } catch (MessagingException e) {
                throw new RuntimeException(e);
              }
            }
          });
    } catch (Exception e) {
      try {
        cloudinaryService.deleteImage(id[0]);
      } catch (IOException ignored) {
      }
      return false;
    }
    return true;
  }
}
