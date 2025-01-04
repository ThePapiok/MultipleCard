package com.thepapiok.multiplecard.services;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Role;
import com.thepapiok.multiplecard.dto.PageUserDTO;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.AggregationRepository;
import com.thepapiok.multiplecard.repositories.CategoryRepository;
import com.thepapiok.multiplecard.repositories.ProductRepository;
import java.util.Locale;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
  private final String USER_NOT_FOUND_PARAM = "error.user.not_found";
  private final String USER_ALREADY_HAS_PARAM = "adminPanel.error.user_already_has";
  private final String UNEXPECTED_PARAM = "error.unexpected";
  private final String OK_MESSAGE = "ok";
  private final AccountRepository accountRepository;
  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final MessageSource messageSource;
  private final AdminPanelService adminPanelService;
  private final AggregationRepository aggregationRepository;

  @Autowired
  public AccountService(
      AccountRepository accountRepository,
      ProductRepository productRepository,
      CategoryRepository categoryRepository,
      MessageSource messageSource,
      AdminPanelService adminPanelService,
      AggregationRepository aggregationRepository) {
    this.accountRepository = accountRepository;
    this.productRepository = productRepository;
    this.categoryRepository = categoryRepository;
    this.messageSource = messageSource;
    this.adminPanelService = adminPanelService;
    this.aggregationRepository = aggregationRepository;
  }

  public PageUserDTO getCurrentPage(String type, String value, int page) {
    return aggregationRepository.getUsers(type, value, page);
  }

  public String changeActive(String id, boolean value, Locale locale) {
    try {
      Optional<Account> optionalAccount = accountRepository.findById(new ObjectId(id));
      if (optionalAccount.isEmpty()) {
        return messageSource.getMessage(USER_NOT_FOUND_PARAM, null, locale);
      }
      Account account = optionalAccount.get();
      if (account.isActive() == value) {
        return messageSource.getMessage(USER_ALREADY_HAS_PARAM, null, locale);
      }
      account.setActive(value);
      accountRepository.save(account);
      if (value) {
        adminPanelService.sendInfoAboutActivatedUser(account.getEmail(), account.getPhone(), id);
      } else {
        adminPanelService.sendInfoAboutDeactivatedUser(account.getEmail(), account.getPhone(), id);
      }
    } catch (Exception e) {
      return messageSource.getMessage(UNEXPECTED_PARAM, null, locale);
    }
    return OK_MESSAGE;
  }

  public String changeBanned(String id, boolean value, Locale locale) {
    try {
      Optional<Account> optionalAccount = accountRepository.findById(new ObjectId(id));
      if (optionalAccount.isEmpty()) {
        return messageSource.getMessage(USER_NOT_FOUND_PARAM, null, locale);
      }
      Account account = optionalAccount.get();
      if (account.isBanned() == value) {
        return messageSource.getMessage(USER_ALREADY_HAS_PARAM, null, locale);
      }
      account.setBanned(value);
      accountRepository.save(account);
      if (value) {
        adminPanelService.sendInfoAboutBlockedUser(account.getEmail(), account.getPhone(), id);
      } else {
        adminPanelService.sendInfoAboutUnblockedUser(account.getEmail(), account.getPhone(), id);
      }
    } catch (Exception e) {
      return messageSource.getMessage(UNEXPECTED_PARAM, null, locale);
    }
    return OK_MESSAGE;
  }

  public Account getAccountByProductId(String productId) {
    return productRepository.findAccountByProductId(new ObjectId(productId));
  }

  public Account getAccountById(String id) {
    return accountRepository.findAccountById(new ObjectId(id));
  }

  public Account getAccountByCategoryName(String name) {
    return categoryRepository.findAccountByCategoryName(name);
  }

  public String changeRole(String id, String role, Locale locale) {
    try {
      Optional<Account> optionalAccount = accountRepository.findById(new ObjectId(id));
      if (optionalAccount.isEmpty()) {
        return messageSource.getMessage(USER_NOT_FOUND_PARAM, null, locale);
      }
      Role newRole = Role.valueOf(role);
      Account account = optionalAccount.get();
      if (account.getRole().equals(newRole)) {
        return messageSource.getMessage(USER_ALREADY_HAS_PARAM, null, locale);
      } else if (account.getRole().equals(Role.ROLE_SHOP)) {
        return messageSource.getMessage("adminPanel.error.bad_role", null, locale);
      }
      account.setRole(newRole);
      accountRepository.save(account);
      if (newRole.equals(Role.ROLE_ADMIN)) {
        adminPanelService.sendInfoAboutChangeUserToAdmin(
            account.getEmail(), account.getPhone(), id);
      } else {
        adminPanelService.sendInfoAboutChangeAdminToUser(
            account.getEmail(), account.getPhone(), id);
      }
    } catch (Exception e) {
      return messageSource.getMessage(UNEXPECTED_PARAM, null, locale);
    }
    return OK_MESSAGE;
  }
}
