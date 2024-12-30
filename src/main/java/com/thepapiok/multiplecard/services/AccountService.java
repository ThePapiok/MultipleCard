package com.thepapiok.multiplecard.services;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Role;
import com.thepapiok.multiplecard.collections.Shop;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.UserDTO;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.ShopRepository;
import com.thepapiok.multiplecard.repositories.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
  private final AccountRepository accountRepository;
  private final UserRepository userRepository;
  private final ShopRepository shopRepository;

  @Autowired
  public AccountService(
      AccountRepository accountRepository,
      UserRepository userRepository,
      ShopRepository shopRepository) {
    this.accountRepository = accountRepository;
    this.userRepository = userRepository;
    this.shopRepository = shopRepository;
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

  public boolean changeActive(String id, boolean value) {
    try {
      Optional<Account> optionalAccount = accountRepository.findById(new ObjectId(id));
      if (optionalAccount.isEmpty()) {
        return false;
      }
      Account account = optionalAccount.get();
      account.setActive(value);
      accountRepository.save(account);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  public boolean changeBanned(String id, boolean value) {
    try {
      Optional<Account> optionalAccount = accountRepository.findById(new ObjectId(id));
      if (optionalAccount.isEmpty()) {
        return false;
      }
      Account account = optionalAccount.get();
      account.setBanned(value);
      accountRepository.save(account);
    } catch (Exception e) {
      return false;
    }
    return true;
  }
}
