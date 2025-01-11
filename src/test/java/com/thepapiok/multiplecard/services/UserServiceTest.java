package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mongodb.MongoWriteException;
import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Role;
import com.thepapiok.multiplecard.exceptions.BannedException;
import com.thepapiok.multiplecard.exceptions.NotActiveException;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.UserRepository;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserServiceTest {

  private static final String TEST_PHONE = "+4823423411423";
  private static final String TEST_EMAIL = "email";
  private static final String TEST_PASSWORD = "123wefasdfasd123bsedf";
  private static final String TEST_ID = "123456789012345678901234";
  private static final String USER_NOT_FOUND_ERROR = "Nie ma takiego użytkownika";
  private static final ObjectId TEST_OBJECT_ID = new ObjectId(TEST_ID);
  @Mock private AccountRepository accountRepository;
  @Mock private UserRepository userRepository;
  @Mock private MessageSource messageSource;
  @Mock private AdminPanelService adminPanelService;
  private UserService userService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    userService =
        new UserService(accountRepository, userRepository, messageSource, adminPanelService);
  }

  @Test
  public void shouldSuccessAtLoadUserByUsernameWhenEverythingOk() {
    Account expectedAccount = new Account();
    expectedAccount.setPhone(TEST_PHONE);
    expectedAccount.setId(TEST_OBJECT_ID);
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
    expectedAccount.setId(TEST_OBJECT_ID);
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
    expectedAccount.setId(TEST_OBJECT_ID);
    expectedAccount.setEmail(TEST_EMAIL);
    expectedAccount.setActive(true);
    expectedAccount.setBanned(true);
    expectedAccount.setRole(Role.ROLE_USER);
    expectedAccount.setPassword(TEST_PASSWORD);

    when(accountRepository.findByPhone(TEST_PHONE)).thenReturn(expectedAccount);

    assertThrows(BannedException.class, () -> userService.loadUserByUsername(TEST_PHONE));
  }

  @Test
  public void shouldReturnTrueAtCheckIsRestrictedWhenUserNotFound() {
    Account account = new Account();
    account.setId(TEST_OBJECT_ID);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.empty());

    assertTrue(userService.checkIsRestricted(TEST_PHONE));
  }

  @Test
  public void shouldReturnTrueAtCheckIsRestrictedWhenUserIsRestricted() {
    Account account = new Account();
    account.setId(TEST_OBJECT_ID);
    com.thepapiok.multiplecard.collections.User user =
        new com.thepapiok.multiplecard.collections.User();
    user.setRestricted(true);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(user));

    assertTrue(userService.checkIsRestricted(TEST_PHONE));
  }

  @Test
  public void shouldReturnFalseAtCheckIsRestrictedWhenUserIsNotRestricted() {
    Account account = new Account();
    account.setId(TEST_OBJECT_ID);
    com.thepapiok.multiplecard.collections.User user =
        new com.thepapiok.multiplecard.collections.User();
    user.setRestricted(false);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(user));

    assertFalse(userService.checkIsRestricted(TEST_PHONE));
  }

  @Test
  public void shouldReturnUserNotFoundErrorAtChangeRestrictedWhenUserIsNotFound() {
    Locale locale = Locale.getDefault();

    when(userRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.empty());
    when(messageSource.getMessage("error.user_not_found", null, locale))
        .thenReturn(USER_NOT_FOUND_ERROR);

    assertEquals(USER_NOT_FOUND_ERROR, userService.changeRestricted(TEST_ID, true, locale));
  }

  @Test
  public void shouldReturnUserNotFoundErrorAtChangeRestrictedWhenAccountIsNotFound() {
    com.thepapiok.multiplecard.collections.User user =
        new com.thepapiok.multiplecard.collections.User();
    user.setRestricted(false);
    user.setId(TEST_OBJECT_ID);
    Locale locale = Locale.getDefault();

    when(userRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(user));
    when(accountRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.empty());
    when(messageSource.getMessage("error.user_not_found", null, locale))
        .thenReturn(USER_NOT_FOUND_ERROR);

    assertEquals(USER_NOT_FOUND_ERROR, userService.changeRestricted(TEST_ID, true, locale));
  }

  @Test
  public void shouldReturnUserAlreadyHasErrorErrorAtChangeRestrictedWhenUserAlreadyHasThisValue() {
    com.thepapiok.multiplecard.collections.User user =
        new com.thepapiok.multiplecard.collections.User();
    user.setRestricted(false);
    user.setId(TEST_OBJECT_ID);
    Account account = new Account();
    account.setId(TEST_OBJECT_ID);
    account.setRole(Role.ROLE_USER);
    account.setPhone(TEST_PHONE);
    account.setEmail(TEST_EMAIL);
    Locale locale = Locale.getDefault();

    when(userRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(user));
    when(accountRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(account));
    when(messageSource.getMessage("error.user_already_has", null, locale))
        .thenReturn("Użytkownik posiada już taka wartość");

    assertEquals(
        "Użytkownik posiada już taka wartość",
        userService.changeRestricted(TEST_ID, false, locale));
  }

  @Test
  public void shouldReturnBadRoleErrorErrorAtChangeRestrictedWhenUserHasBadRole() {
    com.thepapiok.multiplecard.collections.User user =
        new com.thepapiok.multiplecard.collections.User();
    user.setRestricted(false);
    user.setId(TEST_OBJECT_ID);
    Account account = new Account();
    account.setId(TEST_OBJECT_ID);
    account.setRole(Role.ROLE_ADMIN);
    account.setPhone(TEST_PHONE);
    account.setEmail(TEST_EMAIL);
    Locale locale = Locale.getDefault();

    when(userRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(user));
    when(accountRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(account));
    when(messageSource.getMessage("error.bad_role", null, locale))
        .thenReturn("Nie możesz tego zmienić dla takiej roli użytkownika");

    assertEquals(
        "Nie możesz tego zmienić dla takiej roli użytkownika",
        userService.changeRestricted(TEST_ID, true, locale));
  }

  @Test
  public void shouldReturnUnexpectedErrorAtChangeRestrictedWhenGetException() {
    com.thepapiok.multiplecard.collections.User user =
        new com.thepapiok.multiplecard.collections.User();
    user.setRestricted(false);
    user.setId(TEST_OBJECT_ID);
    com.thepapiok.multiplecard.collections.User excpectedUser =
        new com.thepapiok.multiplecard.collections.User();
    excpectedUser.setRestricted(true);
    excpectedUser.setId(TEST_OBJECT_ID);
    Account account = new Account();
    account.setId(TEST_OBJECT_ID);
    account.setRole(Role.ROLE_USER);
    account.setPhone(TEST_PHONE);
    account.setEmail(TEST_EMAIL);
    Locale locale = Locale.getDefault();

    when(userRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(user));
    when(accountRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(account));
    when(userRepository.save(excpectedUser)).thenThrow(MongoWriteException.class);
    when(messageSource.getMessage("error.unexpected", null, locale))
        .thenReturn("Nieoczekiwany błąd");

    assertEquals("Nieoczekiwany błąd", userService.changeRestricted(TEST_ID, true, locale));
    verify(userRepository).save(excpectedUser);
  }

  @Test
  public void shouldReturnOkSuccessAtChangeRestrictedWhenEverythingOkAndValueTrue() {
    com.thepapiok.multiplecard.collections.User user =
        new com.thepapiok.multiplecard.collections.User();
    user.setRestricted(false);
    user.setId(TEST_OBJECT_ID);
    com.thepapiok.multiplecard.collections.User excpectedUser =
        new com.thepapiok.multiplecard.collections.User();
    excpectedUser.setRestricted(true);
    excpectedUser.setId(TEST_OBJECT_ID);
    Account account = new Account();
    account.setId(TEST_OBJECT_ID);
    account.setRole(Role.ROLE_USER);
    account.setPhone(TEST_PHONE);
    account.setEmail(TEST_EMAIL);
    Locale locale = Locale.getDefault();

    when(userRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(user));
    when(accountRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(account));

    assertEquals("ok", userService.changeRestricted(TEST_ID, true, locale));
    verify(userRepository).save(excpectedUser);
    verify(adminPanelService).sendInfoAboutMutedUser(TEST_EMAIL, TEST_PHONE, TEST_ID);
  }

  @Test
  public void shouldReturnOksSuccessAtChangeRestrictedWhenEverythingOkAndValueTrue() {
    com.thepapiok.multiplecard.collections.User user =
        new com.thepapiok.multiplecard.collections.User();
    user.setRestricted(true);
    user.setId(TEST_OBJECT_ID);
    com.thepapiok.multiplecard.collections.User excpectedUser =
        new com.thepapiok.multiplecard.collections.User();
    excpectedUser.setRestricted(false);
    excpectedUser.setId(TEST_OBJECT_ID);
    Account account = new Account();
    account.setId(TEST_OBJECT_ID);
    account.setRole(Role.ROLE_USER);
    account.setPhone(TEST_PHONE);
    account.setEmail(TEST_EMAIL);
    Locale locale = Locale.getDefault();

    when(userRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(user));
    when(accountRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(account));

    assertEquals("ok", userService.changeRestricted(TEST_ID, false, locale));
    verify(userRepository).save(excpectedUser);
    verify(adminPanelService).sendInfoAboutUnmutedUser(TEST_EMAIL, TEST_PHONE, TEST_ID);
  }
}
