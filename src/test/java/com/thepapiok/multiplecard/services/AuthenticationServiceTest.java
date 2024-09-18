package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mongodb.MongoExecutionTimeoutException;
import com.mongodb.MongoWriteException;
import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.RegisterDTO;
import com.thepapiok.multiplecard.misc.AccountConverter;
import com.thepapiok.multiplecard.misc.UserConverter;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import java.util.List;
import java.util.Random;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@Profile("test")
public class AuthenticationServiceTest {
  private static final String TEST_PHONE = "213442123411324";
  private static final String TEST_PASSWORD = "password";
  private static final String TEST_ENCODE_PASSWORD = "encodePassword";
  private AuthenticationService authenticationService;
  private RegisterDTO registerDTO;
  private User expectedUser;
  private User expectedUser2;
  private Account expectedAccount;
  @Mock private UserConverter userConverter;
  @Mock private AccountConverter accountConverter;
  @Mock private AccountRepository accountRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private MongoTransactionManager mongoTransactionManager;
  @Mock private MongoTemplate mongoTemplate;
  @Mock private Random random;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    authenticationService =
        new AuthenticationService(
            accountRepository,
            userConverter,
            accountConverter,
            passwordEncoder,
            mongoTransactionManager,
            mongoTemplate);
    final String testText = "Test";
    registerDTO = new RegisterDTO();
    registerDTO.setFirstName(testText);
    registerDTO.setLastName(testText);
    registerDTO.setStreet(testText);
    registerDTO.setCity(testText);
    registerDTO.setCountry("Polska");
    registerDTO.setPostalCode("+48");
    registerDTO.setPhone("12312312321");
    registerDTO.setPassword("Test123!");
    registerDTO.setHouseNumber("1");
    registerDTO.setEmail("test@test");
    registerDTO.setProvince(testText);
    Address expectedAddress = new Address();
    expectedAddress.setCity(registerDTO.getCity());
    expectedAddress.setStreet(registerDTO.getStreet());
    expectedAddress.setPostalCode(registerDTO.getPostalCode());
    expectedAddress.setHouseNumber(registerDTO.getHouseNumber());
    expectedAddress.setCountry(registerDTO.getCountry());
    expectedAddress.setProvince(registerDTO.getProvince());
    expectedUser = new User();
    expectedUser.setFirstName(registerDTO.getFirstName());
    expectedUser.setLastName(registerDTO.getLastName());
    expectedUser.setAddress(expectedAddress);
    expectedUser2 = new User();
    expectedUser2.setFirstName(registerDTO.getFirstName());
    expectedUser2.setLastName(registerDTO.getLastName());
    expectedUser2.setAddress(expectedAddress);
    expectedUser2.setId(new ObjectId("123456789012345678901234"));
    expectedAccount = new Account();
    expectedAccount.setPassword("dsfbv134fvdb");
    expectedAccount.setPhone(registerDTO.getCallingCode() + registerDTO.getPhone());
    expectedAccount.setEmail(registerDTO.getEmail());
  }

  @Test
  public void shouldSuccessAtCreateUser() {
    when(mongoTemplate.save(expectedUser)).thenReturn(expectedUser2);
    when(userConverter.getEntity(registerDTO)).thenReturn(expectedUser);
    when(accountConverter.getEntity(registerDTO)).thenReturn(expectedAccount);

    assertTrue(authenticationService.createUser(registerDTO));
    verify(mongoTemplate).save(expectedUser);
    verify(mongoTemplate).save(expectedAccount);
  }

  @Test
  public void shouldFailAtCreateUser() {
    when(mongoTemplate.save(expectedUser)).thenReturn(expectedUser2);
    when(userConverter.getEntity(registerDTO)).thenReturn(expectedUser);
    when(accountConverter.getEntity(registerDTO)).thenReturn(expectedAccount);
    when(mongoTemplate.save(expectedAccount)).thenThrow(MongoWriteException.class);

    assertFalse(authenticationService.createUser(registerDTO));
    verify(mongoTemplate).save(expectedUser);
    verify(mongoTemplate).save(expectedAccount);
  }

  @Test
  public void shouldSuccessAtGetPhones() {
    Account account1 = new Account();
    account1.setPhone(TEST_PHONE);
    Account account2 = new Account();
    account2.setPhone("4565434253245462");
    List<Account> expectedAccountList = List.of(account1, account2);
    List<String> expectedPhones = List.of(account1.getPhone(), account2.getPhone());

    when(accountRepository.findAllPhones()).thenReturn(expectedAccountList);

    assertEquals(expectedPhones, authenticationService.getPhones());
  }

  @Test
  public void shouldFailAtGetPhones() {
    when(accountRepository.findAllPhones()).thenThrow(MongoExecutionTimeoutException.class);

    assertEquals(List.of(), authenticationService.getPhones());
  }

  @Test
  public void shouldSuccessAtGetEmails() {
    Account account1 = new Account();
    account1.setEmail("test1@test");
    Account account2 = new Account();
    account2.setEmail("Test2@tests");
    List<Account> expectedAccountList = List.of(account1, account2);
    List<String> expectedEmails = List.of(account1.getEmail(), account2.getEmail());

    when(accountRepository.findAllEmails()).thenReturn(expectedAccountList);

    assertEquals(expectedEmails, authenticationService.getEmails());
  }

  @Test
  public void shouldFailAtGetEmails() {
    when(accountRepository.findAllEmails()).thenThrow(MongoExecutionTimeoutException.class);

    assertEquals(List.of(), authenticationService.getEmails());
  }

  @Test
  public void shouldSuccessAtGetVerificationNumber() {
    authenticationService.setRandom(random);

    when(random.nextInt()).thenReturn(0);

    assertEquals("000 000", authenticationService.getVerificationNumber());
  }

  @Test
  public void shouldSuccessAtChangePassword() {
    Account account = new Account();
    account.setPhone(TEST_PHONE);
    Account expectedAccount = new Account();
    expectedAccount.setPhone(TEST_PHONE);
    expectedAccount.setPassword(TEST_ENCODE_PASSWORD);

    when(accountRepository.findByPhone(TEST_PHONE)).thenReturn(account);
    when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(TEST_ENCODE_PASSWORD);

    assertTrue(authenticationService.changePassword(TEST_PHONE, TEST_PASSWORD));
    verify(accountRepository).save(expectedAccount);
  }

  @Test
  public void shouldFailAtChangePasswordWhenGetException() {
    Account account = new Account();
    account.setPhone(TEST_PHONE);
    Account expectedAccount = new Account();
    expectedAccount.setPhone(TEST_PHONE);
    expectedAccount.setPassword(TEST_ENCODE_PASSWORD);

    when(accountRepository.findByPhone(TEST_PHONE)).thenReturn(account);
    when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(TEST_ENCODE_PASSWORD);
    doThrow(MongoWriteException.class).when(accountRepository).save(expectedAccount);

    assertFalse(authenticationService.changePassword(TEST_PHONE, TEST_PASSWORD));
    verify(accountRepository).save(expectedAccount);
  }

  @Test
  public void shouldSuccessAtGetAccountByPhone() {
    Account account = new Account();

    when(accountRepository.findByPhone(TEST_PHONE)).thenReturn(account);

    assertTrue(authenticationService.getAccountByPhone(TEST_PHONE));
  }

  @Test
  public void shouldFailAtGetAccountByPhone() {
    when(accountRepository.findByPhone(TEST_PHONE)).thenReturn(null);

    assertFalse(authenticationService.getAccountByPhone(TEST_PHONE));
  }

  @Test
  public void shouldSuccessAtCheckPassword() {
    Account account = new Account();
    account.setPhone(TEST_PHONE);
    account.setPassword(TEST_ENCODE_PASSWORD);

    when(accountRepository.findPasswordByPhone(TEST_PHONE)).thenReturn(account);
    when(passwordEncoder.matches(TEST_PASSWORD, TEST_ENCODE_PASSWORD)).thenReturn(true);

    assertTrue(authenticationService.checkPassword(TEST_PASSWORD, TEST_PHONE));
  }

  @Test
  public void shouldFailAtCheckPassword() {
    Account account = new Account();
    account.setPhone(TEST_PHONE);
    account.setPassword(TEST_ENCODE_PASSWORD);

    when(accountRepository.findPasswordByPhone(TEST_PHONE)).thenReturn(account);
    when(passwordEncoder.matches(TEST_PASSWORD, TEST_ENCODE_PASSWORD)).thenReturn(false);

    assertFalse(authenticationService.checkPassword(TEST_PASSWORD, TEST_PHONE));
  }
}
