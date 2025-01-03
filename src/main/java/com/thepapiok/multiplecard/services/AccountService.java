package com.thepapiok.multiplecard.services;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Role;
import com.thepapiok.multiplecard.collections.Shop;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.UserDTO;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.CategoryRepository;
import com.thepapiok.multiplecard.repositories.ProductRepository;
import com.thepapiok.multiplecard.repositories.ShopRepository;
import com.thepapiok.multiplecard.repositories.UserRepository;
import java.util.ArrayList;
import java.util.List;
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
  private final UserRepository userRepository;
  private final ShopRepository shopRepository;
  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final MessageSource messageSource;
  private final AdminPanelService adminPanelService;

  @Autowired
  public AccountService(
      AccountRepository accountRepository,
      UserRepository userRepository,
      ShopRepository shopRepository,
      ProductRepository productRepository,
      CategoryRepository categoryRepository,
      MessageSource messageSource,
      AdminPanelService adminPanelService) {
    this.accountRepository = accountRepository;
    this.userRepository = userRepository;
    this.shopRepository = shopRepository;
    this.productRepository = productRepository;
    this.categoryRepository = categoryRepository;
    this.messageSource = messageSource;
    this.adminPanelService = adminPanelService;
  }

  public List<UserDTO> getUsers(Integer type, String value) {
    final int type0 = 0;
    final int type1 = 1;
    final int type2 = 2;
    final int type3 = 3;
    final int type4 = 4;
    final int type5 = 5;
    final int type6 = 6;
    try {
      List<Account> accounts = accountRepository.findAll();
      List<UserDTO> userDTOS = new ArrayList<>();
      for (Account account : accounts) {
        Shop shop = null;
        User user = null;
        boolean isShop = false;
        if (Role.ROLE_SHOP.equals(account.getRole())) {
          isShop = true;
          Optional<Shop> optionalShop = shopRepository.findById(account.getId());
          if (optionalShop.isEmpty()) {
            return List.of();
          }
          shop = optionalShop.get();
        } else {
          Optional<User> optionalUser = userRepository.findById(account.getId());
          if (optionalUser.isEmpty()) {
            return List.of();
          }
          user = optionalUser.get();
        }
        if (type != null) {
          switch (type) {
            case type0:
              if (!account.getId().toHexString().equals(value)) {
                continue;
              }
              break;
            case type1:
              if ((isShop && !shop.getFirstName().equals(value))
                  || (!isShop && !user.getFirstName().equals(value))) {
                continue;
              }
              break;
            case type2:
              if ((isShop && !shop.getLastName().equals(value))
                  || (!isShop && !user.getLastName().equals(value))) {
                continue;
              }
              break;
            case type3:
              if (!account.getPhone().equals(value)) {
                continue;
              }
              break;
            case type4:
              if (!account.getRole().name().equals(value)) {
                continue;
              }
              break;
            case type5:
              if (!String.valueOf(account.isActive()).equals(value)) {
                continue;
              }
              break;
            case type6:
              if (!String.valueOf(account.isBanned()).equals(value)) {
                continue;
              }
              break;
            default:
              break;
          }
        }
        UserDTO userDTO = new UserDTO();
        userDTO.setId(account.getId().toString());
        userDTO.setPhone(account.getPhone());
        userDTO.setActive(account.isActive());
        userDTO.setBanned(account.isBanned());
        userDTO.setRole(account.getRole().name());
        if (isShop) {
          userDTO.setFirstName(shop.getFirstName());
          userDTO.setLastName(shop.getLastName());
        } else {
          userDTO.setFirstName(user.getFirstName());
          userDTO.setLastName(user.getLastName());
        }
        userDTOS.add(userDTO);
      }
      return userDTOS;
    } catch (Exception e) {
      return List.of();
    }
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
