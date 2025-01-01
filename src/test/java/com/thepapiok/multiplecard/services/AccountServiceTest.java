package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mongodb.MongoWriteException;
import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Role;
import com.thepapiok.multiplecard.collections.Shop;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.UserDTO;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.ProductRepository;
import com.thepapiok.multiplecard.repositories.ShopRepository;
import com.thepapiok.multiplecard.repositories.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AccountServiceTest {
  private static final String TEST_PHONE = "+4823412341242134";
  private static final String ROLE_ADMIN = "ROLE_ADMIN";
  private static final String ROLE_SHOP = "ROLE_SHOP";
  private static final String TEST_OTHER_PHONE = "+481352312341234423";
  private static final String TEST_ID = "123456789012345678901234";
  private static final ObjectId TEST_OBJECT_ID = new ObjectId(TEST_ID);
  private static final ObjectId TEST_OBJECT_OTHER_ID = new ObjectId("673456789012345678901234");
  private Shop shop;
  private User user;
  private AccountService accountService;
  @Mock private AccountRepository accountRepository;
  @Mock private UserRepository userRepository;
  @Mock private ShopRepository shopRepository;
  @Mock private ProductRepository productRepository;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    accountService =
        new AccountService(accountRepository, userRepository, shopRepository, productRepository);
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
  public void shouldReturnFalseAtChangeActiveWhenAccountNotFound() {
    when(accountRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.empty());

    assertFalse(accountService.changeActive(TEST_ID, true));
  }

  @Test
  public void shouldReturnFalseAtChangeActiveWhenGetException() {
    Account account = new Account();
    account.setId(TEST_OBJECT_ID);
    account.setActive(false);
    Account expectedAccount = new Account();
    expectedAccount.setId(TEST_OBJECT_ID);
    expectedAccount.setActive(true);

    when(accountRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(account));
    when(accountRepository.save(expectedAccount)).thenThrow(MongoWriteException.class);

    assertFalse(accountService.changeActive(TEST_ID, true));
  }

  @Test
  public void shouldReturnTrueAtChangeActiveWhenEverythingOk() {
    Account account = new Account();
    account.setId(TEST_OBJECT_ID);
    account.setActive(false);
    Account expectedAccount = new Account();
    expectedAccount.setId(TEST_OBJECT_ID);
    expectedAccount.setActive(true);

    when(accountRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(account));

    assertTrue(accountService.changeActive(TEST_ID, true));
    verify(accountRepository).save(expectedAccount);
  }

  @Test
  public void shouldReturnFalseAtChangeBannedWhenAccountNotFound() {
    when(accountRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.empty());

    assertFalse(accountService.changeBanned(TEST_ID, true));
  }

  @Test
  public void shouldReturnFalseAtChangeBannedWhenGetException() {
    Account account = new Account();
    account.setId(TEST_OBJECT_ID);
    account.setBanned(false);
    Account expectedAccount = new Account();
    expectedAccount.setId(TEST_OBJECT_ID);
    expectedAccount.setBanned(true);

    when(accountRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(account));
    when(accountRepository.save(expectedAccount)).thenThrow(MongoWriteException.class);

    assertFalse(accountService.changeBanned(TEST_ID, true));
  }

  @Test
  public void shouldReturnTrueAtChangeBannedWhenEverythingOk() {
    Account account = new Account();
    account.setId(TEST_OBJECT_ID);
    account.setBanned(false);
    Account expectedAccount = new Account();
    expectedAccount.setId(TEST_OBJECT_ID);
    expectedAccount.setBanned(true);

    when(accountRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(account));

    assertTrue(accountService.changeBanned(TEST_ID, true));
    verify(accountRepository).save(expectedAccount);
  }

  @Test
  public void shouldReturnAccountAtGetAccountByProductIdWhenEverythingOk() {
    final String testEmail = "testEmail";
    Account account = new Account();
    account.setId(TEST_OBJECT_ID);
    account.setPhone(TEST_PHONE);
    account.setEmail(testEmail);

    when(productRepository.findAccountByProductId(TEST_OBJECT_ID)).thenReturn(account);

    assertEquals(account, accountService.getAccountByProductId(TEST_ID));
  }

  @Test
  public void shouldReturnAccountAtGetAccountByIdWhenEverythingOk() {
    final String testEmail = "testEmail";
    Account account = new Account();
    account.setPhone(TEST_PHONE);
    account.setEmail(testEmail);

    when(accountRepository.findAccountById(TEST_OBJECT_ID)).thenReturn(account);

    assertEquals(account, accountService.getAccountById(TEST_ID));
  }
}
