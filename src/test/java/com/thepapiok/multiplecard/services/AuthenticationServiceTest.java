package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mongodb.MongoExecutionTimeoutException;
import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.RegisterDTO;
import com.thepapiok.multiplecard.misc.AccountConverter;
import com.thepapiok.multiplecard.misc.UserConverter;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.UserRepository;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Profile;

@Profile("test")
public class AuthenticationServiceTest {

  private AuthenticationService authenticationService;
  @Mock private UserConverter userConverter;
  @Mock private UserRepository userRepository;
  @Mock private AccountConverter accountConverter;
  @Mock private AccountRepository accountRepository;
  @Mock private Random random;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    authenticationService =
        new AuthenticationService(
            accountRepository, userRepository, userConverter, accountConverter);
  }

  @Test
  public void shouldSuccessCreateUser() {
    RegisterDTO registerDTO = new RegisterDTO();
    registerDTO.setFirstName("Test");
    registerDTO.setLastName("Test");
    registerDTO.setStreet("Test");
    registerDTO.setCity("Test");
    registerDTO.setCountry("Polska");
    registerDTO.setPostalCode("+48");
    registerDTO.setPhone("12312312321");
    registerDTO.setPassword("Test123!");
    registerDTO.setHouseNumber("1");
    registerDTO.setEmail("test@test");
    registerDTO.setProvince("Test");
    Address expectedAddress = new Address();
    expectedAddress.setCity(registerDTO.getCity());
    expectedAddress.setStreet(registerDTO.getStreet());
    expectedAddress.setPostalCode(registerDTO.getPostalCode());
    expectedAddress.setHouseNumber(registerDTO.getHouseNumber());
    expectedAddress.setCountry(registerDTO.getCountry());
    expectedAddress.setProvince(registerDTO.getProvince());
    User expectedUser = new User();
    expectedUser.setFirstName(registerDTO.getFirstName());
    expectedUser.setLastName(registerDTO.getLastName());
    expectedUser.setAddress(expectedAddress);
    User expectedUser2 = new User();
    expectedUser2.setFirstName(registerDTO.getFirstName());
    expectedUser2.setLastName(registerDTO.getLastName());
    expectedUser2.setAddress(expectedAddress);
    expectedUser2.setId("123dfsv231fsd");
    Account expectedAccount = new Account();
    expectedAccount.setPassword("dsfbv134fvdb");
    expectedAccount.setPhone(registerDTO.getCallingCode() + registerDTO.getPhone());
    expectedAccount.setEmail(registerDTO.getEmail());

    when(userRepository.save(expectedUser)).thenReturn(expectedUser2);
    when(userConverter.getEntity(registerDTO)).thenReturn(expectedUser);
    when(accountConverter.getEntity(registerDTO)).thenReturn(expectedAccount);

    authenticationService.createUser(registerDTO);
    verify(userRepository).save(expectedUser);
    verify(accountRepository).save(expectedAccount);
  }

  @Test
  public void shouldSuccessGetPhones() {
    Account account1 = new Account();
    account1.setPhone("213442123411324");
    Account account2 = new Account();
    account2.setPhone("4565434253245462");
    List<Account> expectedAccountList = List.of(account1, account2);
    List<String> expectedPhones = List.of(account1.getPhone(), account2.getPhone());

    when(accountRepository.findAllPhones()).thenReturn(expectedAccountList);

    assertEquals(expectedPhones, authenticationService.getPhones());
  }

  @Test
  public void shouldFailGetPhones() {
    when(accountRepository.findAllPhones()).thenThrow(MongoExecutionTimeoutException.class);

    assertNull(authenticationService.getPhones());
  }

  @Test
  public void shouldSuccessGetEmails() {
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
  public void shouldFailGetEmails() {
    when(accountRepository.findAllEmails()).thenThrow(MongoExecutionTimeoutException.class);

    assertNull(authenticationService.getEmails());
  }

  @Test
  public void shouldSuccessGetVerificationNumber() {
    authenticationService.setRandom(random);

    when(random.nextInt()).thenReturn(0);

    assertEquals("000 000", authenticationService.getVerificationNumber());
  }
}
