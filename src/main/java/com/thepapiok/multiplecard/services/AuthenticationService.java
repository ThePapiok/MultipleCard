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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {

  private final AccountRepository accountRepository;
  private final UserRepository userRepository;
  private final UserConverter userConverter;
  private final AccountConverter accountConverter;
  private final PasswordEncoder passwordEncoder;

  private Random random;

  @Autowired
  public AuthenticationService(
      AccountRepository accountRepository,
      UserRepository userRepository,
      UserConverter userConverter,
      AccountConverter accountConverter,
      PasswordEncoder passwordEncoder) {
    this.accountRepository = accountRepository;
    this.userRepository = userRepository;
    this.userConverter = userConverter;
    this.accountConverter = accountConverter;
    this.passwordEncoder = passwordEncoder;
    random = new Random();
  }

  @Transactional
  public boolean createUser(RegisterDTO register) {
    try {
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
      return true;
    } catch (Exception e) {
      System.out.println(e);
      return false;
    }
  }

  public List<String> getPhones() {
    try {
      return accountRepository.findAllPhones().stream().map(Account::getPhone).toList();
    } catch (Exception e) {
      return List.of();
    }
  }

  public List<String> getEmails() {
    try {
      return accountRepository.findAllEmails().stream().map(Account::getEmail).toList();
    } catch (Exception e) {
      return List.of();
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
}
