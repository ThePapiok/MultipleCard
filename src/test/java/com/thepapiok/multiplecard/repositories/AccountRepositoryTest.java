package com.thepapiok.multiplecard.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Role;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

@DataMongoTest
@ActiveProfiles("test")
public class AccountRepositoryTest {

  private static final String TEST_ID = "1232rfvbb";
  private static final String TEST_PASSWORD = "123sdfadvvdb";
  private static final String TEST_OTHER_ID = "vadv1223dvbv";
  private static final String TEST_OTHER_PASSWORD = "sadfb34545dfvb";

  @Autowired private AccountRepository accountRepository;
  @MockBean private RestTemplate restTemplate;

  @Test
  public void shouldSuccessFindAllPhones() {
    final String phone1 = "+21413241234";
    final String phone2 = "+76546545343";
    final String email = "mail";
    Account account1 = new Account();
    account1.setPhone(phone1);
    account1.setEmail(email);
    account1.setId(TEST_ID);
    account1.setPassword(TEST_PASSWORD);
    account1.setActive(true);
    account1.setRole(Role.ROLE_USER);
    Account account2 = new Account();
    account2.setPhone(phone2);
    account2.setId(TEST_OTHER_ID);
    account2.setEmail(email);
    account2.setPassword(TEST_OTHER_PASSWORD);
    account2.setActive(true);
    account2.setRole(Role.ROLE_USER);
    Account expectedAccount1 = new Account();
    expectedAccount1.setPhone(phone1);
    Account expectedAccount2 = new Account();
    expectedAccount2.setPhone(phone2);
    List<Account> expectedAccounts = List.of(expectedAccount1, expectedAccount2);
    accountRepository.save(account1);
    accountRepository.save(account2);

    assertEquals(expectedAccounts, accountRepository.findAllPhones());
  }

  @Test
  public void shouldSuccessFindAllEmails() {
    final String email1 = "test1@test";
    final String email2 = "Test2@tset";
    Account account1 = new Account();
    account1.setPhone("+1231231");
    account1.setEmail(email1);
    account1.setId(TEST_ID);
    account1.setPassword(TEST_PASSWORD);
    account1.setActive(true);
    account1.setRole(Role.ROLE_USER);
    Account account2 = new Account();
    account2.setPhone("+234324345");
    account2.setId(TEST_OTHER_ID);
    account2.setEmail(email2);
    account2.setPassword(TEST_OTHER_PASSWORD);
    account2.setActive(true);
    account2.setRole(Role.ROLE_USER);
    Account expectedAccount1 = new Account();
    expectedAccount1.setEmail(email1);
    Account expectedAccount2 = new Account();
    expectedAccount2.setEmail(email2);
    List<Account> expectedAccounts = List.of(expectedAccount1, expectedAccount2);
    accountRepository.save(account1);
    accountRepository.save(account2);

    assertEquals(expectedAccounts, accountRepository.findAllEmails());
  }
}
