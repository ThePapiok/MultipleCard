package com.thepapiok.multiplecard.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Role;
import com.thepapiok.multiplecard.configs.DbConfig;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

@DataMongoTest
@ActiveProfiles("test")
@Import(DbConfig.class)
public class AccountRepositoryTest {
  private static final String TEST_PHONE1 = "+21413241234";
  private static final String TEST_PHONE2 = "+76546545343";
  private static final String TEST_EMAIL1 = "email1";
  private static final String TEST_EMAIL2 = "email2";
  private static final String TEST_PASSWORD1 = "Zasdq1!2dss";
  private static final ObjectId TEST_ID = new ObjectId("123456789012345678901234");
  private Account account1;
  private Account account2;
  @Autowired private AccountRepository accountRepository;
  @Autowired private MongoTemplate mongoTemplate;
  @MockBean private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() {
    account1 = new Account();
    account1.setPhone(TEST_PHONE1);
    account1.setEmail(TEST_EMAIL1);
    account1.setId(TEST_ID);
    account1.setPassword(TEST_PASSWORD1);
    account1.setActive(true);
    account1.setRole(Role.ROLE_USER);
    account2 = new Account();
    account2.setPhone(TEST_PHONE2);
    account2.setEmail(TEST_EMAIL2);
    account2.setId(new ObjectId("123456789012345678901235"));
    account2.setPassword("asdaZ12!asd");
    account2.setActive(true);
    account2.setRole(Role.ROLE_USER);
    mongoTemplate.save(account1);
    mongoTemplate.save(account2);
  }

  @AfterEach
  public void cleanUp() {
    accountRepository.deleteAll();
  }

  @Test
  public void shouldSuccessFindAllPhones() {
    Account expectedAccount1 = new Account();
    expectedAccount1.setPhone(TEST_PHONE1);
    Account expectedAccount2 = new Account();
    expectedAccount2.setPhone(TEST_PHONE2);
    List<Account> expectedAccounts = List.of(expectedAccount1, expectedAccount2);

    assertEquals(expectedAccounts, accountRepository.findAllPhones());
  }

  @Test
  public void shouldSuccessFindAllEmails() {
    Account expectedAccount1 = new Account();
    expectedAccount1.setEmail(TEST_EMAIL1);
    Account expectedAccount2 = new Account();
    expectedAccount2.setEmail(TEST_EMAIL2);
    List<Account> expectedAccounts = List.of(expectedAccount1, expectedAccount2);
    accountRepository.save(account1);
    accountRepository.save(account2);

    assertEquals(expectedAccounts, accountRepository.findAllEmails());
  }

  @Test
  public void shouldSuccessFindIdByPhone() {
    Account expectedAccount = new Account();
    expectedAccount.setId(TEST_ID);

    assertEquals(expectedAccount, accountRepository.findIdByPhone(TEST_PHONE1));
  }

  @Test
  public void shouldSuccessFindPasswordByPhone() {
    Account expecetedAccount = new Account();
    expecetedAccount.setPassword(TEST_PASSWORD1);

    assertEquals(expecetedAccount, accountRepository.findPasswordByPhone(TEST_PHONE1));
  }
}
