package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Role;
import com.thepapiok.multiplecard.exceptions.BannedException;
import com.thepapiok.multiplecard.exceptions.NotActiveException;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import java.util.Collections;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserServiceTest {

  private static final String TEST_PHONE = "+4823423411423";
  private static final String TEST_EMAIL = "email";
  private static final String TEST_PASSWORD = "123wefasdfasd123bsedf";
  private static final ObjectId TEST_ID = new ObjectId("123456789012345678901234");

  private UserService userService;
  @Mock private AccountRepository accountRepository;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    userService = new UserService(accountRepository);
  }

  @Test
  public void shouldSuccessAtLoadUserByUsernameWhenEverythingOk() {
    Account expectedAccount = new Account();
    expectedAccount.setPhone(TEST_PHONE);
    expectedAccount.setId(TEST_ID);
    expectedAccount.setEmail(TEST_EMAIL);
    expectedAccount.setActive(true);
    expectedAccount.setBanned(false);
    expectedAccount.setRole(Role.ROLE_USER);
    expectedAccount.setPassword(TEST_PASSWORD);
    User user =
        new User(
            TEST_PHONE,
            TEST_PASSWORD,
            Collections.singleton(new SimpleGrantedAuthority(Role.ROLE_USER.name())));

    when(accountRepository.findByPhone(TEST_PHONE)).thenReturn(expectedAccount);

    assertEquals(user, userService.loadUserByUsername(TEST_PHONE));
  }

  @Test
  public void shouldFailAtLoadUserByUsernameWhenUserNotFound() {

    when(accountRepository.findByPhone(TEST_PHONE)).thenReturn(null);

    assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(TEST_PHONE));
  }

  @Test
  public void shouldFailAtLoadUserByUsernameWhenUserNotActive() {
    Account expectedAccount = new Account();
    expectedAccount.setPhone(TEST_PHONE);
    expectedAccount.setId(TEST_ID);
    expectedAccount.setEmail(TEST_EMAIL);
    expectedAccount.setActive(false);
    expectedAccount.setBanned(false);
    expectedAccount.setRole(Role.ROLE_USER);
    expectedAccount.setPassword(TEST_PASSWORD);

    when(accountRepository.findByPhone(TEST_PHONE)).thenReturn(expectedAccount);

    assertThrows(NotActiveException.class, () -> userService.loadUserByUsername(TEST_PHONE));
  }

  @Test
  public void shouldFailAtLoadUserByUsernameWhenUserBanned() {
    Account expectedAccount = new Account();
    expectedAccount.setPhone(TEST_PHONE);
    expectedAccount.setId(TEST_ID);
    expectedAccount.setEmail(TEST_EMAIL);
    expectedAccount.setActive(true);
    expectedAccount.setBanned(true);
    expectedAccount.setRole(Role.ROLE_USER);
    expectedAccount.setPassword(TEST_PASSWORD);

    when(accountRepository.findByPhone(TEST_PHONE)).thenReturn(expectedAccount);

    assertThrows(BannedException.class, () -> userService.loadUserByUsername(TEST_PHONE));
  }
}
