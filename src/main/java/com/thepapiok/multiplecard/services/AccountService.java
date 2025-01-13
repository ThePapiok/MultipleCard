package com.thepapiok.multiplecard.services;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Order;
import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.collections.Role;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.PageUserDTO;
import com.thepapiok.multiplecard.misc.ProductInfo;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.AggregationRepository;
import com.thepapiok.multiplecard.repositories.CategoryRepository;
import com.thepapiok.multiplecard.repositories.OrderRepository;
import com.thepapiok.multiplecard.repositories.ProductRepository;
import com.thepapiok.multiplecard.repositories.ReportRepository;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class AccountService {
  private final String USER_NOT_FOUND_PARAM = "error.user_not_found";
  private final String USER_ALREADY_HAS_PARAM = "error.user_already_has";
  private final String UNEXPECTED_PARAM = "error.unexpected";
  private final String OK_MESSAGE = "ok";
  private final AccountRepository accountRepository;
  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final MessageSource messageSource;
  private final AdminPanelService adminPanelService;
  private final AggregationRepository aggregationRepository;
  private final MongoTransactionManager mongoTransactionManager;
  private final MongoTemplate mongoTemplate;
  private final OrderRepository orderRepository;
  private final ReportRepository reportRepository;

  @Autowired
  public AccountService(
      AccountRepository accountRepository,
      ProductRepository productRepository,
      CategoryRepository categoryRepository,
      MessageSource messageSource,
      AdminPanelService adminPanelService,
      AggregationRepository aggregationRepository,
      MongoTransactionManager mongoTransactionManager,
      MongoTemplate mongoTemplate,
      OrderRepository orderRepository,
      ReportRepository reportRepository) {
    this.accountRepository = accountRepository;
    this.productRepository = productRepository;
    this.categoryRepository = categoryRepository;
    this.messageSource = messageSource;
    this.adminPanelService = adminPanelService;
    this.aggregationRepository = aggregationRepository;
    this.mongoTransactionManager = mongoTransactionManager;
    this.mongoTemplate = mongoTemplate;
    this.orderRepository = orderRepository;
    this.reportRepository = reportRepository;
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
      final float centsPerZloty = 100;
      TransactionTemplate transactionTemplate = new TransactionTemplate(mongoTransactionManager);
      return transactionTemplate.execute(
          new TransactionCallback<String>() {
            @Override
            public String doInTransaction(TransactionStatus status) {
              ObjectId accountId = new ObjectId(id);
              Optional<Account> optionalAccount = accountRepository.findById(accountId);
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
                if (account.getRole().equals(Role.ROLE_SHOP)) {
                  for (ObjectId productId :
                      productRepository.getProductsIdByShopId(accountId).stream()
                          .map(Product::getId)
                          .toList()) {
                    reportRepository.deleteAllByReportedId(productId);
                    List<Order> orders =
                        orderRepository.findAllByProductIdAndIsUsed(productId, false);
                    for (Order order : orders) {
                      mongoTemplate.updateFirst(
                          query(where("cardId").is(order.getCardId())),
                          new Update().inc("points", Math.round(order.getPrice() / centsPerZloty)),
                          User.class);
                      mongoTemplate.remove(order);
                    }
                  }
                } else {
                  reportRepository.deleteAllByReportedId(accountId);
                }
                adminPanelService.sendInfoAboutBlockedUser(
                    account.getEmail(), account.getPhone(), id);
              } else {
                adminPanelService.sendInfoAboutUnblockedUser(
                    account.getEmail(), account.getPhone(), id);
              }
              return OK_MESSAGE;
            }
          });
    } catch (Exception e) {
      return messageSource.getMessage(UNEXPECTED_PARAM, null, locale);
    }
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
        return messageSource.getMessage("error.bad_role", null, locale);
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

  public boolean checkAnyShopIsBanned(Map<ProductInfo, Integer> productsInfo) {
    for (ProductInfo productInfo : productsInfo.keySet()) {
      Account account = productRepository.findAccountByProductId(productInfo.getProductId());
      if (account == null || account.isBanned()) {
        return true;
      }
    }
    return false;
  }

  public boolean checkUserIsBanned(ObjectId id) {
    Optional<Account> optionalAccount = accountRepository.findById(id);
    if (optionalAccount.isEmpty()) {
      return true;
    }
    return optionalAccount.get().isBanned();
  }
}
