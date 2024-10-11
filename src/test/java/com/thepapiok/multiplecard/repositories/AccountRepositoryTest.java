package com.thepapiok.multiplecard.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.Role;
import com.thepapiok.multiplecard.collections.Shop;
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
  private static final String TEST_CITY = "city1";
  private static final String TEST_STREET = "street1";
  private static final String TEST_COUNTRY = "country1";
  private static final String TEST_PROVINCE = "province1";
  private static final String TEST_POSTAL_CODE = "postalCode1";
  private static final String TEST_HOUSE_NUMBER = "houseNumber1";
  private static final String TEST_SHOP_NAME1 = "shop1";
  private static final String TEST_SHOP_NAME2 = "shop2";
  private static final String TEST_ACCOUNT_NUMBER1 = "132345346457568657342343464575685667";
  private static final String TEST_ACCOUNT_NUMBER2 = "12312312312312312312312312312312312312";
  private static final ObjectId TEST_ID = new ObjectId("123456789012345678901234");
  private Account account1;
  private Account account2;
  private Address address1;
  private Address address2;
  @Autowired private AccountRepository accountRepository;
  @Autowired private ShopRepository shopRepository;
  @Autowired private MongoTemplate mongoTemplate;
  @MockBean private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() {
    final ObjectId otherIdTest = new ObjectId("123456789012345678901235");
    account1 = new Account();
    account1.setPhone(TEST_PHONE1);
    account1.setEmail(TEST_EMAIL1);
    account1.setId(TEST_ID);
    account1.setPassword(TEST_PASSWORD1);
    account1.setActive(true);
    account1.setRole(Role.ROLE_USER);
    address1 = new Address();
    address1.setCity(TEST_CITY);
    address1.setStreet(TEST_STREET);
    address1.setCountry(TEST_COUNTRY);
    address1.setProvince(TEST_PROVINCE);
    address1.setPostalCode(TEST_POSTAL_CODE);
    address1.setHouseNumber(TEST_HOUSE_NUMBER);
    address1.setApartmentNumber(1);
    Shop shop1 = new Shop();
    shop1.setFirstName("firstName1");
    shop1.setLastName("lastName1");
    shop1.setId(TEST_ID);
    shop1.setName(TEST_SHOP_NAME1);
    shop1.setTotalAmount(0L);
    shop1.setImageUrl("sadf12312fsddfbwerewr");
    shop1.setPoints(List.of(address1));
    shop1.setAccountNumber(TEST_ACCOUNT_NUMBER1);
    mongoTemplate.save(shop1);
    address2 = new Address();
    address2.setCity("city2");
    address2.setStreet("street2");
    address2.setCountry("country2");
    address2.setProvince("province2");
    address2.setPostalCode("postalCode2");
    address2.setHouseNumber("houseNumber2");
    address2.setApartmentNumber(1);
    Shop shop2 = new Shop();
    shop2.setId(otherIdTest);
    shop2.setFirstName("firstName2");
    shop2.setLastName("lastName2");
    shop2.setName(TEST_SHOP_NAME2);
    shop2.setTotalAmount(0L);
    shop2.setImageUrl("safddb123vberewrr");
    shop2.setPoints(List.of(address2));
    shop2.setAccountNumber(TEST_ACCOUNT_NUMBER2);
    mongoTemplate.save(shop2);
    account2 = new Account();
    account2.setPhone(TEST_PHONE2);
    account2.setEmail(TEST_EMAIL2);
    account2.setId(otherIdTest);
    account2.setPassword("asdaZ12!asd");
    account2.setActive(true);
    account2.setRole(Role.ROLE_USER);
    mongoTemplate.save(account1);
    mongoTemplate.save(account2);
  }

  @AfterEach
  public void cleanUp() {
    accountRepository.deleteAll();
    shopRepository.deleteAll();
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

  @Test
  public void shouldSuccessAtHasRole() {
    assertTrue(accountRepository.hasRole(TEST_PHONE1, Role.ROLE_USER));
  }

  @Test
  public void shouldFailAtHasRoleWhenBadRole() {
    assertFalse(accountRepository.hasRole(TEST_PHONE1, Role.ROLE_SHOP));
  }

  @Test
  public void shouldFailAtHasRoleWhenUserNotFound() {
    assertNull(accountRepository.hasRole("+48123123321312213", Role.ROLE_SHOP));
  }

  @Test
  public void shouldSuccessAtExistsByNameOtherThanPhoneWhenFound() {
    assertTrue(accountRepository.existsByNameOtherThanPhone(TEST_SHOP_NAME1, null));
  }

  @Test
  public void shouldFailAtExistsByNameOtherThanPhoneWhenNotFound() {
    assertFalse(accountRepository.existsByNameOtherThanPhone("cos1", null));
  }

  @Test
  public void shouldFailAtExistsByNameOtherThanPhoneWhenFoundButItsYours() {
    assertFalse(accountRepository.existsByNameOtherThanPhone(TEST_SHOP_NAME1, TEST_PHONE1));
  }

  @Test
  public void shouldSuccessAtExistsByAccountNumberOtherThanPhoneWhenFound() {
    assertTrue(accountRepository.existsByAccountNumberOtherThanPhone(TEST_ACCOUNT_NUMBER1, null));
  }

  @Test
  public void shouldFailAtExistsByAccountNumberOtherThanPhoneWhenNotFound() {
    assertFalse(accountRepository.existsByAccountNumberOtherThanPhone("cos2", null));
  }

  @Test
  public void shouldFailAtExistsByAccountNumberOtherThanPhoneWhenFoundButItsYours() {
    assertFalse(
        accountRepository.existsByAccountNumberOtherThanPhone(TEST_ACCOUNT_NUMBER1, TEST_PHONE1));
  }

  @Test
  public void shouldSuccessAtExistsByPointsOtherThanPhoneWhenFound() {
    assertTrue(accountRepository.existsByPointsOtherThanPhone(address1, null));
  }

  @Test
  public void shouldFailAtExistsByPointsOtherThanPhoneWhenNotFound() {
    assertFalse(accountRepository.existsByPointsOtherThanPhone(null, null));
  }

  @Test
  public void shouldFailAtExistsByPointsOtherThanPhoneWhenFoundButItsYours() {
    assertFalse(accountRepository.existsByPointsOtherThanPhone(address1, TEST_PHONE1));
  }
}
