package com.thepapiok.multiplecard.services;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Role;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.RegisterDTO;
import com.thepapiok.multiplecard.misc.AccountConverter;
import com.thepapiok.multiplecard.misc.UserConverter;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {

  private final AccountRepository accountRepository;
  private final UserRepository userRepository;
  private final UserConverter userConverter;
  private final AccountConverter accountConverter;

  @Autowired
  public AuthenticationService(
      AccountRepository accountRepository,
      UserRepository userRepository,
      UserConverter userConverter,
      AccountConverter accountConverter) {
    this.accountRepository = accountRepository;
    this.userRepository = userRepository;
    this.userConverter = userConverter;
    this.accountConverter = accountConverter;
  }

  @Transactional
  public void createUser(RegisterDTO register) {
    User user = userConverter.getEntity(register);
    user.setCard(null);
    user.setPoints(0);
    user.setReview(null);
    user = userRepository.save(user);
    Account account = accountConverter.getEntity(register);
    // TODO add encode password
    account.setId(user.getId());
    account.setRole(Role.ROLE_USER);
    account.setActive(true);
    // TODO add verificiation
    account.setVerificationNumber(null);
    account.setShop(false);
    accountRepository.save(account);
  }
}
