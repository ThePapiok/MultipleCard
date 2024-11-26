package com.thepapiok.multiplecard.services;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.ProductRepository;
import java.util.Locale;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class BlockedProductService {
  private final EmailService emailService;
  private final AccountRepository accountRepository;
  private final ProductRepository productRepository;
  private final MessageSource messageSource;

  @Autowired
  public BlockedProductService(
      EmailService emailService,
      AccountRepository accountRepository,
      ProductRepository productRepository,
      MessageSource messageSource) {
    this.emailService = emailService;
    this.accountRepository = accountRepository;
    this.productRepository = productRepository;
    this.messageSource = messageSource;
  }

  public void sendWarning(ObjectId productId, String messageParam) {
    Optional<Product> optionalProduct = productRepository.findById(productId);
    if (optionalProduct.isEmpty()) {
      return;
    }
    Product product = optionalProduct.get();
    Optional<Account> optionalAccount = accountRepository.findById(product.getShopId());
    if (optionalAccount.isEmpty()) {
      return;
    }
    Account account = optionalAccount.get();
    Locale locale;
    if (account.getPhone().startsWith("+48")) {
      locale = Locale.getDefault();
    } else {
      locale = new Locale.Builder().setLanguage("eng").build();
    }
    emailService.sendEmail(
        messageSource.getMessage(messageParam, null, locale),
        account.getEmail(),
        messageSource.getMessage("blockedProductService.warning.title", null, locale)
            + " - "
            + product.getName());
  }
}
