package com.thepapiok.multiplecard.services;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.dto.ReviewGetDTO;
import com.thepapiok.multiplecard.exceptions.NotActiveException;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.UserRepository;
import java.util.Collections;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
  private final AccountRepository accountRepository;
  private final UserRepository userRepository;

  @Autowired
  public UserService(AccountRepository accountRepository, UserRepository userRepository) {
    this.accountRepository = accountRepository;
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username)
      throws UsernameNotFoundException, NotActiveException {
    Account account = accountRepository.findByPhone(username);
    if (account == null) {
      throw new UsernameNotFoundException("Błędny login lub hasło");
    }
    if (!account.isActive()) {
      throw new NotActiveException("Konto nie jest aktywowane");
    }
    return new org.springframework.security.core.userdetails.User(
        account.getPhone(),
        account.getPassword(),
        Collections.singleton(new SimpleGrantedAuthority(account.getRole().name())));
  }

  public List<ReviewGetDTO> getReviewsFirst3(String phone) {
    try {
      final int maxSize = 3;
      List<ReviewGetDTO> reviewGetDTOS =
          userRepository.findAllReviewWithCountAndIsAddedCheck(
              new ObjectId(accountRepository.findIdByPhone(phone).getId()));
      return reviewGetDTOS.subList(0, Math.min(maxSize, reviewGetDTOS.size()));
    } catch (Exception e) {
      return null;
    }
  }
}
