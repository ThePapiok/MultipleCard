package com.thepapiok.multiplecard.services;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
  private final AccountRepository accountRepository;

  @Autowired
  public UserService(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Account account = accountRepository.findByPhone(username);
    if (account == null) {
      throw new UsernameNotFoundException("Błędny login lub hasło");
    }
    return new org.springframework.security.core.userdetails.User(
        account.getPhone(),
        account.getPassword(),
        Collections.singleton(new SimpleGrantedAuthority(account.getRole().name())));
  }
}