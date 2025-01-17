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
import com.thepapiok.multiplecard.dto.AddressDTO;
import com.thepapiok.multiplecard.dto.RegisterDTO;
import com.thepapiok.multiplecard.dto.RegisterShopDTO;
import com.thepapiok.multiplecard.misc.AccountConverter;
import com.thepapiok.multiplecard.misc.ShopConverter;
import com.thepapiok.multiplecard.misc.UserConverter;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
  @Mock private EmailService emailService;

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
            cloudinaryService,
            emailService);
    final String testText = "Test";
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setStreet(testText);
    addressDTO.setCity(testText);
    addressDTO.setCountry("Polska");
    addressDTO.setHouseNumber("1");
    addressDTO.setProvince(testText);
    addressDTO.setPostalCode(testText);
    registerDTO = new RegisterDTO();
    registerDTO.setFirstName(testText);
    registerDTO.setLastName(testText);
    registerDTO.setCallingCode("+48");
    registerDTO.setPhone("12312312321");
    registerDTO.setPassword("Test123!");
    registerDTO.setEmail("test@test");
    registerDTO.setAddress(addressDTO);
    Address expectedAddress = new Address();
    expectedAddress.setCity(addressDTO.getCity());
    expectedAddress.setStreet(addressDTO.getStreet());
    expectedAddress.setPostalCode(addressDTO.getPostalCode());
    expectedAddress.setHouseNumber(addressDTO.getHouseNumber());
    expectedAddress.setCountry(addressDTO.getCountry());
    expectedAddress.setProvince(addressDTO.getProvince());
    expectedUser = new User();
    expectedUser.setFirstName(registerDTO.getFirstName());
    expectedUser.setLastName(registerDTO.getLastName());
    expectedUser.setAddress(expectedAddress);
    expectedUser.setRestricted(false);
    expectedUser2 = new User();
    expectedUser2.setFirstName(registerDTO.getFirstName());
    expectedUser2.setLastName(registerDTO.getLastName());
    expectedUser2.setAddress(expectedAddress);
    expectedUser2.setId(TEST_ID);
    expectedUser2.setRestricted(false);
    expectedAccount = new Account();
    expectedAccount.setPassword("dsfbv134fvdb");
    expectedAccount.setPhone(registerDTO.getCallingCode() + registerDTO.getPhone());
    expectedAccount.setEmail(registerDTO.getEmail());
  }

  @Test
  public void shouldReturnTrueAtCreateUserWhenEverythingOk() {
    when(mongoTemplate.save(expectedUser)).thenReturn(expectedUser2);
    when(userConverter.getEntity(registerDTO)).thenReturn(expectedUser);
    when(accountConverter.getEntity(registerDTO)).thenReturn(expectedAccount);

    assertTrue(authenticationService.createUser(registerDTO));
    verify(mongoTemplate).save(expectedUser);
    verify(mongoTemplate).save(expectedAccount);
  }

  @Test
  public void shouldReturnFalseAtCreateUserWhenGetException() {
    when(mongoTemplate.save(expectedUser)).thenReturn(expectedUser2);
    when(userConverter.getEntity(registerDTO)).thenReturn(expectedUser);
    when(accountConverter.getEntity(registerDTO)).thenReturn(expectedAccount);
    when(mongoTemplate.save(expectedAccount)).thenThrow(MongoWriteException.class);

    assertFalse(authenticationService.createUser(registerDTO));
    verify(mongoTemplate).save(expectedUser);
    verify(mongoTemplate).save(expectedAccount);
  }

  @Test
  public void shouldReturnTrueAtPhoneExistsWhenUserFound() {
    when(accountRepository.existsByPhone(TEST_PHONE)).thenReturn(true);

    assertTrue(authenticationService.phoneExists(TEST_PHONE));
  }

  @Test
  public void shouldReturnFalseAtPhoneExistsWhenUserWithThatPhoneNotFound() {
    when(accountRepository.existsByPhone(TEST_PHONE)).thenReturn(false);

    assertFalse(authenticationService.phoneExists(TEST_PHONE));
  }

  @Test
  public void shouldReturnTrueAtEmailExistsWhenUserFound() {
    when(accountRepository.existsByEmail(TEST_EMAIL)).thenReturn(true);

    assertTrue(authenticationService.emailExists(TEST_EMAIL));
  }

  @Test
  public void shouldReturnFalseAtEmailExistsWhenUserWithThatEmailNotFound() {
    when(accountRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);

    assertFalse(authenticationService.emailExists(TEST_EMAIL));
  }

  @Test
  public void shouldReturnVerificationNumberAtGetVerificationNumberWhenEverythingOk() {
    authenticationService.setRandom(random);

    when(random.nextInt()).thenReturn(0);

    assertEquals("000 000", authenticationService.getVerificationNumber());
  }

  @Test
  public void shouldReturnTrueAtChangePasswordWhenEverythingOk() {
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
  public void shouldReturnFalseAtChangePasswordWhenGetException() {
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
  public void shouldReturnTrueAtGetAccountByPhoneWhenAccountFound() {
    Account account = new Account();

    when(accountRepository.findByPhone(TEST_PHONE)).thenReturn(account);

    assertTrue(authenticationService.getAccountByPhone(TEST_PHONE));
  }

  @Test
  public void shouldReturnFalseAtGetAccountByPhoneWhenAccountNotFound() {
    when(accountRepository.findByPhone(TEST_PHONE)).thenReturn(null);

    assertFalse(authenticationService.getAccountByPhone(TEST_PHONE));
  }

  @Test
  public void shouldReturnTrueAtCheckPasswordWhenEverythingOk() {
    Account account = new Account();
    account.setPhone(TEST_PHONE);
    account.setPassword(TEST_ENCODE_PASSWORD);

    when(accountRepository.findPasswordByPhone(TEST_PHONE)).thenReturn(account);
    when(passwordEncoder.matches(TEST_PASSWORD, TEST_ENCODE_PASSWORD)).thenReturn(true);

    assertTrue(authenticationService.checkPassword(TEST_PASSWORD, TEST_PHONE));
  }

  @Test
  public void shouldFalseAtCheckPasswordWhenBadPassword() {
    Account account = new Account();
    account.setPhone(TEST_PHONE);
    account.setPassword(TEST_ENCODE_PASSWORD);

    when(accountRepository.findPasswordByPhone(TEST_PHONE)).thenReturn(account);
    when(passwordEncoder.matches(TEST_PASSWORD, TEST_ENCODE_PASSWORD)).thenReturn(false);

    assertFalse(authenticationService.checkPassword(TEST_PASSWORD, TEST_PHONE));
  }

  @Test
  public void shouldReturnTrueAtCreateShopWhenEverythingOk() throws IOException {
    final String url = "fasdfds123123sads";
    final String email = "email@email";
    Locale locale = Locale.UK;
    Address address1 = new Address();
    Address address2 = new Address();
    List<Address> addresses = new ArrayList<>();
    addresses.add(address1);
    addresses.add(address2);
    byte[] bytes = new byte[0];
    MultipartFile multipartFile = new MockMultipartFile("file", bytes);
    Path path = Files.createTempFile("upload_", ".tmp");
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
    Shop expectedShopWithId = new Shop();
    expectedShopWithId.setId(TEST_ID);
    expectedShopWithId.setName(TEST_SHOP_NAME);
    expectedShopWithId.setAccountNumber(TEST_ACCOUNT_NUMBER);
    expectedShopWithId.setImageUrl("");
    expectedShopWithId.setPoints(addresses);
    Shop expectedShopWithIdAndUrl = new Shop();
    expectedShopWithIdAndUrl.setId(TEST_ID);
    expectedShopWithIdAndUrl.setImageUrl(url);
    expectedShopWithIdAndUrl.setName(TEST_SHOP_NAME);
    expectedShopWithIdAndUrl.setAccountNumber(TEST_ACCOUNT_NUMBER);
    expectedShopWithIdAndUrl.setPoints(addresses);
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

    assertTrue(
        authenticationService.createShop(
            registerShopDTO, path.toString(), List.of(multipartFile), locale));
    verify(mongoTemplate).save(expectedShop);
    verify(mongoTemplate).save(expectedShopWithIdAndUrl);
    verify(mongoTemplate).save(expectedAccount);
  }

  @Test
  public void shouldReturnFalseAtCreateShopWhenGetException() {
    final String filePath = "safdfdas12312";
    Locale locale = Locale.UK;
    List<MultipartFile> list = List.of();
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

    when(shopConverter.getEntity(registerShopDTO)).thenReturn(shop);
    when(mongoTemplate.save(expectedShopWithId)).thenThrow(MongoWriteException.class);

    assertFalse(authenticationService.createShop(registerShopDTO, filePath, list, locale));
  }
}
