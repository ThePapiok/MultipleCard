package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mongodb.MongoWriteException;
import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Role;
import com.thepapiok.multiplecard.collections.Shop;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.UserDTO;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.CategoryRepository;
import com.thepapiok.multiplecard.repositories.ProductRepository;
import com.thepapiok.multiplecard.repositories.ShopRepository;
import com.thepapiok.multiplecard.repositories.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;

public class AccountServiceTest {
  private static final String TEST_PHONE = "+4823412341242134";
  private static final String TEST_EMAIL = "testEmail";
  private static final String ROLE_ADMIN = "ROLE_ADMIN";
  private static final String ROLE_SHOP = "ROLE_SHOP";
  private static final String TEST_OTHER_PHONE = "+481352312341234423";
  private static final String TEST_ID = "123456789012345678901234";
  private static final String USER_NOT_FOUND_ERROR = "Nie ma takiego użytkownika";
  private static final String USER_ALREADY_HAS_ERROR = "Użytkownik posiada już taka wartość";
  private static final String UNEXPECTED_ERROR = "Nieoczekiwany błąd";
  private static final String USER_NOT_FOUND_PARAM = "error.user.not_found";
  private static final String USER_ALREADY_HAS_PARAM = "adminPanel.error.user_already_has";
  private static final String UNEXPECTED_PARAM = "error.unexpected";
  private static final String OK_SUCCESS = "ok";
  private static final ObjectId TEST_OBJECT_ID = new ObjectId(TEST_ID);
  private static final ObjectId TEST_OBJECT_OTHER_ID = new ObjectId("673456789012345678901234");
  private Shop shop;
  private User user;
  private AccountService accountService;
  @Mock private AccountRepository accountRepository;
  @Mock private UserRepository userRepository;
  @Mock private ShopRepository shopRepository;
  @Mock private ProductRepository productRepository;
  @Mock private CategoryRepository categoryRepository;
  @Mock private MessageSource messageSource;
  @Mock private AdminPanelService adminPanelService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    accountService =
        new AccountService(
            accountRepository,
            userRepository,
            shopRepository,
            productRepository,
            categoryRepository,
            messageSource,
            adminPanelService);
  }

  @Test
  public void shouldReturnEmptyListAtGetUsersWhenNotFoundShop() {
    List<Account> accounts = new ArrayList<>();
    Account account = new Account();
    account.setRole(Role.ROLE_SHOP);
    account.setId(TEST_OBJECT_ID);
    accounts.add(account);

    when(accountRepository.findAll()).thenReturn(accounts);
    when(shopRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.empty());

    assertEquals(List.of(), accountService.getUsers(null, null));
  }

  @Test
  public void shouldReturnEmptyListAtGetUsersWhenNotFoundUser() {
    List<Account> accounts = new ArrayList<>();
    Account account = new Account();
    account.setRole(Role.ROLE_ADMIN);
    account.setId(TEST_OBJECT_ID);
    accounts.add(account);

    when(accountRepository.findAll()).thenReturn(accounts);
    when(userRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.empty());

    assertEquals(List.of(), accountService.getUsers(null, null));
  }

  @Test
  public void shouldReturnEmptyListAtGetUsersWhenGetException() {
    when(accountRepository.findAll()).thenThrow(MongoWriteException.class);

    assertEquals(List.of(), accountService.getUsers(null, null));
  }

  @Test
  public void shouldReturnListOfUserDTOAtGetUsersWhenTypeIsNull() {
    List<Account> accounts = setAccountsAtGetUsers();
    List<UserDTO> userDTOS = new ArrayList<>();
    UserDTO userDTO1 = new UserDTO();
    userDTO1.setId(TEST_OBJECT_ID.toString());
    userDTO1.setPhone(TEST_PHONE);
    userDTO1.setActive(true);
    userDTO1.setBanned(false);
    userDTO1.setRole(ROLE_ADMIN);
    userDTO1.setFirstName(user.getFirstName());
    userDTO1.setLastName(user.getLastName());
    UserDTO userDTO2 = new UserDTO();
    userDTO2.setId(TEST_OBJECT_OTHER_ID.toString());
    userDTO2.setPhone(TEST_OTHER_PHONE);
    userDTO2.setActive(false);
    userDTO2.setBanned(true);
    userDTO2.setRole(ROLE_SHOP);
    userDTO2.setFirstName(shop.getFirstName());
    userDTO2.setLastName(shop.getLastName());
    userDTOS.add(userDTO1);
    userDTOS.add(userDTO2);

    when(userRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(user));
    when(shopRepository.findById(TEST_OBJECT_OTHER_ID)).thenReturn(Optional.of(shop));
    when(accountRepository.findAll()).thenReturn(accounts);

    assertEquals(userDTOS, accountService.getUsers(null, null));
  }

  @Test
  public void shouldReturnListOfUserDTOAtGetUsersWhenTypeIs0() {
    final int testType = 0;
    List<Account> accounts = setAccountsAtGetUsers();
    List<UserDTO> userDTOS = new ArrayList<>();
    UserDTO userDTO1 = new UserDTO();
    userDTO1.setId(TEST_OBJECT_ID.toString());
    userDTO1.setPhone(TEST_PHONE);
    userDTO1.setActive(true);
    userDTO1.setBanned(false);
    userDTO1.setRole(ROLE_ADMIN);
    userDTO1.setFirstName(user.getFirstName());
    userDTO1.setLastName(user.getLastName());
    userDTOS.add(userDTO1);

    when(userRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(user));
    when(shopRepository.findById(TEST_OBJECT_OTHER_ID)).thenReturn(Optional.of(shop));
    when(accountRepository.findAll()).thenReturn(accounts);

    assertEquals(userDTOS, accountService.getUsers(testType, TEST_ID));
  }

  @Test
  public void shouldReturnListOfUserDTOAtGetUsersWhenTypeIs1() {
    final int testType = 1;
    List<Account> accounts = setAccountsAtGetUsers();
    List<UserDTO> userDTOS = new ArrayList<>();
    UserDTO userDTO1 = new UserDTO();
    userDTO1.setId(TEST_OBJECT_OTHER_ID.toString());
    userDTO1.setPhone(TEST_OTHER_PHONE);
    userDTO1.setActive(false);
    userDTO1.setBanned(true);
    userDTO1.setRole(ROLE_SHOP);
    userDTO1.setFirstName(shop.getFirstName());
    userDTO1.setLastName(shop.getLastName());
    userDTOS.add(userDTO1);

    when(userRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(user));
    when(shopRepository.findById(TEST_OBJECT_OTHER_ID)).thenReturn(Optional.of(shop));
    when(accountRepository.findAll()).thenReturn(accounts);

    assertEquals(userDTOS, accountService.getUsers(testType, shop.getFirstName()));
  }

  @Test
  public void shouldReturnListOfUserDTOAtGetUsersWhenTypeIs2() {
    final int testType = 2;
    List<Account> accounts = setAccountsAtGetUsers();
    List<UserDTO> userDTOS = new ArrayList<>();
    UserDTO userDTO1 = new UserDTO();
    userDTO1.setId(TEST_OBJECT_ID.toString());
    userDTO1.setPhone(TEST_PHONE);
    userDTO1.setActive(true);
    userDTO1.setBanned(false);
    userDTO1.setRole(ROLE_ADMIN);
    userDTO1.setFirstName(user.getFirstName());
    userDTO1.setLastName(user.getLastName());
    userDTOS.add(userDTO1);

    when(userRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(user));
    when(shopRepository.findById(TEST_OBJECT_OTHER_ID)).thenReturn(Optional.of(shop));
    when(accountRepository.findAll()).thenReturn(accounts);

    assertEquals(userDTOS, accountService.getUsers(testType, user.getLastName()));
  }

  @Test
  public void shouldReturnListOfUserDTOAtGetUsersWhenTypeIs3() {
    final int testType = 3;
    List<Account> accounts = setAccountsAtGetUsers();
    List<UserDTO> userDTOS = new ArrayList<>();
    UserDTO userDTO1 = new UserDTO();
    userDTO1.setId(TEST_OBJECT_ID.toString());
    userDTO1.setPhone(TEST_PHONE);
    userDTO1.setActive(true);
    userDTO1.setBanned(false);
    userDTO1.setRole(ROLE_ADMIN);
    userDTO1.setFirstName(user.getFirstName());
    userDTO1.setLastName(user.getLastName());
    userDTOS.add(userDTO1);

    when(userRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(user));
    when(shopRepository.findById(TEST_OBJECT_OTHER_ID)).thenReturn(Optional.of(shop));
    when(accountRepository.findAll()).thenReturn(accounts);

    assertEquals(userDTOS, accountService.getUsers(testType, TEST_PHONE));
  }

  @Test
  public void shouldReturnListOfUserDTOAtGetUsersWhenTypeIs4() {
    final int testType = 4;
    List<Account> accounts = setAccountsAtGetUsers();
    List<UserDTO> userDTOS = new ArrayList<>();
    UserDTO userDTO1 = new UserDTO();
    userDTO1.setId(TEST_OBJECT_ID.toString());
    userDTO1.setPhone(TEST_PHONE);
    userDTO1.setActive(true);
    userDTO1.setBanned(false);
    userDTO1.setRole(ROLE_ADMIN);
    userDTO1.setFirstName(user.getFirstName());
    userDTO1.setLastName(user.getLastName());
    userDTOS.add(userDTO1);

    when(userRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(user));
    when(shopRepository.findById(TEST_OBJECT_OTHER_ID)).thenReturn(Optional.of(shop));
    when(accountRepository.findAll()).thenReturn(accounts);

    assertEquals(userDTOS, accountService.getUsers(testType, Role.ROLE_ADMIN.name()));
  }

  @Test
  public void shouldReturnListOfUserDTOAtGetUsersWhenTypeIs5() {
    final int testType = 5;
    List<Account> accounts = setAccountsAtGetUsers();
    List<UserDTO> userDTOS = new ArrayList<>();
    UserDTO userDTO1 = new UserDTO();
    userDTO1.setId(TEST_OBJECT_OTHER_ID.toString());
    userDTO1.setPhone(TEST_OTHER_PHONE);
    userDTO1.setActive(false);
    userDTO1.setBanned(true);
    userDTO1.setRole(ROLE_SHOP);
    userDTO1.setFirstName(shop.getFirstName());
    userDTO1.setLastName(shop.getLastName());
    userDTOS.add(userDTO1);

    when(userRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(user));
    when(shopRepository.findById(TEST_OBJECT_OTHER_ID)).thenReturn(Optional.of(shop));
    when(accountRepository.findAll()).thenReturn(accounts);

    assertEquals(userDTOS, accountService.getUsers(testType, "false"));
  }

  @Test
  public void shouldReturnListOfUserDTOAtGetUsersWhenTypeIs6() {
    final int testType = 6;
    List<Account> accounts = setAccountsAtGetUsers();
    List<UserDTO> userDTOS = new ArrayList<>();
    UserDTO userDTO1 = new UserDTO();
    userDTO1.setId(TEST_OBJECT_ID.toString());
    userDTO1.setPhone(TEST_PHONE);
    userDTO1.setActive(true);
    userDTO1.setBanned(false);
    userDTO1.setRole(ROLE_ADMIN);
    userDTO1.setFirstName(user.getFirstName());
    userDTO1.setLastName(user.getLastName());
    userDTOS.add(userDTO1);

    when(userRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(user));
    when(shopRepository.findById(TEST_OBJECT_OTHER_ID)).thenReturn(Optional.of(shop));
    when(accountRepository.findAll()).thenReturn(accounts);

    assertEquals(userDTOS, accountService.getUsers(testType, "false"));
  }

  @Test
  public void shouldReturnListOfUserDTOAtGetUsersWhenTypeIsOther() {
    final int testType = 8;
    List<Account> accounts = setAccountsAtGetUsers();
    List<UserDTO> userDTOS = new ArrayList<>();
    UserDTO userDTO1 = new UserDTO();
    userDTO1.setId(TEST_OBJECT_ID.toString());
    userDTO1.setPhone(TEST_PHONE);
    userDTO1.setActive(true);
    userDTO1.setBanned(false);
    userDTO1.setRole(ROLE_ADMIN);
    userDTO1.setFirstName(user.getFirstName());
    userDTO1.setLastName(user.getLastName());
    UserDTO userDTO2 = new UserDTO();
    userDTO2.setId(TEST_OBJECT_OTHER_ID.toString());
    userDTO2.setPhone(TEST_OTHER_PHONE);
    userDTO2.setActive(false);
    userDTO2.setBanned(true);
    userDTO2.setRole(ROLE_SHOP);
    userDTO2.setFirstName(shop.getFirstName());
    userDTO2.setLastName(shop.getLastName());
    userDTOS.add(userDTO1);
    userDTOS.add(userDTO2);

    when(userRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(user));
    when(shopRepository.findById(TEST_OBJECT_OTHER_ID)).thenReturn(Optional.of(shop));
    when(accountRepository.findAll()).thenReturn(accounts);

    assertEquals(userDTOS, accountService.getUsers(testType, "some"));
  }

  private List<Account> setAccountsAtGetUsers() {
    List<Account> accounts = new ArrayList<>();
    Account account1 = new Account();
    account1.setRole(Role.ROLE_ADMIN);
    account1.setId(TEST_OBJECT_ID);
    account1.setActive(true);
    account1.setBanned(false);
    account1.setPhone(TEST_PHONE);
    Account account2 = new Account();
    account2.setRole(Role.ROLE_SHOP);
    account2.setId(TEST_OBJECT_OTHER_ID);
    account2.setActive(false);
    account2.setBanned(true);
    account2.setPhone(TEST_OTHER_PHONE);
    accounts.add(account1);
    accounts.add(account2);
    user = new User();
    user.setFirstName("firstName");
    user.setLastName("lastName");
    shop = new Shop();
    shop.setFirstName("firstNameShop");
    shop.setLastName("lastNameShop");
    return accounts;
  }

  @Test
  public void shouldReturnUserNotFoundErrorAtChangeActiveWhenAccountNotFound() {
    Locale locale = Locale.getDefault();

    when(accountRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.empty());
    when(messageSource.getMessage(USER_NOT_FOUND_PARAM, null, locale))
        .thenReturn(USER_NOT_FOUND_ERROR);

    assertEquals(USER_NOT_FOUND_ERROR, accountService.changeActive(TEST_ID, true, locale));
  }

  @Test
  public void shouldReturnUserAlreadyHasErrorAtChangeActiveWhenUserAlreadyHasThisValue() {
    Account account = setAccount();
    Locale locale = Locale.getDefault();

    when(messageSource.getMessage(USER_ALREADY_HAS_PARAM, null, locale))
        .thenReturn(USER_ALREADY_HAS_ERROR);
    when(accountRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(account));

    assertEquals(
        USER_ALREADY_HAS_ERROR, accountService.changeActive(TEST_ID, false, Locale.getDefault()));
  }

  @Test
  public void shouldReturnUnexpectedErrorAtChangeActiveWhenGetException() {
    Account account = setAccount();
    Account expectedAccount = new Account();
    expectedAccount.setId(TEST_OBJECT_ID);
    expectedAccount.setActive(true);
    expectedAccount.setBanned(false);
    expectedAccount.setPhone(TEST_PHONE);
    expectedAccount.setEmail(TEST_EMAIL);
    expectedAccount.setRole(Role.ROLE_USER);
    Locale locale = Locale.getDefault();

    when(accountRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(account));
    when(accountRepository.save(expectedAccount)).thenThrow(MongoWriteException.class);
    when(messageSource.getMessage(UNEXPECTED_PARAM, null, locale)).thenReturn(UNEXPECTED_ERROR);

    assertEquals(UNEXPECTED_ERROR, accountService.changeActive(TEST_ID, true, Locale.getDefault()));
  }

  @Test
  public void shouldReturnOkSuccessAtChangeActiveWhenEverythingOkAndValueTrue() {
    Account account = setAccount();
    Account expectedAccount = new Account();
    expectedAccount.setId(TEST_OBJECT_ID);
    expectedAccount.setActive(true);
    expectedAccount.setBanned(false);
    expectedAccount.setPhone(TEST_PHONE);
    expectedAccount.setEmail(TEST_EMAIL);
    expectedAccount.setRole(Role.ROLE_USER);
    Locale locale = Locale.getDefault();

    when(accountRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(account));

    assertEquals(OK_SUCCESS, accountService.changeActive(TEST_ID, true, locale));
    verify(accountRepository).save(expectedAccount);
    verify(adminPanelService).sendInfoAboutActivatedUser(TEST_EMAIL, TEST_PHONE, TEST_ID);
  }

  @Test
  public void shouldReturnOkSuccessAtChangeActiveWhenEverythingOkAndValueFalse() {
    Account account = new Account();
    account.setId(TEST_OBJECT_ID);
    account.setActive(true);
    account.setBanned(false);
    account.setPhone(TEST_PHONE);
    account.setEmail(TEST_EMAIL);
    account.setRole(Role.ROLE_USER);
    Account expectedAccount = new Account();
    expectedAccount.setId(TEST_OBJECT_ID);
    expectedAccount.setActive(false);
    expectedAccount.setBanned(false);
    expectedAccount.setPhone(TEST_PHONE);
    expectedAccount.setEmail(TEST_EMAIL);
    expectedAccount.setRole(Role.ROLE_USER);
    Locale locale = Locale.getDefault();

    when(accountRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(account));

    assertEquals(OK_SUCCESS, accountService.changeActive(TEST_ID, false, locale));
    verify(accountRepository).save(expectedAccount);
    verify(adminPanelService).sendInfoAboutDeactivatedUser(TEST_EMAIL, TEST_PHONE, TEST_ID);
  }

  @Test
  public void shouldReturnUserNotFoundErrorAtChangeBannedWhenAccountNotFound() {
    Locale locale = Locale.getDefault();

    when(accountRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.empty());
    when(messageSource.getMessage(USER_NOT_FOUND_PARAM, null, locale))
        .thenReturn(USER_NOT_FOUND_ERROR);

    assertEquals(USER_NOT_FOUND_ERROR, accountService.changeBanned(TEST_ID, true, locale));
  }

  @Test
  public void shouldReturnUserAlreadyHasErrorAtChangeBannedWhenUserAlreadyHasThisValue() {
    Account account = setAccount();
    Locale locale = Locale.getDefault();

    when(messageSource.getMessage(USER_ALREADY_HAS_PARAM, null, locale))
        .thenReturn(USER_ALREADY_HAS_ERROR);
    when(accountRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(account));

    assertEquals(
        USER_ALREADY_HAS_ERROR, accountService.changeBanned(TEST_ID, false, Locale.getDefault()));
  }

  @Test
  public void shouldReturnUnexpectedErrorAtChangeBannedWhenGetException() {
    Account account = setAccount();
    Account expectedAccount = new Account();
    expectedAccount.setId(TEST_OBJECT_ID);
    expectedAccount.setBanned(true);
    expectedAccount.setActive(false);
    expectedAccount.setPhone(TEST_PHONE);
    expectedAccount.setEmail(TEST_EMAIL);
    expectedAccount.setRole(Role.ROLE_USER);
    Locale locale = Locale.getDefault();

    when(accountRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(account));
    when(accountRepository.save(expectedAccount)).thenThrow(MongoWriteException.class);
    when(messageSource.getMessage(UNEXPECTED_PARAM, null, locale)).thenReturn(UNEXPECTED_ERROR);

    assertEquals(UNEXPECTED_ERROR, accountService.changeBanned(TEST_ID, true, locale));
  }

  @Test
  public void shouldReturnOkSuccessAtChangeBannedWhenEverythingOkAndValueTrue() {
    Account account = setAccount();
    Account expectedAccount = new Account();
    expectedAccount.setId(TEST_OBJECT_ID);
    expectedAccount.setBanned(true);
    expectedAccount.setActive(false);
    expectedAccount.setPhone(TEST_PHONE);
    expectedAccount.setEmail(TEST_EMAIL);
    expectedAccount.setRole(Role.ROLE_USER);
    Locale locale = Locale.getDefault();

    when(accountRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(account));

    assertEquals(OK_SUCCESS, accountService.changeBanned(TEST_ID, true, locale));
    verify(accountRepository).save(expectedAccount);
    verify(adminPanelService).sendInfoAboutBlockedUser(TEST_EMAIL, TEST_PHONE, TEST_ID);
  }

  @Test
  public void shouldReturnOkSuccessAtChangeBannedWhenEverythingOkAndValueFalse() {
    Account account = new Account();
    account.setId(TEST_OBJECT_ID);
    account.setBanned(true);
    account.setActive(false);
    account.setPhone(TEST_PHONE);
    account.setEmail(TEST_EMAIL);
    account.setRole(Role.ROLE_USER);
    Account expectedAccount = new Account();
    expectedAccount.setId(TEST_OBJECT_ID);
    expectedAccount.setBanned(false);
    expectedAccount.setActive(false);
    expectedAccount.setPhone(TEST_PHONE);
    expectedAccount.setEmail(TEST_EMAIL);
    expectedAccount.setRole(Role.ROLE_USER);
    Locale locale = Locale.getDefault();

    when(accountRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(account));

    assertEquals(OK_SUCCESS, accountService.changeBanned(TEST_ID, false, locale));
    verify(accountRepository).save(expectedAccount);
    verify(adminPanelService).sendInfoAboutUnblockedUser(TEST_EMAIL, TEST_PHONE, TEST_ID);
  }

  @Test
  public void shouldReturnAccountAtGetAccountByProductIdWhenEverythingOk() {
    Account account = new Account();
    account.setId(TEST_OBJECT_ID);
    account.setPhone(TEST_PHONE);
    account.setEmail(TEST_EMAIL);

    when(productRepository.findAccountByProductId(TEST_OBJECT_ID)).thenReturn(account);

    assertEquals(account, accountService.getAccountByProductId(TEST_ID));
  }

  @Test
  public void shouldReturnAccountAtGetAccountByIdWhenEverythingOk() {
    Account account = new Account();
    account.setPhone(TEST_PHONE);
    account.setEmail(TEST_EMAIL);

    when(accountRepository.findAccountById(TEST_OBJECT_ID)).thenReturn(account);

    assertEquals(account, accountService.getAccountById(TEST_ID));
  }

  @Test
  public void shouldReturnAccountAtGetAccountByCategoryNameWhenEverythingOk() {
    final String testName = "testName";
    Account account = new Account();
    account.setPhone(TEST_PHONE);
    account.setEmail(TEST_EMAIL);

    when(categoryRepository.findAccountByCategoryName(testName)).thenReturn(account);

    assertEquals(account, accountService.getAccountByCategoryName(testName));
  }

  @Test
  public void shouldReturnUserNotFoundErrorAtChangeRoleWhenAccountNotFound() {
    Locale locale = Locale.getDefault();

    when(accountRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.empty());
    when(messageSource.getMessage(USER_NOT_FOUND_PARAM, null, locale))
        .thenReturn(USER_NOT_FOUND_ERROR);

    assertEquals(USER_NOT_FOUND_ERROR, accountService.changeRole(TEST_ID, ROLE_ADMIN, locale));
  }

  @Test
  public void shouldReturnUserAlreadyHasErrorAtChangeRoleWhenUserAlreadyHasThisValue() {
    Account account = setAccount();
    Locale locale = Locale.getDefault();

    when(messageSource.getMessage(USER_ALREADY_HAS_PARAM, null, locale))
        .thenReturn(USER_ALREADY_HAS_ERROR);
    when(accountRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(account));

    assertEquals(
        USER_ALREADY_HAS_ERROR,
        accountService.changeRole(TEST_ID, "ROLE_USER", Locale.getDefault()));
  }

  @Test
  public void shouldReturnBadRoleErrorAtChangeRoleWhenRoleIsShop() {
    Account badAccount = new Account();
    badAccount.setId(TEST_OBJECT_ID);
    badAccount.setPhone(TEST_PHONE);
    badAccount.setEmail(TEST_EMAIL);
    badAccount.setActive(false);
    badAccount.setBanned(false);
    badAccount.setRole(Role.ROLE_SHOP);
    Locale locale = Locale.getDefault();

    when(messageSource.getMessage("adminPanel.error.bad_role", null, locale))
        .thenReturn("Nie możesz tego zmienić dla takiej roli użytkownika");
    when(accountRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(badAccount));

    assertEquals(
        "Nie możesz tego zmienić dla takiej roli użytkownika",
        accountService.changeRole(TEST_ID, ROLE_ADMIN, Locale.getDefault()));
  }

  @Test
  public void shouldReturnUnexpectedErrorAtChangeRoleWhenGetException() {
    Account account = setAccount();
    Account expectedAccount = new Account();
    expectedAccount.setId(TEST_OBJECT_ID);
    expectedAccount.setBanned(false);
    expectedAccount.setActive(false);
    expectedAccount.setPhone(TEST_PHONE);
    expectedAccount.setEmail(TEST_EMAIL);
    expectedAccount.setRole(Role.ROLE_ADMIN);
    Locale locale = Locale.getDefault();

    when(accountRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(account));
    when(accountRepository.save(expectedAccount)).thenThrow(MongoWriteException.class);
    when(messageSource.getMessage(UNEXPECTED_PARAM, null, locale)).thenReturn(UNEXPECTED_ERROR);

    assertEquals(UNEXPECTED_ERROR, accountService.changeRole(TEST_ID, ROLE_ADMIN, locale));
  }

  @Test
  public void shouldReturnOkSuccessAtChangeRoleWhenEverythingOkAndValueRoleAdmin() {
    Account account = setAccount();
    Account expectedAccount = new Account();
    expectedAccount.setId(TEST_OBJECT_ID);
    expectedAccount.setBanned(false);
    expectedAccount.setActive(false);
    expectedAccount.setPhone(TEST_PHONE);
    expectedAccount.setEmail(TEST_EMAIL);
    expectedAccount.setRole(Role.ROLE_ADMIN);
    Locale locale = Locale.getDefault();

    when(accountRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(account));

    assertEquals(OK_SUCCESS, accountService.changeRole(TEST_ID, ROLE_ADMIN, locale));
    verify(accountRepository).save(expectedAccount);
    verify(adminPanelService).sendInfoAboutChangeUserToAdmin(TEST_EMAIL, TEST_PHONE, TEST_ID);
  }

  @Test
  public void shouldReturnOkSuccessAtChangeRoleWhenEverythingOkAndValueRoleUser() {
    Account account = new Account();
    account.setId(TEST_OBJECT_ID);
    account.setBanned(false);
    account.setActive(false);
    account.setPhone(TEST_PHONE);
    account.setEmail(TEST_EMAIL);
    account.setRole(Role.ROLE_ADMIN);
    Account expectedAccount = new Account();
    expectedAccount.setId(TEST_OBJECT_ID);
    expectedAccount.setBanned(false);
    expectedAccount.setActive(false);
    expectedAccount.setPhone(TEST_PHONE);
    expectedAccount.setEmail(TEST_EMAIL);
    expectedAccount.setRole(Role.ROLE_USER);
    Locale locale = Locale.getDefault();

    when(accountRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(account));

    assertEquals(OK_SUCCESS, accountService.changeRole(TEST_ID, "ROLE_USER", locale));
    verify(accountRepository).save(expectedAccount);
    verify(adminPanelService).sendInfoAboutChangeAdminToUser(TEST_EMAIL, TEST_PHONE, TEST_ID);
  }

  private Account setAccount() {
    Account account = new Account();
    account.setId(TEST_OBJECT_ID);
    account.setBanned(false);
    account.setActive(false);
    account.setPhone(TEST_PHONE);
    account.setEmail(TEST_EMAIL);
    account.setRole(Role.ROLE_USER);
    return account;
  }
}
