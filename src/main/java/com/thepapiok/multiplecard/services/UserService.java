package com.thepapiok.multiplecard.services;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Role;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.exceptions.BannedException;
import com.thepapiok.multiplecard.exceptions.NotActiveException;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.UserRepository;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
  private final AccountRepository accountRepository;
  private final UserRepository userRepository;
  private final MessageSource messageSource;
  private final AdminPanelService adminPanelService;

  @Autowired
  public UserService(
      AccountRepository accountRepository,
      UserRepository userRepository,
      MessageSource messageSource,
      AdminPanelService adminPanelService) {
    this.accountRepository = accountRepository;
    this.userRepository = userRepository;
    this.messageSource = messageSource;
    this.adminPanelService = adminPanelService;
  }

  @Override
  public UserDetails loadUserByUsername(String username)
      throws UsernameNotFoundException, NotActiveException {
    Account account = accountRepository.findByPhone(username);
    if (account == null) {
      throw new UsernameNotFoundException("");
    }
    if (!account.isActive()) {
      throw new NotActiveException("");
    }
    if (account.isBanned()) {
      throw new BannedException("");
    }
    return new org.springframework.security.core.userdetails.User(
        account.getPhone(),
        account.getPassword(),
        Collections.singleton(new SimpleGrantedAuthority(account.getRole().name())));
  }

  public boolean checkIsRestricted(String phone) {
    Optional<User> optionalUser =
        userRepository.findById(accountRepository.findIdByPhone(phone).getId());
    if (optionalUser.isEmpty()) {
      return true;
    }
    User user = optionalUser.get();
    return user.isRestricted();
  }

  public String changeRestricted(String id, boolean value, Locale locale) {
    try {
      ObjectId objectId = new ObjectId(id);
      Optional<User> optionalUser = userRepository.findById(objectId);
      if (optionalUser.isEmpty()) {
        return messageSource.getMessage("error.user_not_found", null, locale);
      }
      User user = optionalUser.get();
      Optional<Account> optionalAccount = accountRepository.findById(objectId);
      if (optionalAccount.isEmpty()) {
        return messageSource.getMessage("error.user_not_found", null, locale);
      }
      Account account = optionalAccount.get();
      if (user.isRestricted() == value) {
        return messageSource.getMessage("error.user_already_has", null, locale);
      } else if (!account.getRole().equals(Role.ROLE_USER)) {
        return messageSource.getMessage("error.bad_role", null, locale);
      }
      user.setRestricted(value);
      userRepository.save(user);
      if (value) {
        adminPanelService.sendInfoAboutMutedUser(account.getEmail(), account.getPhone(), id);
      } else {
        adminPanelService.sendInfoAboutUnmutedUser(account.getEmail(), account.getPhone(), id);
      }
    } catch (Exception e) {
      return messageSource.getMessage("error.unexpected", null, locale);
    }
    return "ok";
  }
}
