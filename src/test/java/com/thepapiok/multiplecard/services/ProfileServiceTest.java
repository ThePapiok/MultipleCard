package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.mongodb.MongoWriteException;
import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.Card;
import com.thepapiok.multiplecard.collections.Like;
import com.thepapiok.multiplecard.collections.Order;
import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.collections.Role;
import com.thepapiok.multiplecard.collections.Shop;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.dto.AddressDTO;
import com.thepapiok.multiplecard.dto.ProfileDTO;
import com.thepapiok.multiplecard.misc.ProfileConverter;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.CardRepository;
import com.thepapiok.multiplecard.repositories.OrderRepository;
import com.thepapiok.multiplecard.repositories.ProductRepository;
import com.thepapiok.multiplecard.repositories.UserRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;

public class ProfileServiceTest {
  private static final String TEST_PHONE = "+48755775676767";
  private static final String ID_PARAM = "_id";
  private static final String CARD_ID_PARAM = "cardId";
  private static final String REVIEW_USER_ID_PARAM = "reviewUserId";
  private static final String USER_ID_PARAM = "userId";
  private static final ObjectId TEST_PRODUCT_ID1 = new ObjectId("123132123312123312312577");
  private static final ObjectId TEST_PRODUCT_ID2 = new ObjectId("123132103312123310312577");
  private static final ObjectId TEST_PRODUCT_ID3 = new ObjectId("423132303311123310772591");
  private static final ObjectId TEST_ID = new ObjectId("123456789012345678901234");
  private static Address address;
  private static User user;
  private static ProfileDTO profileDTO;
  @Mock private AccountRepository accountRepository;
  @Mock private UserRepository userRepository;
  @Mock private ProfileConverter profileConverter;
  @Mock private CardRepository cardRepository;
  @Mock private OrderRepository orderRepository;
  @Mock private ProductRepository productRepository;
  @Mock private MongoTemplate mongoTemplate;
  @Mock private MongoTransactionManager mongoTransactionManager;
  @Mock private CloudinaryService cloudinaryService;
  private ProfileService profileService;

  @BeforeAll
  public static void setObjects() {
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
            cloudinaryService);
  }

  @Test
  public void shouldSuccessAtGetProfile() {
    Account account = new Account();
    account.setId(TEST_ID);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(user));
    when(profileConverter.getDTO(user)).thenReturn(profileDTO);

    assertEquals(profileDTO, profileService.getProfile(TEST_PHONE));
  }

  @Test
  public void shouldFailAtGetProfileWhenUserNotFound() {
    Account account = new Account();
    account.setId(TEST_ID);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(userRepository.findById(TEST_ID)).thenReturn(Optional.empty());

    assertNull(profileService.getProfile(TEST_PHONE));
  }

  @Test
  public void shouldSuccessAtDeleteAccountRoleShopWhenNoProducts() throws IOException {
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
  public void shouldSuccessAtDeleteAccountRoleShopWhenProductsButNoOrders() throws IOException {
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
    when(orderRepository.findAllByProductId(any())).thenReturn(List.of());

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
  }

  @Test
  public void shouldSuccessAtDeleteAccountRoleShopWhenProductsAndSomeOrders() throws IOException {
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
    Order order2 = new Order();
    order2.setAmount(amount2);
    order2.setCardId(cardId2);
    Order order3 = new Order();
    order3.setAmount(amount3);
    order3.setCardId(cardId3);
    orders.add(order1);
    orders.add(order2);
    orders.add(order3);

    when(accountRepository.findByPhone(TEST_PHONE)).thenReturn(account);
    when(productRepository.getAllByShopId(TEST_ID)).thenReturn(products);
    when(orderRepository.findAllByProductId(TEST_PRODUCT_ID1)).thenReturn(orders);
    when(orderRepository.findAllByProductId(TEST_PRODUCT_ID2)).thenReturn(List.of());
    when(orderRepository.findAllByProductId(TEST_PRODUCT_ID3)).thenReturn(List.of());

    assertTrue(profileService.deleteAccount(TEST_PHONE));
    verify(mongoTemplate).remove(query(where(ID_PARAM).is(TEST_ID)), Shop.class);
    verify(mongoTemplate).remove(account);
    verify(mongoTemplate).remove(product1);
    verify(mongoTemplate).remove(product2);
    verify(mongoTemplate).remove(product3);
    verify(mongoTemplate).remove(order1);
    verify(mongoTemplate).remove(order2);
    verify(mongoTemplate).remove(order3);
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
  }

  @Test
  public void shouldSuccessAtDeleteAccountRoleAdmin() {
    Account account = new Account();
    account.setId(TEST_ID);
    account.setRole(Role.ROLE_ADMIN);

    when(accountRepository.findByPhone(TEST_PHONE)).thenReturn(account);

    assertTrue(profileService.deleteAccount(TEST_PHONE));
    verify(mongoTemplate).remove(account);
    verify(mongoTemplate).remove(query(where(ID_PARAM).is(TEST_ID)), User.class);
  }

  @Test
  public void shouldSuccessAtDeleteAccountRoleUserWhenNoCard() {
    Account account = new Account();
    account.setId(TEST_ID);
    account.setRole(Role.ROLE_USER);

    when(accountRepository.findByPhone(TEST_PHONE)).thenReturn(account);
    when(cardRepository.findCardByUserId(TEST_ID)).thenReturn(null);

    assertTrue(profileService.deleteAccount(TEST_PHONE));
    verify(mongoTemplate).remove(account);
    verify(mongoTemplate).remove(query(where(ID_PARAM).is(TEST_ID)), User.class);
    verify(mongoTemplate).remove(query(where(REVIEW_USER_ID_PARAM).is(TEST_ID)), Like.class);
    verify(mongoTemplate).remove(query(where(USER_ID_PARAM).is(TEST_ID)), Like.class);
  }

  @Test
  public void shouldSuccessAtDeleteAccountRoleUserWithCard() throws IOException {
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
    verify(mongoTemplate).remove(query(where(REVIEW_USER_ID_PARAM).is(TEST_ID)), Like.class);
    verify(mongoTemplate).remove(query(where(USER_ID_PARAM).is(TEST_ID)), Like.class);
    verify(mongoTemplate).remove(query(where(CARD_ID_PARAM).is(cardId)), Order.class);
    verify(mongoTemplate).remove(card);
    verify(cloudinaryService).deleteImage(card.getId().toString());
  }

  @Test
  public void shouldFailAtDeleteAccountWhenGetException() {
    Account account = new Account();
    account.setId(TEST_ID);
    account.setRole(Role.ROLE_USER);

    when(accountRepository.findByPhone(TEST_PHONE)).thenReturn(account);
    doThrow(MongoWriteException.class).when(mongoTemplate).remove(account);

    assertFalse(profileService.deleteAccount(TEST_PHONE));
  }
}
