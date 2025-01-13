package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.mongodb.MongoWriteException;
import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Order;
import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.collections.Role;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.PageUserDTO;
import com.thepapiok.multiplecard.dto.UserDTO;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.AggregationRepository;
import com.thepapiok.multiplecard.repositories.CategoryRepository;
import com.thepapiok.multiplecard.repositories.OrderRepository;
import com.thepapiok.multiplecard.repositories.ProductRepository;
import com.thepapiok.multiplecard.repositories.ReportRepository;
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
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;

public class AccountServiceTest {
  private static final String TEST_PHONE = "+4823412341242134";
  private static final String TEST_EMAIL = "testEmail";
  private static final String ROLE_ADMIN = "ROLE_ADMIN";
  private static final String ROLE_USER = "ROLE_USER";
  private static final String TEST_ID = "123456789012345678901234";
  private static final String USER_NOT_FOUND_ERROR = "Nie ma takiego użytkownika";
  private static final String USER_ALREADY_HAS_ERROR = "Użytkownik posiada już taka wartość";
  private static final String UNEXPECTED_ERROR = "Nieoczekiwany błąd";
  private static final String USER_NOT_FOUND_PARAM = "error.user_not_found";
  private static final String USER_ALREADY_HAS_PARAM = "error.user_already_has";
  private static final String UNEXPECTED_PARAM = "error.unexpected";
  private static final String OK_SUCCESS = "ok";
  private static final ObjectId TEST_OBJECT_ID = new ObjectId(TEST_ID);
  private AccountService accountService;
  @Mock private AccountRepository accountRepository;
  @Mock private ProductRepository productRepository;
  @Mock private CategoryRepository categoryRepository;
  @Mock private MessageSource messageSource;
  @Mock private AdminPanelService adminPanelService;
  @Mock private AggregationRepository aggregationRepository;
  @Mock private MongoTransactionManager mongoTransactionManager;
  @Mock private MongoTemplate mongoTemplate;
  @Mock private OrderRepository orderRepository;
  @Mock private ReportRepository reportRepository;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    accountService =
        new AccountService(
            accountRepository,
            productRepository,
            categoryRepository,
            messageSource,
            adminPanelService,
            aggregationRepository,
            mongoTransactionManager,
            mongoTemplate,
            orderRepository,
            reportRepository);
  }

  @Test
  public void shouldReturnPageUserDTOAtGetCurrentPageWhenEverythingOk() {
    List<UserDTO> userDTOS = new ArrayList<>();
    UserDTO userDTO1 = new UserDTO();
    userDTO1.setFirstName("testFirstName1");
    userDTO1.setLastName("testLastName1");
    userDTO1.setPhone("testPhone1");
    userDTO1.setEmail("testEmail1");
    userDTO1.setBanned(false);
    userDTO1.setBanned(true);
    userDTO1.setRole(ROLE_ADMIN);
    userDTO1.setId(new ObjectId().toString());
    UserDTO userDTO2 = new UserDTO();
    userDTO2.setFirstName("testFirstName2");
    userDTO2.setLastName("testLastName2");
    userDTO2.setPhone("testPhone2");
    userDTO2.setEmail("testEmail2");
    userDTO2.setBanned(false);
    userDTO2.setBanned(false);
    userDTO2.setRole(ROLE_USER);
    userDTO2.setId(new ObjectId().toString());
    userDTOS.add(userDTO1);
    userDTOS.add(userDTO2);
    PageUserDTO pageUserDTO = new PageUserDTO();
    pageUserDTO.setMaxPage(1);
    pageUserDTO.setUsers(userDTOS);

    when(aggregationRepository.getUsers("", "", 0)).thenReturn(pageUserDTO);

    assertEquals(pageUserDTO, accountService.getCurrentPage("", "", 0));
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
  public void shouldReturnOkSuccessAtChangeBannedWhenEverythingOkAndValueTrueAndIsShop() {
    final float centsPerZloty = 100;
    final ObjectId testProductId1 = new ObjectId("123456789012345678906666");
    final ObjectId testProductId2 = new ObjectId("123457789012345678906667");
    final ObjectId testCardId1 = new ObjectId("523457789012345678906667");
    final ObjectId testCardId2 = new ObjectId("423457789012345678906667");
    final ObjectId testCardId3 = new ObjectId("323457789012345678906667");
    final int testPrice1 = 234;
    final int testPrice2 = 543;
    final int testPrice3 = 111;
    final String cardIdKey = "cardId";
    final String pointsKey = "points";
    Account account = new Account();
    account.setId(TEST_OBJECT_ID);
    account.setBanned(false);
    account.setActive(false);
    account.setPhone(TEST_PHONE);
    account.setEmail(TEST_EMAIL);
    account.setRole(Role.ROLE_SHOP);
    Account expectedAccount = new Account();
    expectedAccount.setId(TEST_OBJECT_ID);
    expectedAccount.setBanned(true);
    expectedAccount.setActive(false);
    expectedAccount.setPhone(TEST_PHONE);
    expectedAccount.setEmail(TEST_EMAIL);
    expectedAccount.setRole(Role.ROLE_SHOP);
    Locale locale = Locale.getDefault();
    Product product1 = new Product();
    product1.setId(testProductId1);
    Product product2 = new Product();
    product2.setId(testProductId2);
    Order order1 = new Order();
    order1.setCardId(testCardId1);
    order1.setPrice(testPrice1);
    order1.setUsed(false);
    order1.setProductId(testProductId1);
    Order order2 = new Order();
    order2.setCardId(testCardId2);
    order2.setUsed(false);
    order2.setProductId(testProductId1);
    order2.setPrice(testPrice2);
    Order order3 = new Order();
    order3.setCardId(testCardId3);
    order3.setUsed(false);
    order3.setProductId(testProductId2);
    order3.setPrice(testPrice3);

    when(accountRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(account));
    when(productRepository.getProductsIdByShopId(TEST_OBJECT_ID))
        .thenReturn(List.of(product1, product2));
    when(orderRepository.findAllByProductIdAndIsUsed(testProductId1, false))
        .thenReturn(List.of(order1, order2));
    when(orderRepository.findAllByProductIdAndIsUsed(testProductId2, false))
        .thenReturn(List.of(order3));

    assertEquals(OK_SUCCESS, accountService.changeBanned(TEST_ID, true, locale));
    verify(accountRepository).save(expectedAccount);
    verify(adminPanelService).sendInfoAboutBlockedUser(TEST_EMAIL, TEST_PHONE, TEST_ID);
    verify(reportRepository).deleteAllByReportedId(testProductId1);
    verify(reportRepository).deleteAllByReportedId(testProductId2);
    verify(mongoTemplate).remove(order1);
    verify(mongoTemplate).remove(order2);
    verify(mongoTemplate).remove(order3);
    verify(mongoTemplate)
        .updateFirst(
            query(where(cardIdKey).is(testCardId1)),
            new Update().inc(pointsKey, Math.round(testPrice1 / centsPerZloty)),
            User.class);
    verify(mongoTemplate)
        .updateFirst(
            query(where(cardIdKey).is(testCardId2)),
            new Update().inc(pointsKey, Math.round(testPrice2 / centsPerZloty)),
            User.class);
    verify(mongoTemplate)
        .updateFirst(
            query(where(cardIdKey).is(testCardId3)),
            new Update().inc(pointsKey, Math.round(testPrice3 / centsPerZloty)),
            User.class);
  }

  @Test
  public void shouldReturnOkSuccessAtChangeBannedWhenEverythingOkAndValueTrueAndIsUser() {
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
    verify(reportRepository).deleteAllByReportedId(TEST_OBJECT_ID);
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
        USER_ALREADY_HAS_ERROR, accountService.changeRole(TEST_ID, ROLE_USER, Locale.getDefault()));
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

    when(messageSource.getMessage("error.bad_role", null, locale))
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

    assertEquals(OK_SUCCESS, accountService.changeRole(TEST_ID, ROLE_USER, locale));
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

  @Test
  public void shouldReturnTrueAtCheckUserIsBannedWhenAccountNotFound() {
    when(accountRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.empty());

    assertTrue(accountService.checkUserIsBanned(TEST_OBJECT_ID));
  }

  @Test
  public void shouldReturnTrueAtCheckUserIsBannedWhenAccountIsBanned() {
    Account account = new Account();
    account.setBanned(true);
    account.setId(TEST_OBJECT_ID);

    when(accountRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(account));

    assertTrue(accountService.checkUserIsBanned(TEST_OBJECT_ID));
  }

  @Test
  public void shouldReturnFalseAtCheckUserIsBannedWhenAccountIsNotBanned() {
    Account account = new Account();
    account.setBanned(false);
    account.setId(TEST_OBJECT_ID);

    when(accountRepository.findById(TEST_OBJECT_ID)).thenReturn(Optional.of(account));

    assertFalse(accountService.checkUserIsBanned(TEST_OBJECT_ID));
  }
}
