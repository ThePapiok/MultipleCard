package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mongodb.MongoWriteException;
import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.Role;
import com.thepapiok.multiplecard.collections.Shop;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.RegisterDTO;
import com.thepapiok.multiplecard.dto.RegisterShopDTO;
import com.thepapiok.multiplecard.misc.AccountConverter;
import com.thepapiok.multiplecard.misc.ShopConverter;
import com.thepapiok.multiplecard.misc.UserConverter;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import java.util.ArrayList;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

@Profile("test")
public class AuthenticationServiceTest {
  private static final String TEST_PHONE = "213442123411324";
  private static final String TEST_EMAIL = "1234213sddsdvdrw@pasf.pl";
  private static final String TEST_PASSWORD = "password";
  private static final String TEST_ENCODE_PASSWORD = "encodePassword";
  private static final String TEST_SHOP_NAME = "shopName";
  private static final String TEST_ACCOUNT_NUMBER = "12342132134132443212314431224323414132";
  private static final ObjectId TEST_ID = new ObjectId("123456789012345678901234");

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
  @Mock private ShopConverter shopConverter;
  @Mock private CloudinaryService cloudinaryService;

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
            mongoTemplate,
            shopConverter,
            cloudinaryService);
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
    expectedUser2.setId(TEST_ID);
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
  public void shouldSuccessAtPhoneExists() {
    when(accountRepository.existsByPhone(TEST_PHONE)).thenReturn(true);

    assertTrue(authenticationService.phoneExists(TEST_PHONE));
  }

  @Test
  public void shouldFailAtPhoneExists() {
    when(accountRepository.existsByPhone(TEST_PHONE)).thenReturn(false);

    assertFalse(authenticationService.phoneExists(TEST_PHONE));
  }

  @Test
  public void shouldSuccessAtEmailExists() {
    when(accountRepository.existsByEmail(TEST_EMAIL)).thenReturn(true);

    assertTrue(authenticationService.emailExists(TEST_EMAIL));
  }

  @Test
  public void shouldFailAtEmailExists() {
    when(accountRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);

    assertFalse(authenticationService.emailExists(TEST_EMAIL));
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

  @Test
  public void shouldSuccessAtCreateShop() {
    final String url = "fasdfds123123sads";
    final String email = "email@email";
    Address address1 = new Address();
    Address address2 = new Address();
    List<Address> addresses = new ArrayList<>();
    addresses.add(address1);
    addresses.add(address2);
    byte[] bytes = new byte[0];
    MultipartFile multipartFile = new MockMultipartFile("file", bytes);
    RegisterShopDTO registerShopDTO = new RegisterShopDTO();
    registerShopDTO.setName(TEST_SHOP_NAME);
    registerShopDTO.setAccountNumber(TEST_ACCOUNT_NUMBER);
    registerShopDTO.setFile(multipartFile);
    registerShopDTO.setEmail(email);
    registerShopDTO.setPassword(TEST_PASSWORD);
    registerShopDTO.setPhone(TEST_PHONE);
    Shop shop = new Shop();
    shop.setName(TEST_SHOP_NAME);
    shop.setAccountNumber(TEST_ACCOUNT_NUMBER);
    shop.setImageUrl("");
    shop.setPoints(addresses);
    Shop expectedShop = new Shop();
    expectedShop.setName(TEST_SHOP_NAME);
    expectedShop.setAccountNumber(TEST_ACCOUNT_NUMBER);
    expectedShop.setImageUrl("");
    expectedShop.setPoints(addresses);
    expectedShop.setTotalAmount(0L);
    Shop expectedShopWithId = new Shop();
    expectedShopWithId.setId(TEST_ID);
    expectedShopWithId.setName(TEST_SHOP_NAME);
    expectedShopWithId.setAccountNumber(TEST_ACCOUNT_NUMBER);
    expectedShopWithId.setImageUrl("");
    expectedShopWithId.setPoints(addresses);
    expectedShopWithId.setTotalAmount(0L);
    Shop expectedShopWithIdAndUrl = new Shop();
    expectedShopWithIdAndUrl.setId(TEST_ID);
    expectedShopWithIdAndUrl.setImageUrl(url);
    expectedShopWithIdAndUrl.setName(TEST_SHOP_NAME);
    expectedShopWithIdAndUrl.setAccountNumber(TEST_ACCOUNT_NUMBER);
    expectedShopWithIdAndUrl.setPoints(addresses);
    expectedShopWithIdAndUrl.setTotalAmount(0L);
    Account account = new Account();
    account.setPassword(TEST_ENCODE_PASSWORD);
    account.setPhone(TEST_PHONE);
    account.setEmail(email);
    Account expectedAccount = new Account();
    expectedAccount.setPassword(TEST_ENCODE_PASSWORD);
    expectedAccount.setPhone(TEST_PHONE);
    expectedAccount.setEmail(email);
    expectedAccount.setId(TEST_ID);
    expectedAccount.setRole(Role.ROLE_SHOP);
    expectedAccount.setActive(false);
    expectedAccount.setBanned(false);

    when(shopConverter.getEntity(registerShopDTO)).thenReturn(shop);
    when(mongoTemplate.save(expectedShop)).thenReturn(expectedShopWithId);
    when(cloudinaryService.addImage(bytes, TEST_ID.toString())).thenReturn(url);
    when(accountConverter.getEntity(registerShopDTO)).thenReturn(account);

    assertTrue(authenticationService.createShop(registerShopDTO));
    verify(mongoTemplate).save(expectedShop);
    verify(mongoTemplate).save(expectedShopWithIdAndUrl);
    verify(mongoTemplate).save(expectedAccount);
  }

  @Test
  public void shouldFailAtCreateShopWhenGetException() {
    Address address1 = new Address();
    Address address2 = new Address();
    List<Address> addresses = new ArrayList<>();
    addresses.add(address1);
    addresses.add(address2);
    RegisterShopDTO registerShopDTO = new RegisterShopDTO();
    registerShopDTO.setName(TEST_SHOP_NAME);
    registerShopDTO.setAccountNumber(TEST_SHOP_NAME);
    Shop shop = new Shop();
    shop.setName(TEST_SHOP_NAME);
    shop.setAccountNumber(TEST_ACCOUNT_NUMBER);
    shop.setImageUrl("");
    shop.setPoints(addresses);
    Shop expectedShopWithId = new Shop();
    expectedShopWithId.setId(TEST_ID);
    expectedShopWithId.setName(TEST_SHOP_NAME);
    expectedShopWithId.setAccountNumber(TEST_ACCOUNT_NUMBER);
    expectedShopWithId.setImageUrl("");
    expectedShopWithId.setPoints(addresses);
    expectedShopWithId.setTotalAmount(0L);

    when(shopConverter.getEntity(registerShopDTO)).thenReturn(shop);
    when(mongoTemplate.save(expectedShopWithId)).thenThrow(MongoWriteException.class);

    assertFalse(authenticationService.createShop(registerShopDTO));
  }
}
