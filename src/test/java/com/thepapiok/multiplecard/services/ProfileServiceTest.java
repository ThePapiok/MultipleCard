package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.mongodb.MongoWriteException;
import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.Blocked;
import com.thepapiok.multiplecard.collections.Card;
import com.thepapiok.multiplecard.collections.Like;
import com.thepapiok.multiplecard.collections.Order;
import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.collections.Promotion;
import com.thepapiok.multiplecard.collections.Role;
import com.thepapiok.multiplecard.collections.Shop;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.AddressDTO;
import com.thepapiok.multiplecard.dto.ProfileDTO;
import com.thepapiok.multiplecard.dto.ProfileShopDTO;
import com.thepapiok.multiplecard.misc.ProfileConverter;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.CardRepository;
import com.thepapiok.multiplecard.repositories.OrderRepository;
import com.thepapiok.multiplecard.repositories.ProductRepository;
import com.thepapiok.multiplecard.repositories.ShopRepository;
import com.thepapiok.multiplecard.repositories.UserRepository;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class ProfileServiceTest {
  private static final String TEST_PHONE = "+48755775676767";
  private static final String ID_PARAM = "_id";
  private static final String CARD_ID_PARAM = "cardId";
  private static final ObjectId TEST_PRODUCT_ID1 = new ObjectId("123132123312123312312577");
  private static final ObjectId TEST_PRODUCT_ID2 = new ObjectId("123132103312123310312577");
  private static final ObjectId TEST_PRODUCT_ID3 = new ObjectId("423132303311123310772591");
  private static final ObjectId TEST_ID = new ObjectId("123456789012345678901234");
  private static final String TEST_SHOP_NAME = "shopName";
  private static final Long TEST_TOTAL_AMOUNT = 3300L;
  private static final String TEST_FIRST_NAME = "firstNameShop";
  private static final String TEST_LAST_NAME = "lastNameShop";
  private static final String TEST_ACCOUNT_NUMBER = "accountNumberShop";
  private static final String PRODUCT_ID_PARAM = "productId";
  private static Address address;
  private static User user;
  private static Shop shop;
  private static ProfileDTO profileDTO;
  private static ProfileShopDTO profileShopDTO;
  @Mock private AccountRepository accountRepository;
  @Mock private UserRepository userRepository;
  @Mock private ProfileConverter profileConverter;
  @Mock private CardRepository cardRepository;
  @Mock private OrderRepository orderRepository;
  @Mock private ProductRepository productRepository;
  @Mock private MongoTemplate mongoTemplate;
  @Mock private MongoTransactionManager mongoTransactionManager;
  @Mock private CloudinaryService cloudinaryService;
  @Mock private ShopRepository shopRepository;
  @Mock private EmailService emailService;
  private ProfileService profileService;

  @BeforeEach
  public void setObjects() {
    final String street = "street";
    final String city = "city";
    final String country = "pl";
    final String province = "province";
    final String houseNumber = "1";
    final String postalCode = "postalCode";
    final String firstName = "firstName";
    final String lastName = "lastName";
    address = new Address();
    address.setStreet(street);
    address.setCity(city);
    address.setCountry(country);
    address.setProvince(province);
    address.setHouseNumber(houseNumber);
    address.setPostalCode(postalCode);
    user = new User();
    user.setAddress(address);
    user.setFirstName(firstName);
    user.setLastName(lastName);
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setPostalCode(postalCode);
    addressDTO.setApartmentNumber("");
    addressDTO.setCountry(country);
    addressDTO.setCity(city);
    addressDTO.setStreet(street);
    addressDTO.setProvince(province);
    addressDTO.setHouseNumber(houseNumber);
    profileDTO = new ProfileDTO();
    profileDTO.setAddress(addressDTO);
    profileDTO.setFirstName(firstName);
    profileDTO.setLastName(lastName);
    profileShopDTO = new ProfileShopDTO();
    profileShopDTO.setName(TEST_SHOP_NAME);
    profileShopDTO.setAccountNumber(TEST_ACCOUNT_NUMBER);
    profileShopDTO.setFirstName(TEST_FIRST_NAME);
    profileShopDTO.setLastName(TEST_LAST_NAME);
    profileShopDTO.setAddress(List.of(addressDTO));
    shop = new Shop();
    shop.setId(TEST_ID);
    shop.setName(TEST_SHOP_NAME);
    shop.setImageUrl("url");
    shop.setFirstName(TEST_FIRST_NAME);
    shop.setLastName(TEST_LAST_NAME);
    shop.setAccountNumber(TEST_ACCOUNT_NUMBER);
    shop.setPoints(List.of(address));
  }

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    profileService =
        new ProfileService(
            accountRepository,
            userRepository,
            profileConverter,
            cardRepository,
            orderRepository,
            productRepository,
            mongoTemplate,
            mongoTransactionManager,
            cloudinaryService,
            shopRepository,
            emailService);
  }

  @Test
  public void shouldReturnProfileDTOAtGetProfileWhenEverythingOk() {
    Account account = new Account();
    account.setId(TEST_ID);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(user));
    when(profileConverter.getDTO(user)).thenReturn(profileDTO);

    assertEquals(profileDTO, profileService.getProfile(TEST_PHONE));
  }

  @Test
  public void shouldReturnNullAtGetProfileWhenUserNotFound() {
    Account account = new Account();
    account.setId(TEST_ID);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findById(TEST_ID)).thenReturn(Optional.empty());

    assertNull(profileService.getProfile(TEST_PHONE));
  }

  @Test
  public void shouldReturnTrueAtEditProfileWhenEverythingOk() {
    when(profileConverter.getEntity(profileDTO, TEST_PHONE)).thenReturn(user);

    assertTrue(profileService.editProfile(profileDTO, TEST_PHONE));
    verify(userRepository).save(user);
  }

  @Test
  public void shouldReturnFalseAtEditProfileWhenGetException() {
    when(profileConverter.getEntity(profileDTO, TEST_PHONE)).thenReturn(user);
    doThrow(MongoWriteException.class).when(userRepository).save(user);

    assertFalse(profileService.editProfile(profileDTO, TEST_PHONE));
  }

  @Test
  public void shouldReturnTrueAtDeleteAccountRoleShopWhenNoProducts() throws IOException {
    Account account = new Account();
    account.setId(TEST_ID);
    account.setRole(Role.ROLE_SHOP);

    when(accountRepository.findByPhone(TEST_PHONE)).thenReturn(account);
    when(productRepository.getAllByShopId(TEST_ID)).thenReturn(List.of());

    assertTrue(profileService.deleteAccount(TEST_PHONE));
    verify(mongoTemplate).remove(query(where(ID_PARAM).is(TEST_ID)), Shop.class);
    verify(mongoTemplate).remove(account);
    verify(cloudinaryService).deleteImage(TEST_ID.toString());
  }

  @Test
  public void shouldReturnTrueAtDeleteAccountRoleShopWhenProductsButNoOrders() throws IOException {
    Account account = new Account();
    account.setId(TEST_ID);
    account.setRole(Role.ROLE_SHOP);
    List<Product> products = new ArrayList<>();
    Product product1 = new Product();
    product1.setId(TEST_PRODUCT_ID1);
    Product product2 = new Product();
    product2.setId(TEST_PRODUCT_ID2);
    Product product3 = new Product();
    product3.setId(TEST_PRODUCT_ID3);
    products.add(product1);
    products.add(product2);
    products.add(product3);

    when(accountRepository.findByPhone(TEST_PHONE)).thenReturn(account);
    when(productRepository.getAllByShopId(TEST_ID)).thenReturn(products);
    when(orderRepository.findAllByProductIdAndIsUsed(any(), eq(false))).thenReturn(List.of());

    assertTrue(profileService.deleteAccount(TEST_PHONE));
    verify(mongoTemplate).remove(query(where(ID_PARAM).is(TEST_ID)), Shop.class);
    verify(mongoTemplate).remove(account);
    verify(mongoTemplate).remove(product1);
    verify(mongoTemplate).remove(product2);
    verify(mongoTemplate).remove(product3);
    verify(cloudinaryService).deleteImage(TEST_ID.toString());
    verify(cloudinaryService).deleteImage(product1.getId().toString());
    verify(cloudinaryService).deleteImage(product2.getId().toString());
    verify(cloudinaryService).deleteImage(product3.getId().toString());
    verify(mongoTemplate)
        .remove(query(where(PRODUCT_ID_PARAM).is(TEST_PRODUCT_ID1)), Promotion.class);
    verify(mongoTemplate)
        .remove(query(where(PRODUCT_ID_PARAM).is(TEST_PRODUCT_ID2)), Promotion.class);
    verify(mongoTemplate)
        .remove(query(where(PRODUCT_ID_PARAM).is(TEST_PRODUCT_ID3)), Promotion.class);
    verify(mongoTemplate)
        .remove(query(where(PRODUCT_ID_PARAM).is(TEST_PRODUCT_ID1)), Blocked.class);
    verify(mongoTemplate)
        .remove(query(where(PRODUCT_ID_PARAM).is(TEST_PRODUCT_ID2)), Blocked.class);
    verify(mongoTemplate)
        .remove(query(where(PRODUCT_ID_PARAM).is(TEST_PRODUCT_ID3)), Blocked.class);
  }

  @Test
  public void shouldReturnTrueAtDeleteAccountRoleShopWhenProductsAndSomeOrders()
      throws IOException {
    final int centsPerZloty = 100;
    final String pointsParam = "points";
    final ObjectId cardId1 = new ObjectId("123132123312123312312579");
    final ObjectId cardId2 = new ObjectId("123132103312123310312579");
    final ObjectId cardId3 = new ObjectId("423132303311123310772599");
    final int amount1 = 10;
    final int amount2 = 20;
    final int amount3 = 30;
    Account account = new Account();
    account.setId(TEST_ID);
    account.setRole(Role.ROLE_SHOP);
    List<Product> products = new ArrayList<>();
    Product product1 = new Product();
    product1.setId(TEST_PRODUCT_ID1);
    Product product2 = new Product();
    product2.setId(TEST_PRODUCT_ID2);
    Product product3 = new Product();
    product3.setId(TEST_PRODUCT_ID3);
    products.add(product1);
    products.add(product2);
    products.add(product3);
    List<Order> orders = new ArrayList<>();
    Order order1 = new Order();
    order1.setAmount(amount1);
    order1.setCardId(cardId1);
    order1.setUsed(false);
    Order order2 = new Order();
    order2.setAmount(amount2);
    order2.setCardId(cardId2);
    order2.setUsed(false);
    Order order3 = new Order();
    order3.setAmount(amount3);
    order3.setCardId(cardId3);
    order3.setUsed(false);
    orders.add(order1);
    orders.add(order2);
    orders.add(order3);

    when(accountRepository.findByPhone(TEST_PHONE)).thenReturn(account);
    when(productRepository.getAllByShopId(TEST_ID)).thenReturn(products);
    when(orderRepository.findAllByProductIdAndIsUsed(TEST_PRODUCT_ID1, false)).thenReturn(orders);
    when(orderRepository.findAllByProductIdAndIsUsed(TEST_PRODUCT_ID2, false))
        .thenReturn(List.of());
    when(orderRepository.findAllByProductIdAndIsUsed(TEST_PRODUCT_ID3, false))
        .thenReturn(List.of());

    assertTrue(profileService.deleteAccount(TEST_PHONE));
    verify(mongoTemplate).remove(query(where(ID_PARAM).is(TEST_ID)), Shop.class);
    verify(mongoTemplate).remove(account);
    verify(mongoTemplate).remove(order1);
    verify(mongoTemplate).remove(order2);
    verify(mongoTemplate).remove(order3);
    verify(mongoTemplate).remove(product1);
    verify(mongoTemplate).remove(product2);
    verify(mongoTemplate).remove(product3);
    verify(cloudinaryService).deleteImage(TEST_ID.toString());
    verify(cloudinaryService).deleteImage(product1.getId().toString());
    verify(cloudinaryService).deleteImage(product2.getId().toString());
    verify(cloudinaryService).deleteImage(product3.getId().toString());
    verify(mongoTemplate)
        .updateFirst(
            query(where(CARD_ID_PARAM).is(cardId1)),
            new Update().inc(pointsParam, (amount1 / centsPerZloty)),
            User.class);
    verify(mongoTemplate)
        .updateFirst(
            query(where(CARD_ID_PARAM).is(cardId2)),
            new Update().inc(pointsParam, (amount2 / centsPerZloty)),
            User.class);
    verify(mongoTemplate)
        .updateFirst(
            query(where(CARD_ID_PARAM).is(cardId3)),
            new Update().inc(pointsParam, (amount3 / centsPerZloty)),
            User.class);
    verify(mongoTemplate)
        .remove(query(where(PRODUCT_ID_PARAM).is(TEST_PRODUCT_ID1)), Promotion.class);
    verify(mongoTemplate)
        .remove(query(where(PRODUCT_ID_PARAM).is(TEST_PRODUCT_ID2)), Promotion.class);
    verify(mongoTemplate)
        .remove(query(where(PRODUCT_ID_PARAM).is(TEST_PRODUCT_ID3)), Promotion.class);
    verify(mongoTemplate)
        .remove(query(where(PRODUCT_ID_PARAM).is(TEST_PRODUCT_ID1)), Blocked.class);
    verify(mongoTemplate)
        .remove(query(where(PRODUCT_ID_PARAM).is(TEST_PRODUCT_ID2)), Blocked.class);
    verify(mongoTemplate)
        .remove(query(where(PRODUCT_ID_PARAM).is(TEST_PRODUCT_ID3)), Blocked.class);
  }

  @Test
  public void shouldReturnTrueAtDeleteAccountWithRoleAdmin() {
    Account account = new Account();
    account.setId(TEST_ID);
    account.setRole(Role.ROLE_ADMIN);

    when(accountRepository.findByPhone(TEST_PHONE)).thenReturn(account);

    assertTrue(profileService.deleteAccount(TEST_PHONE));
    verify(mongoTemplate).remove(account);
    verify(mongoTemplate).remove(query(where(ID_PARAM).is(TEST_ID)), User.class);
  }

  @Test
  public void shouldReturnTrueAtDeleteAccountWithRoleUserWhenNoCard() {
    Account account = new Account();
    account.setId(TEST_ID);
    account.setRole(Role.ROLE_USER);

    when(accountRepository.findByPhone(TEST_PHONE)).thenReturn(account);
    when(cardRepository.findCardByUserId(TEST_ID)).thenReturn(null);

    assertTrue(profileService.deleteAccount(TEST_PHONE));
    verify(mongoTemplate).remove(account);
    verify(mongoTemplate).remove(query(where(ID_PARAM).is(TEST_ID)), User.class);
    verify(mongoTemplate).remove(query(where("reviewUserId").is(TEST_ID)), Like.class);
    verify(mongoTemplate).remove(query(where("userId").is(TEST_ID)), Like.class);
  }

  @Test
  public void shouldReturnTrueAtDeleteAccountRoleUserWithCard() throws IOException {
    final ObjectId cardId = new ObjectId("423132303311523310172599");
    Account account = new Account();
    account.setId(TEST_ID);
    account.setRole(Role.ROLE_USER);
    Card card = new Card();
    card.setId(cardId);

    when(accountRepository.findByPhone(TEST_PHONE)).thenReturn(account);
    when(cardRepository.findCardByUserId(TEST_ID)).thenReturn(card);

    assertTrue(profileService.deleteAccount(TEST_PHONE));
    verify(mongoTemplate).remove(account);
    verify(mongoTemplate).remove(query(where(ID_PARAM).is(TEST_ID)), User.class);
    verify(mongoTemplate).remove(query(where("reviewUserId").is(TEST_ID)), Like.class);
    verify(mongoTemplate).remove(query(where("userId").is(TEST_ID)), Like.class);
    verify(mongoTemplate).remove(query(where(CARD_ID_PARAM).is(cardId)), Order.class);
    verify(mongoTemplate).remove(card);
    verify(cloudinaryService).deleteImage(card.getId().toString());
  }

  @Test
  public void shouldReturnFalseAtDeleteAccountWhenGetException() {
    Account account = new Account();
    account.setId(TEST_ID);
    account.setRole(Role.ROLE_USER);

    when(accountRepository.findByPhone(TEST_PHONE)).thenReturn(account);
    doThrow(MongoWriteException.class).when(mongoTemplate).remove(account);

    assertFalse(profileService.deleteAccount(TEST_PHONE));
  }

  @Test
  public void shouldReturnTrueAtCheckRoleWhenUserHasThatRole() {
    when(accountRepository.hasRole(TEST_PHONE, Role.ROLE_USER)).thenReturn(true);

    assertTrue(profileService.checkRole(TEST_PHONE, Role.ROLE_USER));
  }

  @Test
  public void shouldReturnFalseAtCheckRoleWhenBadRole() {
    when(accountRepository.hasRole(TEST_PHONE, Role.ROLE_USER)).thenReturn(false);

    assertFalse(profileService.checkRole(TEST_PHONE, Role.ROLE_USER));
  }

  @Test
  public void shouldReturnFalseAtCheckRoleWhenUserNotFound() {
    when(accountRepository.hasRole(TEST_PHONE, Role.ROLE_USER)).thenReturn(null);

    assertFalse(profileService.checkRole(TEST_PHONE, Role.ROLE_USER));
  }

  @Test
  public void shouldReturnProfileShopDTOAtGetShopWhenEverythingOk() {
    Account account = new Account();
    account.setId(TEST_ID);
    Shop shop = new Shop();
    shop.setId(TEST_ID);
    ProfileShopDTO expectedProfileShopDTO = new ProfileShopDTO();

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(shopRepository.findById(TEST_ID)).thenReturn(Optional.of(shop));
    when(profileConverter.getDTO(shop)).thenReturn(expectedProfileShopDTO);

    assertEquals(expectedProfileShopDTO, profileService.getShop(TEST_PHONE));
  }

  @Test
  public void shouldReturnNullAtGetShopWhenShopNotFound() {
    Account account = new Account();
    account.setId(TEST_ID);
    Shop shop = new Shop();
    shop.setId(TEST_ID);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(shopRepository.findById(TEST_ID)).thenReturn(Optional.empty());

    assertNull(profileService.getShop(TEST_PHONE));
  }

  @Test
  public void shouldReturnTrueAtEditProfileShopWithoutNewImage() throws MessagingException {
    List<MultipartFile> list = List.of(new MockMultipartFile("file", new byte[0]));
    Account account = new Account();
    account.setActive(true);
    Account expectedAccount = new Account();
    expectedAccount.setActive(false);

    when(profileConverter.getEntity(profileShopDTO, TEST_PHONE)).thenReturn(shop);
    when(accountRepository.findByPhone(TEST_PHONE)).thenReturn(account);

    assertTrue(profileService.editProfileShop(profileShopDTO, null, null, list, TEST_PHONE));
    verify(mongoTemplate).save(shop);
    verify(mongoTemplate).save(account);
    verify(emailService).sendEmailWithAttachment(eq(shop), eq(account), any(), eq(list));
  }

  @Test
  public void shouldReturnTrueAtEditProfileShopWithNewImage()
      throws MessagingException, IOException {
    final String otherImageUrlTest = "newUrl";
    List<MultipartFile> list = List.of(new MockMultipartFile("file1", new byte[0]));
    Account account = new Account();
    account.setActive(true);
    Account expectedAccount = new Account();
    expectedAccount.setActive(false);
    Shop expectedShopWithNewUrl = new Shop();
    expectedShopWithNewUrl.setId(TEST_ID);
    expectedShopWithNewUrl.setName(TEST_SHOP_NAME);
    expectedShopWithNewUrl.setImageUrl(otherImageUrlTest);
    expectedShopWithNewUrl.setFirstName(TEST_FIRST_NAME);
    expectedShopWithNewUrl.setLastName(TEST_LAST_NAME);
    expectedShopWithNewUrl.setAccountNumber(TEST_ACCOUNT_NUMBER);
    expectedShopWithNewUrl.setPoints(List.of(address));
    MockMultipartFile multipartFile = new MockMultipartFile("file2", new byte[0]);
    Path path = Files.createTempFile("_upload", ".tmp");
    Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

    when(profileConverter.getEntity(profileShopDTO, TEST_PHONE)).thenReturn(shop);
    when(accountRepository.findByPhone(TEST_PHONE)).thenReturn(account);
    when(cloudinaryService.addImage(Files.readAllBytes(path), shop.getId().toString()))
        .thenReturn(otherImageUrlTest);

    assertTrue(
        profileService.editProfileShop(profileShopDTO, path.toString(), null, list, TEST_PHONE));
    verify(mongoTemplate).save(expectedShopWithNewUrl);
    verify(mongoTemplate).save(account);
    verify(emailService).sendEmailWithAttachment(eq(shop), eq(account), any(), eq(list));
  }

  @Test
  public void shouldReturnFalseAtEditProfileShopWhenGetException() {
    when(profileConverter.getEntity(profileShopDTO, TEST_PHONE)).thenReturn(shop);
    doThrow(MongoWriteException.class).when(mongoTemplate).save(shop);

    assertFalse(profileService.editProfileShop(profileShopDTO, null, null, null, TEST_PHONE));
  }
}
