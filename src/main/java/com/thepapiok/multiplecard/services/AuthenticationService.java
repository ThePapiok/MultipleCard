package com.thepapiok.multiplecard.services;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Role;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.RegisterDTO;
import com.thepapiok.multiplecard.misc.AccountConverter;
import com.thepapiok.multiplecard.misc.UserConverter;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.UserRepository;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {

  private final AccountRepository accountRepository;
  private final UserRepository userRepository;
  private final UserConverter userConverter;
  private final AccountConverter accountConverter;

  private Random random;

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
    random = new Random();
  }

  @Transactional
  public void createUser(RegisterDTO register) {
    User user = userConverter.getEntity(register);
    user.setCard(null);
    user.setPoints(0);
    user.setReview(null);
    user = userRepository.save(user);
    Account account = accountConverter.getEntity(register);
    account.setId(user.getId());
    account.setRole(Role.ROLE_USER);
    account.setActive(true);
    accountRepository.save(account);
  }

  public List<String> getPhones() {
    try {
      return accountRepository.findAllPhones().stream().map(Account::getPhone).toList();
    } catch (Exception e) {
      return null;
    }
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
}
