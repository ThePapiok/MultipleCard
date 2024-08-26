package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Role;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserServiceTest {
  private UserService userService;
  @Mock private AccountRepository accountRepository;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    userService = new UserService(accountRepository);
  }

  @Test
  public void shouldSuccessLoadUserByUsername() {
    final String phone = "+4823423411423";
    final String password = "123wefasdfasd123bsedf";
    Account expectedAccount = new Account();
    expectedAccount.setPhone(phone);
    expectedAccount.setId("123123dfasdf");
    expectedAccount.setShop(false);
    expectedAccount.setActive(true);
    expectedAccount.setRole(Role.ROLE_USER);
    expectedAccount.setPassword(password);
    User user =
        new User(
            phone,
            password,
            Collections.singleton(new SimpleGrantedAuthority(Role.ROLE_USER.name())));

    when(accountRepository.findByPhone(phone)).thenReturn(expectedAccount);

    assertEquals(user, userService.loadUserByUsername(phone));
  }

  @Test
  public void shouldFailLoadUserByUsername() {
    final String phone = "+4823423411423";

    when(accountRepository.findByPhone(phone)).thenReturn(null);

    assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(phone));
  }
}
