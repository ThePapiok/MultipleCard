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
  @Autowired private AccountRepository accountRepository;
  @MockBean private RestTemplate restTemplate;

  @Test
  public void shouldSuccessFindAllPhones() {
    final String phone1 = "+21413241234";
    final String phone2 = "+76546545343";
    Account account1 = new Account();
    account1.setPhone(phone1);
    account1.setEmail("mail");
    account1.setId("1232rfvbb");
    account1.setPassword("123sdfadvvdb");
    account1.setActive(true);
    account1.setRole(Role.ROLE_USER);
    Account account2 = new Account();
    account2.setPhone(phone2);
    account2.setId("vadv1223dvbv");
    account2.setEmail("mail");
    account2.setPassword("sadfb34545dfvb");
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
}
