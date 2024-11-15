package com.thepapiok.multiplecard.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.Blocked;
import com.thepapiok.multiplecard.collections.Category;
import com.thepapiok.multiplecard.collections.Order;
import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.collections.Promotion;
import com.thepapiok.multiplecard.collections.Role;
import com.thepapiok.multiplecard.collections.Shop;
import com.thepapiok.multiplecard.configs.DbConfig;
import com.thepapiok.multiplecard.dto.ProductDTO;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

@DataMongoTest
@ActiveProfiles("test")
@Import(DbConfig.class)
public class AggregationRepositoryTest {
  private static final ObjectId TEST_OWNER_ID = new ObjectId("123456789012345678901234");
  private static final String TEST_PHONE = "+48132423412342314231";
  private static final String TEST_PHONE3 = "+48135304342921";
  private static final String TEST_PRODUCT_NAME = "product";
  private static final String COUNT_FIELD = "count";
  private static final String DATE_FIELD = "date";
  private static final String PRICE_FIELD = "price";
  private static final String ADDED_FIELD = "added";
  private static final String TEST_CATEGORY_NAME = "category";
  private static final String TEST_SHOP_NAME = "shop1";
  private ProductDTO productDTO1;
  private ProductDTO productDTO2;
  private ProductDTO productDTO3;
  private ProductDTO productDTO4;
  private ProductDTO productDTO5;
  private ProductDTO productDTO7;
  private ProductDTO productDTO8;
  private ProductDTO productDTO9;
  private ProductDTO productDTO10;
  private ProductDTO productDTO11;
  private ProductDTO productDTO13;
  private ProductDTO productDTO14;
  private ProductDTO productDTO15;
  private ProductDTO productDTO17;

  @Autowired private MongoTemplate mongoTemplate;
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private ProductRepository productRepository;
  @Autowired private AccountRepository accountRepository;
  @Autowired private PromotionRepository promotionRepository;
  @Autowired private ShopRepository shopRepository;
  private AggregationRepository aggregationRepository;
  @MockBean private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() {
    final ObjectId testCardId = new ObjectId("123456789012345678901111");
    final ObjectId testOtherCardId = new ObjectId("855456789019940678901112");
    final int testProduct1Amount = 1233;
    final int testProduct2Amount = 123;
    final int testProduct3Amount = 1231;
    final int testProduct4Amount = 10;
    final int testProduct5Amount = 11;
    final int testProduct6Amount = 12;
    final int testProduct7Amount = 13;
    final int testProduct8Amount = 14;
    final int testProduct9Amount = 15;
    final int testProduct10Amount = 30;
    final int testProduct11Amount = 17;
    final int testProduct12Amount = 18;
    final int testProduct13Amount = 19;
    final int testProduct14Amount = 31;
    final int testProduct15Amount = 21;
    final int testProduct16Amount = 2;
    final int testProduct17Amount = 23;
    final int testPromotion1Amount = 100;
    final int testPromotion2Amount = 1200;
    final int testPromotion3Amount = 5;
    final int testYearStartAtPromotion1 = 2024;
    final int testMonthStartAtPromotion1 = 5;
    final int testDayStartAtPromotion1 = 3;
    final int testYearStartAtPromotion2 = 2024;
    final int testMonthStartAtPromotion2 = 1;
    final int testDayStartAtPromotion2 = 1;
    final int testYearStartAtPromotion3 = 2020;
    final int testMonthStartAtPromotion3 = 12;
    final int testDayStartAtPromotion3 = 11;
    final int testYearExpiredAtPromotion1 = 2025;
    final int testMonthExpiredAtPromotion1 = 10;
    final int testDayExpiredAtPromotion1 = 9;
    final int testYearExpiredAtPromotion2 = 2026;
    final int testMonthExpiredAtPromotion2 = 10;
    final int testDayExpiredAtPromotion2 = 9;
    final int testYearExpiredAtPromotion3 = 2022;
    final int testMonthExpiredAtPromotion3 = 10;
    final int testDayExpiredAtPromotion3 = 9;
    final int testOrder1Amount = 100;
    final int testOrder2Amount = 123;
    final int testOrder3Amount = 1200;
    final int testOrder4Amount = 10;
    final int testYear1 = 2014;
    final int testMonth1 = 1;
    final int testDay1 = 1;
    final int testHour1 = 1;
    final int testMinute1 = 1;
    final int testYear2 = 2015;
    final int testMonth2 = 12;
    final int testDay2 = 2;
    final int testHour2 = 3;
    final int testMinute2 = 10;
    final int testYear3 = 2019;
    final int testMonth3 = 12;
    final int testDay3 = 2;
    final int testHour3 = 3;
    final int testMinute3 = 10;
    final int testYear4 = 2022;
    final int testMonth4 = 12;
    final int testDay4 = 2;
    final int testHour4 = 3;
    final int testMinute4 = 10;
    final int testYear5 = 2023;
    final int testMonth5 = 1;
    final int testDay5 = 2;
    final int testHour5 = 3;
    final int testMinute5 = 10;
    final int testYear6 = 2023;
    final int testMonth6 = 2;
    final int testDay6 = 2;
    final int testHour6 = 3;
    final int testMinute6 = 10;
    final int testYear7 = 2023;
    final int testMonth7 = 3;
    final int testDay7 = 2;
    final int testHour7 = 3;
    final int testMinute7 = 10;
    final int testYear8 = 2023;
    final int testMonth8 = 4;
    final int testDay8 = 2;
    final int testHour8 = 3;
    final int testMinute8 = 10;
    final int testYear9 = 2023;
    final int testMonth9 = 5;
    final int testDay9 = 2;
    final int testHour9 = 3;
    final int testMinute9 = 10;
    final int testYear10 = 2023;
    final int testMonth10 = 6;
    final int testDay10 = 2;
    final int testHour10 = 3;
    final int testMinute10 = 10;
    final int testYear11 = 2023;
    final int testMonth11 = 9;
    final int testDay11 = 2;
    final int testHour11 = 3;
    final int testMinute11 = 10;
    final int testYear12 = 2023;
    final int testMonth12 = 8;
    final int testDay12 = 2;
    final int testHour12 = 3;
    final int testMinute12 = 10;
    final int testYear13 = 2023;
    final int testMonth13 = 7;
    final int testDay13 = 2;
    final int testHour13 = 3;
    final int testMinute13 = 10;
    final int testYear14 = 2023;
    final int testMonth14 = 12;
    final int testDay14 = 2;
    final int testHour14 = 3;
    final int testMinute14 = 10;
    final int testYear15 = 2023;
    final int testMonth15 = 11;
    final int testDay15 = 2;
    final int testHour15 = 3;
    final int testMinute15 = 10;
    final int testYear16 = 2023;
    final int testMonth16 = 10;
    final int testDay16 = 2;
    final int testHour16 = 3;
    final int testMinute16 = 10;
    final int testYear17 = 2024;
    final int testMonth17 = 1;
    final int testDay17 = 2;
    final int testHour17 = 3;
    final int testMinute17 = 10;
    final int testYear18 = 2001;
    final int testMonth18 = 1;
    final int testDay18 = 2;
    final int testHour18 = 3;
    final int testMinute18 = 10;
    final LocalDateTime testDate1 =
        LocalDateTime.of(testYear1, testMonth1, testDay1, testHour1, testMinute1);
    final LocalDateTime testDate2 =
        LocalDateTime.of(testYear2, testMonth2, testDay2, testHour2, testMinute2);
    final LocalDateTime testDate3 =
        LocalDateTime.of(testYear3, testMonth3, testDay3, testHour3, testMinute3);
    final LocalDateTime testDate4 =
        LocalDateTime.of(testYear4, testMonth4, testDay4, testHour4, testMinute4);
    final LocalDateTime testDate5 =
        LocalDateTime.of(testYear5, testMonth5, testDay5, testHour5, testMinute5);
    final LocalDateTime testDate6 =
        LocalDateTime.of(testYear6, testMonth6, testDay6, testHour6, testMinute6);
    final LocalDateTime testDate7 =
        LocalDateTime.of(testYear7, testMonth7, testDay7, testHour7, testMinute7);
    final LocalDateTime testDate8 =
        LocalDateTime.of(testYear8, testMonth8, testDay8, testHour8, testMinute8);
    final LocalDateTime testDate9 =
        LocalDateTime.of(testYear9, testMonth9, testDay9, testHour9, testMinute9);
    final LocalDateTime testDate10 =
        LocalDateTime.of(testYear10, testMonth10, testDay10, testHour10, testMinute10);
    final LocalDateTime testDate11 =
        LocalDateTime.of(testYear11, testMonth11, testDay11, testHour11, testMinute11);
    final LocalDateTime testDate12 =
        LocalDateTime.of(testYear12, testMonth12, testDay12, testHour12, testMinute12);
    final LocalDateTime testDate13 =
        LocalDateTime.of(testYear13, testMonth13, testDay13, testHour13, testMinute13);
    final LocalDateTime testDate14 =
        LocalDateTime.of(testYear14, testMonth14, testDay14, testHour14, testMinute14);
    final LocalDateTime testDate15 =
        LocalDateTime.of(testYear15, testMonth15, testDay15, testHour15, testMinute15);
    final LocalDateTime testDate16 =
        LocalDateTime.of(testYear16, testMonth16, testDay16, testHour16, testMinute16);
    final LocalDateTime testDate17 =
        LocalDateTime.of(testYear17, testMonth17, testDay17, testHour17, testMinute17);
    final LocalDateTime testDate18 =
        LocalDateTime.of(testYear18, testMonth18, testDay18, testHour18, testMinute18);
    aggregationRepository = new AggregationRepository(accountRepository, mongoTemplate);
    Category category1 = new Category();
    category1.setOwnerId(TEST_OWNER_ID);
    category1.setName(TEST_CATEGORY_NAME);
    category1 = mongoTemplate.save(category1);
    Category category2 = new Category();
    category2.setOwnerId(TEST_OWNER_ID);
    category2.setName("Category2");
    category2 = mongoTemplate.save(category2);
    Category category3 = new Category();
    category3.setOwnerId(TEST_OWNER_ID);
    category3.setName("other");
    category3 = mongoTemplate.save(category3);
    Category category4 = new Category();
    category4.setOwnerId(TEST_OWNER_ID);
    category4.setName("other category");
    category4 = mongoTemplate.save(category4);
    Category category5 = new Category();
    category5.setOwnerId(TEST_OWNER_ID);
    category5.setName("mat");
    category5 = mongoTemplate.save(category5);
    Address address = new Address();
    address.setCountry("country");
    address.setCity("city");
    address.setStreet("street");
    address.setProvince("province");
    address.setPostalCode("postalCode");
    address.setHouseNumber("house");
    address.setApartmentNumber(1);
    Shop shop1 = new Shop();
    shop1.setName(TEST_SHOP_NAME);
    shop1.setFirstName("firstNameShop1");
    shop1.setLastName("lastNameShop1");
    shop1.setImageUrl("imageUrl1");
    shop1.setPoints(List.of(address));
    shop1.setAccountNumber("account1");
    shop1 = mongoTemplate.save(shop1);
    Shop shop2 = new Shop();
    shop2.setName("shop2");
    shop2.setFirstName("firstNameShop2");
    shop2.setLastName("lastNameShop2");
    shop2.setImageUrl("imageUrl2");
    shop2.setPoints(List.of(address));
    shop2.setAccountNumber("account2");
    shop2 = mongoTemplate.save(shop2);
    Shop shop3 = new Shop();
    shop3.setName("shop3");
    shop3.setFirstName("firstNameShop3");
    shop3.setLastName("lastNameShop3");
    shop3.setImageUrl("imageUrl3");
    shop3.setPoints(List.of(address));
    shop3.setAccountNumber("account3");
    shop3 = mongoTemplate.save(shop3);
    Shop shop4 = new Shop();
    shop4.setName("shop4");
    shop4.setFirstName("firstNameShop4");
    shop4.setLastName("lastNameShop4");
    shop4.setImageUrl("imageUrl4");
    shop4.setPoints(List.of(address));
    shop4.setAccountNumber("account4");
    shop4 = mongoTemplate.save(shop4);
    Product product1 = new Product();
    product1.setImageUrl("url1");
    product1.setName(TEST_PRODUCT_NAME);
    product1.setDescription("description1");
    product1.setAmount(testProduct1Amount);
    product1.setBarcode("barcode1");
    product1.setShopId(shop1.getId());
    product1.setCategories(List.of(category1.getId()));
    product1.setUpdatedAt(testDate1);
    Product product2 = new Product();
    product2.setImageUrl("url2");
    product2.setName("product2");
    product2.setDescription("description2");
    product2.setAmount(testProduct2Amount);
    product2.setBarcode("barcode2");
    product2.setShopId(shop3.getId());
    product2.setCategories(List.of(category1.getId(), category5.getId()));
    product2.setUpdatedAt(testDate2);
    Product product3 = new Product();
    product3.setImageUrl("url3");
    product3.setName(TEST_PRODUCT_NAME);
    product3.setDescription("description3");
    product3.setAmount(testProduct3Amount);
    product3.setBarcode("barcode3");
    product3.setShopId(shop1.getId());
    product3.setCategories(List.of(category2.getId()));
    product3.setUpdatedAt(testDate3);
    Product product4 = new Product();
    product4.setImageUrl("url4");
    product4.setName("product4");
    product4.setDescription("description4");
    product4.setAmount(testProduct4Amount);
    product4.setBarcode("barcode4");
    product4.setShopId(shop3.getId());
    product4.setCategories(List.of(category2.getId()));
    product4.setUpdatedAt(testDate4);
    Product product5 = new Product();
    product5.setImageUrl("url45");
    product5.setName(TEST_PRODUCT_NAME);
    product5.setDescription("description5");
    product5.setAmount(testProduct5Amount);
    product5.setBarcode("barcode5");
    product5.setShopId(shop3.getId());
    product5.setCategories(List.of(category3.getId()));
    product5.setUpdatedAt(testDate5);
    Product product6 = new Product();
    product6.setImageUrl("url6");
    product6.setName(TEST_PRODUCT_NAME);
    product6.setDescription("description6");
    product6.setAmount(testProduct6Amount);
    product6.setBarcode("barcode6");
    product6.setShopId(shop3.getId());
    product6.setCategories(List.of(category1.getId(), category2.getId()));
    product6.setUpdatedAt(testDate6);
    Product product7 = new Product();
    product7.setImageUrl("url7");
    product7.setName("product7");
    product7.setDescription(TEST_PRODUCT_NAME);
    product7.setAmount(testProduct7Amount);
    product7.setBarcode("barcode7");
    product7.setShopId(shop3.getId());
    product7.setCategories(List.of(category4.getId()));
    product7.setUpdatedAt(testDate7);
    Product product8 = new Product();
    product8.setImageUrl("url8");
    product8.setName("product8");
    product8.setDescription("description8");
    product8.setAmount(testProduct8Amount);
    product8.setBarcode("barcode8");
    product8.setShopId(shop3.getId());
    product8.setCategories(List.of(category4.getId()));
    product8.setUpdatedAt(testDate8);
    Product product9 = new Product();
    product9.setImageUrl("url9");
    product9.setName("product9");
    product9.setDescription("description9");
    product9.setAmount(testProduct9Amount);
    product9.setBarcode("barcode9");
    product9.setShopId(shop3.getId());
    product9.setCategories(List.of(category1.getId()));
    product9.setUpdatedAt(testDate9);
    Product product10 = new Product();
    product10.setImageUrl("url10");
    product10.setName("product10");
    product10.setDescription("description10");
    product10.setAmount(testProduct10Amount);
    product10.setBarcode("barcode10");
    product10.setShopId(shop3.getId());
    product10.setCategories(List.of(category3.getId(), category1.getId()));
    product10.setUpdatedAt(testDate10);
    Product product11 = new Product();
    product11.setImageUrl("url11");
    product11.setName("product11");
    product11.setDescription("description11");
    product11.setAmount(testProduct11Amount);
    product11.setBarcode("barcode11");
    product11.setShopId(shop3.getId());
    product11.setCategories(List.of(category3.getId()));
    product11.setUpdatedAt(testDate11);
    Product product12 = new Product();
    product12.setImageUrl("url12");
    product12.setName("product12");
    product12.setDescription("description12");
    product12.setAmount(testProduct12Amount);
    product12.setBarcode("barcode12");
    product12.setShopId(shop3.getId());
    product12.setCategories(List.of(category5.getId()));
    product12.setUpdatedAt(testDate12);
    Product product13 = new Product();
    product13.setImageUrl("url13");
    product13.setName("product13");
    product13.setDescription("description13");
    product13.setAmount(testProduct13Amount);
    product13.setBarcode("barcode13");
    product13.setShopId(shop3.getId());
    product13.setCategories(List.of(category1.getId()));
    product13.setUpdatedAt(testDate13);
    Product product14 = new Product();
    product14.setImageUrl("url14");
    product14.setName("product14");
    product14.setDescription("description14");
    product14.setAmount(testProduct14Amount);
    product14.setBarcode("barcode14");
    product14.setShopId(shop1.getId());
    product14.setCategories(List.of(category2.getId()));
    product14.setUpdatedAt(testDate14);
    Product product15 = new Product();
    product15.setImageUrl("url15");
    product15.setName("product15");
    product15.setDescription("description15");
    product15.setAmount(testProduct15Amount);
    product15.setBarcode("barcode15");
    product15.setShopId(shop1.getId());
    product15.setCategories(List.of(category3.getId()));
    product15.setUpdatedAt(testDate15);
    Product product16 = new Product();
    product16.setImageUrl("url16");
    product16.setName("product16");
    product16.setDescription("description16");
    product16.setAmount(testProduct16Amount);
    product16.setBarcode("barcode16");
    product16.setShopId(shop3.getId());
    product16.setCategories(List.of(category4.getId()));
    product16.setUpdatedAt(testDate16);
    Product product17 = new Product();
    product17.setImageUrl("url17");
    product17.setName(TEST_PRODUCT_NAME);
    product17.setDescription("description17");
    product17.setAmount(testProduct17Amount);
    product17.setBarcode("barcode17");
    product17.setShopId(shop3.getId());
    product17.setCategories(List.of(category5.getId()));
    product17.setUpdatedAt(testDate17);
    product1 = mongoTemplate.save(product1);
    product2 = mongoTemplate.save(product2);
    mongoTemplate.save(product3);
    product4 = mongoTemplate.save(product4);
    mongoTemplate.save(product5);
    mongoTemplate.save(product6);
    mongoTemplate.save(product7);
    mongoTemplate.save(product8);
    mongoTemplate.save(product9);
    mongoTemplate.save(product10);
    mongoTemplate.save(product11);
    mongoTemplate.save(product12);
    mongoTemplate.save(product13);
    mongoTemplate.save(product14);
    mongoTemplate.save(product15);
    mongoTemplate.save(product16);
    mongoTemplate.save(product17);
    Promotion promotion1 = new Promotion();
    promotion1.setProductId(product1.getId());
    promotion1.setAmount(testPromotion1Amount);
    promotion1.setStartAt(
        LocalDate.of(
            testYearStartAtPromotion1, testMonthStartAtPromotion1, testDayStartAtPromotion1));
    promotion1.setExpiredAt(
        LocalDate.of(
            testYearExpiredAtPromotion1, testMonthExpiredAtPromotion1, testDayExpiredAtPromotion1));
    mongoTemplate.save(promotion1);
    Promotion promotion2 = new Promotion();
    promotion2.setProductId(product2.getId());
    promotion2.setAmount(testPromotion2Amount);
    promotion2.setStartAt(
        LocalDate.of(
            testYearStartAtPromotion2, testMonthStartAtPromotion2, testDayStartAtPromotion2));
    promotion2.setExpiredAt(
        LocalDate.of(
            testYearExpiredAtPromotion2, testMonthExpiredAtPromotion2, testDayExpiredAtPromotion2));
    mongoTemplate.save(promotion2);
    Promotion promotion3 = new Promotion();
    promotion3.setProductId(product4.getId());
    promotion3.setAmount(testPromotion3Amount);
    promotion3.setStartAt(
        LocalDate.of(
            testYearStartAtPromotion3, testMonthStartAtPromotion3, testDayStartAtPromotion3));
    promotion3.setExpiredAt(
        LocalDate.of(
            testYearExpiredAtPromotion3, testMonthExpiredAtPromotion3, testDayExpiredAtPromotion3));
    mongoTemplate.save(promotion3);
    Account account1 = new Account();
    account1.setId(shop1.getId());
    account1.setPhone(TEST_PHONE);
    account1.setEmail("test@test1");
    account1.setRole(Role.ROLE_SHOP);
    account1.setBanned(false);
    account1.setActive(true);
    account1.setPassword("password1");
    mongoTemplate.save(account1);
    Account account2 = new Account();
    account2.setId(shop2.getId());
    account2.setPhone("+48235324342423");
    account2.setEmail("test@test2");
    account2.setRole(Role.ROLE_SHOP);
    account2.setBanned(false);
    account2.setActive(true);
    account2.setPassword("password2");
    mongoTemplate.save(account2);
    Account account3 = new Account();
    account3.setId(shop3.getId());
    account3.setPhone(TEST_PHONE3);
    account3.setEmail("test@test3");
    account3.setRole(Role.ROLE_SHOP);
    account3.setBanned(false);
    account3.setActive(true);
    account3.setPassword("password3");
    mongoTemplate.save(account3);
    Account account4 = new Account();
    account4.setId(shop4.getId());
    account4.setPhone("+48121347392923");
    account4.setEmail("test@test4");
    account4.setRole(Role.ROLE_SHOP);
    account4.setBanned(false);
    account4.setActive(true);
    account4.setPassword("password4");
    mongoTemplate.save(account4);
    Order order1 = new Order();
    order1.setCardId(testCardId);
    order1.setAmount(testOrder1Amount);
    order1.setProductId(product1.getId());
    order1.setUsed(true);
    order1.setCreatedAt(testDate1);
    order1.setShopId(shop1.getId());
    mongoTemplate.save(order1);
    Order order2 = new Order();
    order2.setCardId(testCardId);
    order2.setAmount(testOrder2Amount);
    order2.setProductId(product1.getId());
    order2.setUsed(true);
    order2.setCreatedAt(testDate2);
    order2.setShopId(shop1.getId());
    mongoTemplate.save(order2);
    Order order3 = new Order();
    order3.setCardId(testCardId);
    order3.setAmount(testOrder3Amount);
    order3.setProductId(product1.getId());
    order3.setUsed(true);
    order3.setCreatedAt(testDate3);
    order3.setShopId(shop1.getId());
    mongoTemplate.save(order3);
    Order order4 = new Order();
    order4.setCardId(testOtherCardId);
    order4.setAmount(testOrder4Amount);
    order4.setProductId(product2.getId());
    order4.setUsed(true);
    order4.setCreatedAt(testDate4);
    order4.setShopId(shop3.getId());
    mongoTemplate.save(order4);
    Order order139 = new Order();
    order139.setCardId(testOtherCardId);
    order139.setAmount(testOrder4Amount);
    order139.setProductId(product2.getId());
    order139.setUsed(false);
    order139.setCreatedAt(testDate4);
    order139.setShopId(shop3.getId());
    mongoTemplate.save(order139);
    Order order140 = new Order();
    order140.setCardId(testOtherCardId);
    order140.setAmount(testOrder4Amount);
    order140.setProductId(product2.getId());
    order140.setUsed(false);
    order140.setCreatedAt(testDate4);
    order140.setShopId(shop3.getId());
    mongoTemplate.save(order140);
    Order order5 = new Order();
    order5.setCardId(testOtherCardId);
    order5.setAmount(testOrder4Amount);
    order5.setProductId(product4.getId());
    order5.setUsed(true);
    order5.setCreatedAt(testDate5);
    order5.setShopId(shop3.getId());
    mongoTemplate.save(order5);
    Order order6 = new Order();
    order6.setCardId(testOtherCardId);
    order6.setAmount(testOrder4Amount);
    order6.setProductId(product4.getId());
    order6.setUsed(true);
    order6.setCreatedAt(testDate5);
    order6.setShopId(shop3.getId());
    mongoTemplate.save(order6);
    Order order8 = new Order();
    order8.setCardId(testOtherCardId);
    order8.setAmount(testOrder4Amount);
    order8.setProductId(product5.getId());
    order8.setUsed(true);
    order8.setCreatedAt(testDate6);
    order8.setShopId(shop3.getId());
    mongoTemplate.save(order8);
    Order order9 = new Order();
    order9.setCardId(testOtherCardId);
    order9.setAmount(testOrder4Amount);
    order9.setProductId(product5.getId());
    order9.setUsed(true);
    order9.setCreatedAt(testDate6);
    order9.setShopId(shop3.getId());
    mongoTemplate.save(order9);
    Order order10 = new Order();
    order10.setCardId(testOtherCardId);
    order10.setAmount(testOrder4Amount);
    order10.setProductId(product5.getId());
    order10.setUsed(true);
    order10.setCreatedAt(testDate6);
    order10.setShopId(shop3.getId());
    mongoTemplate.save(order10);
    Order order11 = new Order();
    order11.setCardId(testOtherCardId);
    order11.setAmount(testOrder4Amount);
    order11.setProductId(product5.getId());
    order11.setUsed(true);
    order11.setCreatedAt(testDate6);
    order11.setShopId(shop3.getId());
    mongoTemplate.save(order11);
    Order order141 = new Order();
    order141.setCardId(testOtherCardId);
    order141.setAmount(testOrder4Amount);
    order141.setProductId(product5.getId());
    order141.setUsed(false);
    order141.setCreatedAt(testDate6);
    order141.setShopId(shop3.getId());
    mongoTemplate.save(order141);
    Order order142 = new Order();
    order142.setCardId(testOtherCardId);
    order142.setAmount(testOrder4Amount);
    order142.setProductId(product5.getId());
    order142.setUsed(false);
    order142.setCreatedAt(testDate6);
    order142.setShopId(shop3.getId());
    mongoTemplate.save(order142);
    Order order12 = new Order();
    order12.setCardId(testOtherCardId);
    order12.setAmount(testOrder4Amount);
    order12.setProductId(product6.getId());
    order12.setUsed(true);
    order12.setCreatedAt(testDate7);
    order12.setShopId(shop3.getId());
    mongoTemplate.save(order12);
    Order order13 = new Order();
    order13.setCardId(testOtherCardId);
    order13.setAmount(testOrder4Amount);
    order13.setProductId(product6.getId());
    order13.setUsed(true);
    order13.setCreatedAt(testDate7);
    order13.setShopId(shop3.getId());
    mongoTemplate.save(order13);
    Order order14 = new Order();
    order14.setCardId(testOtherCardId);
    order14.setAmount(testOrder4Amount);
    order14.setProductId(product6.getId());
    order14.setUsed(true);
    order14.setCreatedAt(testDate7);
    order14.setShopId(shop3.getId());
    mongoTemplate.save(order14);
    Order order15 = new Order();
    order15.setCardId(testOtherCardId);
    order15.setAmount(testOrder4Amount);
    order15.setProductId(product6.getId());
    order15.setUsed(true);
    order15.setCreatedAt(testDate7);
    order15.setShopId(shop3.getId());
    mongoTemplate.save(order15);
    Order order16 = new Order();
    order16.setCardId(testOtherCardId);
    order16.setAmount(testOrder4Amount);
    order16.setProductId(product6.getId());
    order16.setUsed(true);
    order16.setCreatedAt(testDate7);
    order16.setShopId(shop3.getId());
    mongoTemplate.save(order16);
    Order order17 = new Order();
    order17.setCardId(testOtherCardId);
    order17.setAmount(testOrder4Amount);
    order17.setProductId(product7.getId());
    order17.setUsed(true);
    order17.setCreatedAt(testDate8);
    order17.setShopId(shop3.getId());
    mongoTemplate.save(order17);
    Order order18 = new Order();
    order18.setCardId(testOtherCardId);
    order18.setAmount(testOrder4Amount);
    order18.setProductId(product7.getId());
    order18.setUsed(true);
    order18.setCreatedAt(testDate8);
    order18.setShopId(shop3.getId());
    mongoTemplate.save(order18);
    Order order19 = new Order();
    order19.setCardId(testOtherCardId);
    order19.setAmount(testOrder4Amount);
    order19.setProductId(product7.getId());
    order19.setUsed(true);
    order19.setCreatedAt(testDate8);
    order19.setShopId(shop3.getId());
    mongoTemplate.save(order19);
    Order order20 = new Order();
    order20.setCardId(testOtherCardId);
    order20.setAmount(testOrder4Amount);
    order20.setProductId(product7.getId());
    order20.setUsed(true);
    order20.setCreatedAt(testDate8);
    order20.setShopId(shop3.getId());
    mongoTemplate.save(order20);
    Order order21 = new Order();
    order21.setCardId(testOtherCardId);
    order21.setAmount(testOrder4Amount);
    order21.setProductId(product7.getId());
    order21.setUsed(true);
    order21.setCreatedAt(testDate8);
    order21.setShopId(shop3.getId());
    mongoTemplate.save(order21);
    Order order22 = new Order();
    order22.setCardId(testOtherCardId);
    order22.setAmount(testOrder4Amount);
    order22.setProductId(product7.getId());
    order22.setUsed(true);
    order22.setCreatedAt(testDate8);
    order22.setShopId(shop3.getId());
    mongoTemplate.save(order22);
    Order order23 = new Order();
    order23.setCardId(testOtherCardId);
    order23.setAmount(testOrder4Amount);
    order23.setProductId(product8.getId());
    order23.setUsed(true);
    order23.setCreatedAt(testDate9);
    order23.setShopId(shop3.getId());
    mongoTemplate.save(order23);
    Order order24 = new Order();
    order24.setCardId(testOtherCardId);
    order24.setAmount(testOrder4Amount);
    order24.setProductId(product8.getId());
    order24.setUsed(true);
    order24.setCreatedAt(testDate9);
    order24.setShopId(shop3.getId());
    mongoTemplate.save(order24);
    Order order25 = new Order();
    order25.setCardId(testOtherCardId);
    order25.setAmount(testOrder4Amount);
    order25.setProductId(product8.getId());
    order25.setUsed(true);
    order25.setCreatedAt(testDate9);
    order25.setShopId(shop3.getId());
    mongoTemplate.save(order25);
    Order order26 = new Order();
    order26.setCardId(testOtherCardId);
    order26.setAmount(testOrder4Amount);
    order26.setProductId(product8.getId());
    order26.setUsed(true);
    order26.setCreatedAt(testDate9);
    order26.setShopId(shop3.getId());
    mongoTemplate.save(order26);
    Order order27 = new Order();
    order27.setCardId(testOtherCardId);
    order27.setAmount(testOrder4Amount);
    order27.setProductId(product8.getId());
    order27.setUsed(true);
    order27.setCreatedAt(testDate9);
    order27.setShopId(shop3.getId());
    mongoTemplate.save(order27);
    Order order28 = new Order();
    order28.setCardId(testOtherCardId);
    order28.setAmount(testOrder4Amount);
    order28.setProductId(product8.getId());
    order28.setUsed(true);
    order28.setCreatedAt(testDate9);
    order28.setShopId(shop3.getId());
    mongoTemplate.save(order28);
    Order order29 = new Order();
    order29.setCardId(testOtherCardId);
    order29.setAmount(testOrder4Amount);
    order29.setProductId(product8.getId());
    order29.setUsed(true);
    order29.setCreatedAt(testDate9);
    order29.setShopId(shop3.getId());
    mongoTemplate.save(order29);
    Order order30 = new Order();
    order30.setCardId(testOtherCardId);
    order30.setAmount(testOrder4Amount);
    order30.setProductId(product9.getId());
    order30.setUsed(true);
    order30.setCreatedAt(testDate10);
    order30.setShopId(shop3.getId());
    mongoTemplate.save(order30);
    Order order31 = new Order();
    order31.setCardId(testOtherCardId);
    order31.setAmount(testOrder4Amount);
    order31.setProductId(product9.getId());
    order31.setUsed(true);
    order31.setCreatedAt(testDate10);
    order31.setShopId(shop3.getId());
    mongoTemplate.save(order31);
    Order order32 = new Order();
    order32.setCardId(testOtherCardId);
    order32.setAmount(testOrder4Amount);
    order32.setProductId(product9.getId());
    order32.setUsed(true);
    order32.setCreatedAt(testDate10);
    order32.setShopId(shop3.getId());
    mongoTemplate.save(order32);
    Order order33 = new Order();
    order33.setCardId(testOtherCardId);
    order33.setAmount(testOrder4Amount);
    order33.setProductId(product9.getId());
    order33.setUsed(true);
    order33.setCreatedAt(testDate10);
    order33.setShopId(shop3.getId());
    mongoTemplate.save(order33);
    Order order34 = new Order();
    order34.setCardId(testOtherCardId);
    order34.setAmount(testOrder4Amount);
    order34.setProductId(product9.getId());
    order34.setUsed(true);
    order34.setCreatedAt(testDate10);
    order34.setShopId(shop3.getId());
    mongoTemplate.save(order34);
    Order order36 = new Order();
    order36.setCardId(testOtherCardId);
    order36.setAmount(testOrder4Amount);
    order36.setProductId(product9.getId());
    order36.setUsed(true);
    order36.setCreatedAt(testDate10);
    order36.setShopId(shop3.getId());
    mongoTemplate.save(order36);
    Order order37 = new Order();
    order37.setCardId(testOtherCardId);
    order37.setAmount(testOrder4Amount);
    order37.setProductId(product9.getId());
    order37.setUsed(true);
    order37.setCreatedAt(testDate10);
    order37.setShopId(shop3.getId());
    mongoTemplate.save(order37);
    Order order38 = new Order();
    order38.setCardId(testOtherCardId);
    order38.setAmount(testOrder4Amount);
    order38.setProductId(product9.getId());
    order38.setUsed(true);
    order38.setCreatedAt(testDate10);
    order38.setShopId(shop3.getId());
    mongoTemplate.save(order38);
    Order order39 = new Order();
    order39.setCardId(testOtherCardId);
    order39.setAmount(testOrder4Amount);
    order39.setProductId(product10.getId());
    order39.setUsed(true);
    order39.setCreatedAt(testDate11);
    order39.setShopId(shop3.getId());
    mongoTemplate.save(order39);
    Order order40 = new Order();
    order40.setCardId(testOtherCardId);
    order40.setAmount(testOrder4Amount);
    order40.setProductId(product10.getId());
    order40.setUsed(true);
    order40.setCreatedAt(testDate11);
    order40.setShopId(shop3.getId());
    mongoTemplate.save(order40);
    Order order41 = new Order();
    order41.setCardId(testOtherCardId);
    order41.setAmount(testOrder4Amount);
    order41.setProductId(product10.getId());
    order41.setUsed(true);
    order41.setCreatedAt(testDate11);
    order41.setShopId(shop3.getId());
    mongoTemplate.save(order41);
    Order order42 = new Order();
    order42.setCardId(testOtherCardId);
    order42.setAmount(testOrder4Amount);
    order42.setProductId(product10.getId());
    order42.setUsed(true);
    order42.setCreatedAt(testDate11);
    order42.setShopId(shop3.getId());
    mongoTemplate.save(order42);
    Order order43 = new Order();
    order43.setCardId(testOtherCardId);
    order43.setAmount(testOrder4Amount);
    order43.setProductId(product10.getId());
    order43.setUsed(true);
    order43.setCreatedAt(testDate11);
    order43.setShopId(shop3.getId());
    mongoTemplate.save(order43);
    Order order44 = new Order();
    order44.setCardId(testOtherCardId);
    order44.setAmount(testOrder4Amount);
    order44.setProductId(product10.getId());
    order44.setUsed(true);
    order44.setCreatedAt(testDate11);
    order44.setShopId(shop3.getId());
    mongoTemplate.save(order44);
    Order order45 = new Order();
    order45.setCardId(testOtherCardId);
    order45.setAmount(testOrder4Amount);
    order45.setProductId(product10.getId());
    order45.setUsed(true);
    order45.setCreatedAt(testDate11);
    order45.setShopId(shop3.getId());
    mongoTemplate.save(order45);
    Order order46 = new Order();
    order46.setCardId(testOtherCardId);
    order46.setAmount(testOrder4Amount);
    order46.setProductId(product10.getId());
    order46.setUsed(true);
    order46.setCreatedAt(testDate11);
    order46.setShopId(shop3.getId());
    mongoTemplate.save(order46);
    Order order47 = new Order();
    order47.setCardId(testOtherCardId);
    order47.setAmount(testOrder4Amount);
    order47.setProductId(product10.getId());
    order47.setUsed(true);
    order47.setCreatedAt(testDate11);
    order47.setShopId(shop3.getId());
    mongoTemplate.save(order47);
    Order order48 = new Order();
    order48.setCardId(testOtherCardId);
    order48.setAmount(testOrder4Amount);
    order48.setProductId(product11.getId());
    order48.setUsed(true);
    order48.setCreatedAt(testDate12);
    order48.setShopId(shop3.getId());
    mongoTemplate.save(order48);
    Order order49 = new Order();
    order49.setCardId(testOtherCardId);
    order49.setAmount(testOrder4Amount);
    order49.setProductId(product11.getId());
    order49.setUsed(true);
    order49.setCreatedAt(testDate12);
    order49.setShopId(shop3.getId());
    mongoTemplate.save(order49);
    Order order50 = new Order();
    order50.setCardId(testOtherCardId);
    order50.setAmount(testOrder4Amount);
    order50.setProductId(product11.getId());
    order50.setUsed(true);
    order50.setCreatedAt(testDate12);
    order50.setShopId(shop3.getId());
    mongoTemplate.save(order50);
    Order order51 = new Order();
    order51.setCardId(testOtherCardId);
    order51.setAmount(testOrder4Amount);
    order51.setProductId(product11.getId());
    order51.setUsed(true);
    order51.setCreatedAt(testDate12);
    order51.setShopId(shop3.getId());
    mongoTemplate.save(order51);
    Order order52 = new Order();
    order52.setCardId(testOtherCardId);
    order52.setAmount(testOrder4Amount);
    order52.setProductId(product11.getId());
    order52.setUsed(true);
    order52.setCreatedAt(testDate12);
    order52.setShopId(shop3.getId());
    mongoTemplate.save(order52);
    Order order53 = new Order();
    order53.setCardId(testOtherCardId);
    order53.setAmount(testOrder4Amount);
    order53.setProductId(product11.getId());
    order53.setUsed(true);
    order53.setCreatedAt(testDate12);
    order53.setShopId(shop3.getId());
    mongoTemplate.save(order53);
    Order order54 = new Order();
    order54.setCardId(testOtherCardId);
    order54.setAmount(testOrder4Amount);
    order54.setProductId(product11.getId());
    order54.setUsed(true);
    order54.setCreatedAt(testDate12);
    order54.setShopId(shop3.getId());
    mongoTemplate.save(order54);
    Order order55 = new Order();
    order55.setCardId(testOtherCardId);
    order55.setAmount(testOrder4Amount);
    order55.setProductId(product11.getId());
    order55.setUsed(true);
    order55.setCreatedAt(testDate12);
    order55.setShopId(shop3.getId());
    mongoTemplate.save(order55);
    Order order56 = new Order();
    order56.setCardId(testOtherCardId);
    order56.setAmount(testOrder4Amount);
    order56.setProductId(product11.getId());
    order56.setUsed(true);
    order56.setCreatedAt(testDate12);
    order56.setShopId(shop3.getId());
    mongoTemplate.save(order56);
    Order order57 = new Order();
    order57.setCardId(testOtherCardId);
    order57.setAmount(testOrder4Amount);
    order57.setProductId(product11.getId());
    order57.setUsed(true);
    order57.setCreatedAt(testDate12);
    order57.setShopId(shop3.getId());
    mongoTemplate.save(order57);
    Order order58 = new Order();
    order58.setCardId(testOtherCardId);
    order58.setAmount(testOrder4Amount);
    order58.setProductId(product12.getId());
    order58.setUsed(true);
    order58.setCreatedAt(testDate13);
    order58.setShopId(shop3.getId());
    mongoTemplate.save(order58);
    Order order59 = new Order();
    order59.setCardId(testOtherCardId);
    order59.setAmount(testOrder4Amount);
    order59.setProductId(product12.getId());
    order59.setUsed(true);
    order59.setCreatedAt(testDate13);
    order59.setShopId(shop3.getId());
    mongoTemplate.save(order59);
    Order order60 = new Order();
    order60.setCardId(testOtherCardId);
    order60.setAmount(testOrder4Amount);
    order60.setProductId(product12.getId());
    order60.setUsed(true);
    order60.setCreatedAt(testDate13);
    order60.setShopId(shop3.getId());
    mongoTemplate.save(order60);
    Order order61 = new Order();
    order61.setCardId(testOtherCardId);
    order61.setAmount(testOrder4Amount);
    order61.setProductId(product12.getId());
    order61.setUsed(true);
    order61.setCreatedAt(testDate13);
    order61.setShopId(shop3.getId());
    mongoTemplate.save(order61);
    Order order62 = new Order();
    order62.setCardId(testOtherCardId);
    order62.setAmount(testOrder4Amount);
    order62.setProductId(product12.getId());
    order62.setUsed(true);
    order62.setCreatedAt(testDate13);
    order62.setShopId(shop3.getId());
    mongoTemplate.save(order62);
    Order order63 = new Order();
    order63.setCardId(testOtherCardId);
    order63.setAmount(testOrder4Amount);
    order63.setProductId(product12.getId());
    order63.setUsed(true);
    order63.setCreatedAt(testDate13);
    order63.setShopId(shop3.getId());
    mongoTemplate.save(order63);
    Order order64 = new Order();
    order64.setCardId(testOtherCardId);
    order64.setAmount(testOrder4Amount);
    order64.setProductId(product12.getId());
    order64.setUsed(true);
    order64.setCreatedAt(testDate13);
    order64.setShopId(shop3.getId());
    mongoTemplate.save(order64);
    Order order65 = new Order();
    order65.setCardId(testOtherCardId);
    order65.setAmount(testOrder4Amount);
    order65.setProductId(product12.getId());
    order65.setUsed(true);
    order65.setCreatedAt(testDate13);
    order65.setShopId(shop3.getId());
    mongoTemplate.save(order65);
    Order order66 = new Order();
    order66.setCardId(testOtherCardId);
    order66.setAmount(testOrder4Amount);
    order66.setProductId(product12.getId());
    order66.setUsed(true);
    order66.setCreatedAt(testDate13);
    order66.setShopId(shop3.getId());
    mongoTemplate.save(order66);
    Order order67 = new Order();
    order67.setCardId(testOtherCardId);
    order67.setAmount(testOrder4Amount);
    order67.setProductId(product12.getId());
    order67.setUsed(true);
    order67.setCreatedAt(testDate13);
    order67.setShopId(shop3.getId());
    mongoTemplate.save(order67);
    Order order68 = new Order();
    order68.setCardId(testOtherCardId);
    order68.setAmount(testOrder4Amount);
    order68.setProductId(product12.getId());
    order68.setUsed(true);
    order68.setCreatedAt(testDate13);
    order68.setShopId(shop3.getId());
    mongoTemplate.save(order68);
    Order order69 = new Order();
    order69.setCardId(testOtherCardId);
    order69.setAmount(testOrder4Amount);
    order69.setProductId(product13.getId());
    order69.setUsed(true);
    order69.setCreatedAt(testDate14);
    order69.setShopId(shop3.getId());
    mongoTemplate.save(order69);
    Order order70 = new Order();
    order70.setCardId(testOtherCardId);
    order70.setAmount(testOrder4Amount);
    order70.setProductId(product13.getId());
    order70.setUsed(true);
    order70.setCreatedAt(testDate14);
    order70.setShopId(shop3.getId());
    mongoTemplate.save(order70);
    Order order71 = new Order();
    order71.setCardId(testOtherCardId);
    order71.setAmount(testOrder4Amount);
    order71.setProductId(product13.getId());
    order71.setUsed(true);
    order71.setCreatedAt(testDate14);
    order71.setShopId(shop3.getId());
    mongoTemplate.save(order71);
    Order order72 = new Order();
    order72.setCardId(testOtherCardId);
    order72.setAmount(testOrder4Amount);
    order72.setProductId(product13.getId());
    order72.setUsed(true);
    order72.setCreatedAt(testDate14);
    order72.setShopId(shop3.getId());
    mongoTemplate.save(order72);
    Order order73 = new Order();
    order73.setCardId(testOtherCardId);
    order73.setAmount(testOrder4Amount);
    order73.setProductId(product13.getId());
    order73.setUsed(true);
    order73.setCreatedAt(testDate14);
    order73.setShopId(shop3.getId());
    mongoTemplate.save(order73);
    Order order74 = new Order();
    order74.setCardId(testOtherCardId);
    order74.setAmount(testOrder4Amount);
    order74.setProductId(product13.getId());
    order74.setUsed(true);
    order74.setCreatedAt(testDate14);
    order74.setShopId(shop3.getId());
    mongoTemplate.save(order74);
    Order order75 = new Order();
    order75.setCardId(testOtherCardId);
    order75.setAmount(testOrder4Amount);
    order75.setProductId(product13.getId());
    order75.setUsed(true);
    order75.setCreatedAt(testDate14);
    order75.setShopId(shop3.getId());
    mongoTemplate.save(order75);
    Order order76 = new Order();
    order76.setCardId(testOtherCardId);
    order76.setAmount(testOrder4Amount);
    order76.setProductId(product13.getId());
    order76.setUsed(true);
    order76.setCreatedAt(testDate14);
    order76.setShopId(shop3.getId());
    mongoTemplate.save(order76);
    Order order77 = new Order();
    order77.setCardId(testOtherCardId);
    order77.setAmount(testOrder4Amount);
    order77.setProductId(product13.getId());
    order77.setUsed(true);
    order77.setCreatedAt(testDate14);
    order77.setShopId(shop3.getId());
    mongoTemplate.save(order77);
    Order order78 = new Order();
    order78.setCardId(testOtherCardId);
    order78.setAmount(testOrder4Amount);
    order78.setProductId(product13.getId());
    order78.setUsed(true);
    order78.setCreatedAt(testDate14);
    order78.setShopId(shop3.getId());
    mongoTemplate.save(order78);
    Order order79 = new Order();
    order79.setCardId(testOtherCardId);
    order79.setAmount(testOrder4Amount);
    order79.setProductId(product13.getId());
    order79.setUsed(true);
    order79.setCreatedAt(testDate14);
    order79.setShopId(shop3.getId());
    mongoTemplate.save(order79);
    Order order80 = new Order();
    order80.setCardId(testOtherCardId);
    order80.setAmount(testOrder4Amount);
    order80.setProductId(product13.getId());
    order80.setUsed(true);
    order80.setCreatedAt(testDate14);
    order80.setShopId(shop3.getId());
    mongoTemplate.save(order80);
    Order order81 = new Order();
    order81.setCardId(testOtherCardId);
    order81.setAmount(testOrder4Amount);
    order81.setProductId(product14.getId());
    order81.setUsed(true);
    order81.setCreatedAt(testDate15);
    order81.setShopId(shop1.getId());
    mongoTemplate.save(order81);
    Order order82 = new Order();
    order82.setCardId(testOtherCardId);
    order82.setAmount(testOrder4Amount);
    order82.setProductId(product14.getId());
    order82.setUsed(true);
    order82.setCreatedAt(testDate15);
    order82.setShopId(shop1.getId());
    mongoTemplate.save(order82);
    Order order83 = new Order();
    order83.setCardId(testOtherCardId);
    order83.setAmount(testOrder4Amount);
    order83.setProductId(product14.getId());
    order83.setUsed(true);
    order83.setCreatedAt(testDate15);
    order83.setShopId(shop1.getId());
    mongoTemplate.save(order83);
    Order order84 = new Order();
    order84.setCardId(testOtherCardId);
    order84.setAmount(testOrder4Amount);
    order84.setProductId(product14.getId());
    order84.setUsed(true);
    order84.setCreatedAt(testDate15);
    order84.setShopId(shop1.getId());
    mongoTemplate.save(order84);
    Order order85 = new Order();
    order85.setCardId(testOtherCardId);
    order85.setAmount(testOrder4Amount);
    order85.setProductId(product14.getId());
    order85.setUsed(true);
    order85.setCreatedAt(testDate15);
    order85.setShopId(shop1.getId());
    mongoTemplate.save(order85);
    Order order86 = new Order();
    order86.setCardId(testOtherCardId);
    order86.setAmount(testOrder4Amount);
    order86.setProductId(product14.getId());
    order86.setUsed(true);
    order86.setCreatedAt(testDate15);
    order86.setShopId(shop1.getId());
    mongoTemplate.save(order86);
    Order order87 = new Order();
    order87.setCardId(testOtherCardId);
    order87.setAmount(testOrder4Amount);
    order87.setProductId(product14.getId());
    order87.setUsed(true);
    order87.setCreatedAt(testDate15);
    order87.setShopId(shop1.getId());
    mongoTemplate.save(order87);
    Order order88 = new Order();
    order88.setCardId(testOtherCardId);
    order88.setAmount(testOrder4Amount);
    order88.setProductId(product14.getId());
    order88.setUsed(true);
    order88.setCreatedAt(testDate15);
    order88.setShopId(shop1.getId());
    mongoTemplate.save(order88);
    Order order89 = new Order();
    order89.setCardId(testOtherCardId);
    order89.setAmount(testOrder4Amount);
    order89.setProductId(product14.getId());
    order89.setUsed(true);
    order89.setCreatedAt(testDate15);
    order89.setShopId(shop1.getId());
    mongoTemplate.save(order89);
    Order order90 = new Order();
    order90.setCardId(testOtherCardId);
    order90.setAmount(testOrder4Amount);
    order90.setProductId(product14.getId());
    order90.setUsed(true);
    order90.setCreatedAt(testDate15);
    order90.setShopId(shop1.getId());
    mongoTemplate.save(order90);
    Order order91 = new Order();
    order91.setCardId(testOtherCardId);
    order91.setAmount(testOrder4Amount);
    order91.setProductId(product14.getId());
    order91.setUsed(true);
    order91.setCreatedAt(testDate15);
    order91.setShopId(shop1.getId());
    mongoTemplate.save(order91);
    Order order92 = new Order();
    order92.setCardId(testOtherCardId);
    order92.setAmount(testOrder4Amount);
    order92.setProductId(product14.getId());
    order92.setUsed(true);
    order92.setCreatedAt(testDate15);
    order92.setShopId(shop1.getId());
    mongoTemplate.save(order92);
    Order order93 = new Order();
    order93.setCardId(testOtherCardId);
    order93.setAmount(testOrder4Amount);
    order93.setProductId(product14.getId());
    order93.setUsed(true);
    order93.setCreatedAt(testDate15);
    order93.setShopId(shop1.getId());
    mongoTemplate.save(order93);
    Order order94 = new Order();
    order94.setCardId(testOtherCardId);
    order94.setAmount(testOrder4Amount);
    order94.setProductId(product15.getId());
    order94.setUsed(true);
    order94.setCreatedAt(testDate16);
    order94.setShopId(shop1.getId());
    mongoTemplate.save(order94);
    Order order95 = new Order();
    order95.setCardId(testOtherCardId);
    order95.setAmount(testOrder4Amount);
    order95.setProductId(product15.getId());
    order95.setUsed(true);
    order95.setCreatedAt(testDate16);
    order95.setShopId(shop1.getId());
    mongoTemplate.save(order95);
    Order order96 = new Order();
    order96.setCardId(testOtherCardId);
    order96.setAmount(testOrder4Amount);
    order96.setProductId(product15.getId());
    order96.setUsed(true);
    order96.setCreatedAt(testDate16);
    order96.setShopId(shop1.getId());
    mongoTemplate.save(order96);
    Order order97 = new Order();
    order97.setCardId(testOtherCardId);
    order97.setAmount(testOrder4Amount);
    order97.setProductId(product15.getId());
    order97.setUsed(true);
    order97.setCreatedAt(testDate16);
    order97.setShopId(shop1.getId());
    mongoTemplate.save(order97);
    Order order98 = new Order();
    order98.setCardId(testOtherCardId);
    order98.setAmount(testOrder4Amount);
    order98.setProductId(product15.getId());
    order98.setUsed(true);
    order98.setCreatedAt(testDate16);
    order98.setShopId(shop1.getId());
    mongoTemplate.save(order98);
    Order order99 = new Order();
    order99.setCardId(testOtherCardId);
    order99.setAmount(testOrder4Amount);
    order99.setProductId(product15.getId());
    order99.setUsed(true);
    order99.setCreatedAt(testDate16);
    order99.setShopId(shop1.getId());
    mongoTemplate.save(order99);
    Order order100 = new Order();
    order100.setCardId(testOtherCardId);
    order100.setAmount(testOrder4Amount);
    order100.setProductId(product15.getId());
    order100.setUsed(true);
    order100.setCreatedAt(testDate16);
    order100.setShopId(shop1.getId());
    mongoTemplate.save(order100);
    Order order101 = new Order();
    order101.setCardId(testOtherCardId);
    order101.setAmount(testOrder4Amount);
    order101.setProductId(product15.getId());
    order101.setUsed(true);
    order101.setCreatedAt(testDate16);
    order101.setShopId(shop1.getId());
    mongoTemplate.save(order101);
    Order order102 = new Order();
    order102.setCardId(testOtherCardId);
    order102.setAmount(testOrder4Amount);
    order102.setProductId(product15.getId());
    order102.setUsed(true);
    order102.setCreatedAt(testDate16);
    order102.setShopId(shop1.getId());
    mongoTemplate.save(order102);
    Order order103 = new Order();
    order103.setCardId(testOtherCardId);
    order103.setAmount(testOrder4Amount);
    order103.setProductId(product15.getId());
    order103.setUsed(true);
    order103.setCreatedAt(testDate16);
    order103.setShopId(shop1.getId());
    mongoTemplate.save(order103);
    Order order104 = new Order();
    order104.setCardId(testOtherCardId);
    order104.setAmount(testOrder4Amount);
    order104.setProductId(product15.getId());
    order104.setUsed(true);
    order104.setCreatedAt(testDate16);
    order104.setShopId(shop1.getId());
    mongoTemplate.save(order104);
    Order order105 = new Order();
    order105.setCardId(testOtherCardId);
    order105.setAmount(testOrder4Amount);
    order105.setProductId(product15.getId());
    order105.setUsed(true);
    order105.setCreatedAt(testDate16);
    order105.setShopId(shop1.getId());
    mongoTemplate.save(order105);
    Order order106 = new Order();
    order106.setCardId(testOtherCardId);
    order106.setAmount(testOrder4Amount);
    order106.setProductId(product15.getId());
    order106.setUsed(true);
    order106.setCreatedAt(testDate16);
    order106.setShopId(shop1.getId());
    mongoTemplate.save(order106);
    Order order107 = new Order();
    order107.setCardId(testOtherCardId);
    order107.setAmount(testOrder4Amount);
    order107.setProductId(product15.getId());
    order107.setUsed(true);
    order107.setCreatedAt(testDate16);
    order107.setShopId(shop1.getId());
    mongoTemplate.save(order107);
    Order order108 = new Order();
    order108.setCardId(testOtherCardId);
    order108.setAmount(testOrder4Amount);
    order108.setProductId(product16.getId());
    order108.setUsed(true);
    order108.setCreatedAt(testDate17);
    order108.setShopId(shop3.getId());
    mongoTemplate.save(order108);
    Order order109 = new Order();
    order109.setCardId(testOtherCardId);
    order109.setAmount(testOrder4Amount);
    order109.setProductId(product16.getId());
    order109.setUsed(true);
    order109.setCreatedAt(testDate17);
    order109.setShopId(shop3.getId());
    mongoTemplate.save(order109);
    Order order110 = new Order();
    order110.setCardId(testOtherCardId);
    order110.setAmount(testOrder4Amount);
    order110.setProductId(product16.getId());
    order110.setUsed(true);
    order110.setCreatedAt(testDate17);
    order110.setShopId(shop3.getId());
    mongoTemplate.save(order110);
    Order order111 = new Order();
    order111.setCardId(testOtherCardId);
    order111.setAmount(testOrder4Amount);
    order111.setProductId(product16.getId());
    order111.setUsed(true);
    order111.setCreatedAt(testDate17);
    order111.setShopId(shop3.getId());
    mongoTemplate.save(order111);
    Order order112 = new Order();
    order112.setCardId(testOtherCardId);
    order112.setAmount(testOrder4Amount);
    order112.setProductId(product16.getId());
    order112.setUsed(true);
    order112.setCreatedAt(testDate17);
    order112.setShopId(shop3.getId());
    mongoTemplate.save(order112);
    Order order113 = new Order();
    order113.setCardId(testOtherCardId);
    order113.setAmount(testOrder4Amount);
    order113.setProductId(product16.getId());
    order113.setUsed(true);
    order113.setCreatedAt(testDate17);
    order113.setShopId(shop3.getId());
    mongoTemplate.save(order113);
    Order order114 = new Order();
    order114.setCardId(testOtherCardId);
    order114.setAmount(testOrder4Amount);
    order114.setProductId(product16.getId());
    order114.setUsed(true);
    order114.setCreatedAt(testDate17);
    order114.setShopId(shop3.getId());
    mongoTemplate.save(order114);
    Order order115 = new Order();
    order115.setCardId(testOtherCardId);
    order115.setAmount(testOrder4Amount);
    order115.setProductId(product16.getId());
    order115.setUsed(true);
    order115.setCreatedAt(testDate17);
    order115.setShopId(shop3.getId());
    mongoTemplate.save(order115);
    Order order116 = new Order();
    order116.setCardId(testOtherCardId);
    order116.setAmount(testOrder4Amount);
    order116.setProductId(product16.getId());
    order116.setUsed(true);
    order116.setCreatedAt(testDate17);
    order116.setShopId(shop3.getId());
    mongoTemplate.save(order116);
    Order order117 = new Order();
    order117.setCardId(testOtherCardId);
    order117.setAmount(testOrder4Amount);
    order117.setProductId(product16.getId());
    order117.setUsed(true);
    order117.setCreatedAt(testDate17);
    order117.setShopId(shop3.getId());
    mongoTemplate.save(order117);
    Order order118 = new Order();
    order118.setCardId(testOtherCardId);
    order118.setAmount(testOrder4Amount);
    order118.setProductId(product16.getId());
    order118.setUsed(true);
    order118.setCreatedAt(testDate17);
    order118.setShopId(shop3.getId());
    mongoTemplate.save(order118);
    Order order119 = new Order();
    order119.setCardId(testOtherCardId);
    order119.setAmount(testOrder4Amount);
    order119.setProductId(product16.getId());
    order119.setUsed(true);
    order119.setCreatedAt(testDate17);
    order119.setShopId(shop3.getId());
    mongoTemplate.save(order119);
    Order order120 = new Order();
    order120.setCardId(testOtherCardId);
    order120.setAmount(testOrder4Amount);
    order120.setProductId(product16.getId());
    order120.setUsed(true);
    order120.setCreatedAt(testDate17);
    order120.setShopId(shop3.getId());
    mongoTemplate.save(order120);
    Order order121 = new Order();
    order121.setCardId(testOtherCardId);
    order121.setAmount(testOrder4Amount);
    order121.setProductId(product16.getId());
    order121.setUsed(true);
    order121.setCreatedAt(testDate17);
    order121.setShopId(shop3.getId());
    mongoTemplate.save(order121);
    Order order122 = new Order();
    order122.setCardId(testOtherCardId);
    order122.setAmount(testOrder4Amount);
    order122.setProductId(product16.getId());
    order122.setUsed(true);
    order122.setCreatedAt(testDate17);
    order122.setShopId(shop3.getId());
    mongoTemplate.save(order122);
    Order order123 = new Order();
    order123.setCardId(testOtherCardId);
    order123.setAmount(testOrder4Amount);
    order123.setProductId(product17.getId());
    order123.setUsed(true);
    order123.setCreatedAt(testDate18);
    order123.setShopId(shop3.getId());
    mongoTemplate.save(order123);
    Order order124 = new Order();
    order124.setCardId(testOtherCardId);
    order124.setAmount(testOrder4Amount);
    order124.setProductId(product17.getId());
    order124.setUsed(true);
    order124.setCreatedAt(testDate18);
    order124.setShopId(shop3.getId());
    mongoTemplate.save(order124);
    Order order125 = new Order();
    order125.setCardId(testOtherCardId);
    order125.setAmount(testOrder4Amount);
    order125.setProductId(product17.getId());
    order125.setUsed(true);
    order125.setCreatedAt(testDate18);
    order125.setShopId(shop3.getId());
    mongoTemplate.save(order125);
    Order order126 = new Order();
    order126.setCardId(testOtherCardId);
    order126.setAmount(testOrder4Amount);
    order126.setProductId(product17.getId());
    order126.setUsed(true);
    order126.setCreatedAt(testDate18);
    order126.setShopId(shop3.getId());
    mongoTemplate.save(order126);
    Order order127 = new Order();
    order127.setCardId(testOtherCardId);
    order127.setAmount(testOrder4Amount);
    order127.setProductId(product17.getId());
    order127.setUsed(true);
    order127.setCreatedAt(testDate18);
    order127.setShopId(shop3.getId());
    mongoTemplate.save(order127);
    Order order128 = new Order();
    order128.setCardId(testOtherCardId);
    order128.setAmount(testOrder4Amount);
    order128.setProductId(product17.getId());
    order128.setUsed(true);
    order128.setCreatedAt(testDate18);
    order128.setShopId(shop3.getId());
    mongoTemplate.save(order128);
    Order order129 = new Order();
    order129.setCardId(testOtherCardId);
    order129.setAmount(testOrder4Amount);
    order129.setProductId(product17.getId());
    order129.setUsed(true);
    order129.setCreatedAt(testDate18);
    order129.setShopId(shop3.getId());
    mongoTemplate.save(order129);
    Order order130 = new Order();
    order130.setCardId(testOtherCardId);
    order130.setAmount(testOrder4Amount);
    order130.setProductId(product17.getId());
    order130.setUsed(true);
    order130.setCreatedAt(testDate18);
    order130.setShopId(shop3.getId());
    mongoTemplate.save(order130);
    Order order131 = new Order();
    order131.setCardId(testOtherCardId);
    order131.setAmount(testOrder4Amount);
    order131.setProductId(product17.getId());
    order131.setUsed(true);
    order131.setCreatedAt(testDate18);
    order131.setShopId(shop3.getId());
    mongoTemplate.save(order131);
    Order order132 = new Order();
    order132.setCardId(testOtherCardId);
    order132.setAmount(testOrder4Amount);
    order132.setProductId(product17.getId());
    order132.setUsed(true);
    order132.setCreatedAt(testDate18);
    order132.setShopId(shop3.getId());
    mongoTemplate.save(order132);
    Order order133 = new Order();
    order133.setCardId(testOtherCardId);
    order133.setAmount(testOrder4Amount);
    order133.setProductId(product17.getId());
    order133.setUsed(true);
    order133.setCreatedAt(testDate18);
    order133.setShopId(shop3.getId());
    mongoTemplate.save(order133);
    Order order134 = new Order();
    order134.setCardId(testOtherCardId);
    order134.setAmount(testOrder4Amount);
    order134.setProductId(product17.getId());
    order134.setUsed(true);
    order134.setCreatedAt(testDate18);
    order134.setShopId(shop3.getId());
    mongoTemplate.save(order134);
    Order order135 = new Order();
    order135.setCardId(testOtherCardId);
    order135.setAmount(testOrder4Amount);
    order135.setProductId(product17.getId());
    order135.setUsed(true);
    order135.setCreatedAt(testDate18);
    order135.setShopId(shop3.getId());
    mongoTemplate.save(order135);
    Order order136 = new Order();
    order136.setCardId(testOtherCardId);
    order136.setAmount(testOrder4Amount);
    order136.setProductId(product17.getId());
    order136.setUsed(true);
    order136.setCreatedAt(testDate18);
    order136.setShopId(shop3.getId());
    mongoTemplate.save(order136);
    Order order137 = new Order();
    order137.setCardId(testOtherCardId);
    order137.setAmount(testOrder4Amount);
    order137.setProductId(product17.getId());
    order137.setUsed(true);
    order137.setCreatedAt(testDate18);
    order137.setShopId(shop3.getId());
    mongoTemplate.save(order137);
    Order order138 = new Order();
    order138.setCardId(testOtherCardId);
    order138.setAmount(testOrder4Amount);
    order138.setProductId(product17.getId());
    order138.setUsed(true);
    order138.setCreatedAt(testDate18);
    order138.setShopId(shop3.getId());
    mongoTemplate.save(order138);
    Blocked blocked1 = new Blocked();
    blocked1.setExpiredAt(LocalDate.now().plusYears(1));
    blocked1.setProductId(product3.getId());
    mongoTemplate.save(blocked1);
    Blocked blocked2 = new Blocked();
    blocked2.setExpiredAt(LocalDate.now().plusYears(1));
    blocked2.setProductId(product6.getId());
    mongoTemplate.save(blocked2);
    Blocked blocked3 = new Blocked();
    blocked3.setExpiredAt(LocalDate.now().plusYears(1));
    blocked3.setProductId(product12.getId());
    mongoTemplate.save(blocked3);
    Blocked blocked4 = new Blocked();
    blocked4.setExpiredAt(LocalDate.now().plusYears(1));
    blocked4.setProductId(product16.getId());
    mongoTemplate.save(blocked4);
    productDTO1 = new ProductDTO();
    productDTO1.setProductId(product1.getId().toString());
    productDTO1.setProductName(product1.getName());
    productDTO1.setActive(true);
    productDTO1.setAmount(product1.getAmount());
    productDTO1.setBarcode(product1.getBarcode());
    productDTO1.setDescription(product1.getDescription());
    productDTO1.setShopId(product1.getShopId());
    productDTO1.setCountPromotion(promotion1.getCount());
    productDTO1.setAmountPromotion(promotion1.getAmount());
    productDTO1.setProductImageUrl(product1.getImageUrl());
    productDTO1.setStartAtPromotion(promotion1.getStartAt());
    productDTO1.setExpiredAtPromotion(promotion1.getExpiredAt());
    productDTO2 = new ProductDTO();
    productDTO2.setProductName(product2.getName());
    productDTO2.setProductId(product2.getId().toString());
    productDTO2.setActive(true);
    productDTO2.setAmount(product2.getAmount());
    productDTO2.setBarcode(product2.getBarcode());
    productDTO2.setDescription(product2.getDescription());
    productDTO2.setShopId(product2.getShopId());
    productDTO2.setCountPromotion(promotion2.getCount());
    productDTO2.setAmountPromotion(promotion2.getAmount());
    productDTO2.setProductImageUrl(product2.getImageUrl());
    productDTO2.setStartAtPromotion(promotion2.getStartAt());
    productDTO2.setExpiredAtPromotion(promotion2.getExpiredAt());
    productDTO3 = new ProductDTO();
    productDTO3.setProductId(product3.getId().toString());
    productDTO3.setProductName(product3.getName());
    productDTO3.setActive(false);
    productDTO3.setAmount(product3.getAmount());
    productDTO3.setBarcode(product3.getBarcode());
    productDTO3.setDescription(product3.getDescription());
    productDTO3.setShopId(product3.getShopId());
    productDTO3.setCountPromotion(0);
    productDTO3.setAmountPromotion(0);
    productDTO3.setProductImageUrl(product3.getImageUrl());
    productDTO3.setStartAtPromotion(null);
    productDTO3.setExpiredAtPromotion(null);
    productDTO4 = new ProductDTO();
    productDTO4.setProductId(product4.getId().toString());
    productDTO4.setProductName(product4.getName());
    productDTO4.setActive(true);
    productDTO4.setAmount(product4.getAmount());
    productDTO4.setBarcode(product4.getBarcode());
    productDTO4.setDescription(product4.getDescription());
    productDTO4.setShopId(product4.getShopId());
    productDTO4.setCountPromotion(promotion3.getCount());
    productDTO4.setAmountPromotion(promotion3.getAmount());
    productDTO4.setProductImageUrl(product4.getImageUrl());
    productDTO4.setStartAtPromotion(promotion3.getStartAt());
    productDTO4.setExpiredAtPromotion(promotion3.getExpiredAt());
    productDTO5 = new ProductDTO();
    productDTO5.setProductId(product5.getId().toString());
    productDTO5.setProductName(product5.getName());
    productDTO5.setActive(true);
    productDTO5.setAmount(product5.getAmount());
    productDTO5.setBarcode(product5.getBarcode());
    productDTO5.setDescription(product5.getDescription());
    productDTO5.setShopId(product5.getShopId());
    productDTO5.setCountPromotion(0);
    productDTO5.setAmountPromotion(0);
    productDTO5.setProductImageUrl(product5.getImageUrl());
    productDTO5.setStartAtPromotion(null);
    productDTO5.setExpiredAtPromotion(null);
    productDTO7 = new ProductDTO();
    productDTO7.setProductId(product7.getId().toString());
    productDTO7.setProductName(product7.getName());
    productDTO7.setActive(true);
    productDTO7.setAmount(product7.getAmount());
    productDTO7.setBarcode(product7.getBarcode());
    productDTO7.setDescription(product7.getDescription());
    productDTO7.setShopId(product7.getShopId());
    productDTO7.setCountPromotion(0);
    productDTO7.setAmountPromotion(0);
    productDTO7.setProductImageUrl(product7.getImageUrl());
    productDTO7.setStartAtPromotion(null);
    productDTO7.setExpiredAtPromotion(null);
    productDTO8 = new ProductDTO();
    productDTO8.setProductId(product8.getId().toString());
    productDTO8.setProductName(product8.getName());
    productDTO8.setActive(true);
    productDTO8.setAmount(product8.getAmount());
    productDTO8.setBarcode(product8.getBarcode());
    productDTO8.setDescription(product8.getDescription());
    productDTO8.setShopId(product8.getShopId());
    productDTO8.setCountPromotion(0);
    productDTO8.setAmountPromotion(0);
    productDTO8.setProductImageUrl(product8.getImageUrl());
    productDTO8.setStartAtPromotion(null);
    productDTO8.setExpiredAtPromotion(null);
    productDTO9 = new ProductDTO();
    productDTO9.setProductId(product9.getId().toString());
    productDTO9.setProductName(product9.getName());
    productDTO9.setActive(true);
    productDTO9.setAmount(product9.getAmount());
    productDTO9.setBarcode(product9.getBarcode());
    productDTO9.setDescription(product9.getDescription());
    productDTO9.setShopId(product9.getShopId());
    productDTO9.setCountPromotion(0);
    productDTO9.setAmountPromotion(0);
    productDTO9.setProductImageUrl(product9.getImageUrl());
    productDTO9.setStartAtPromotion(null);
    productDTO9.setExpiredAtPromotion(null);
    productDTO10 = new ProductDTO();
    productDTO10.setProductId(product10.getId().toString());
    productDTO10.setProductName(product10.getName());
    productDTO10.setActive(true);
    productDTO10.setAmount(product10.getAmount());
    productDTO10.setBarcode(product10.getBarcode());
    productDTO10.setDescription(product10.getDescription());
    productDTO10.setShopId(product10.getShopId());
    productDTO10.setCountPromotion(0);
    productDTO10.setAmountPromotion(0);
    productDTO10.setProductImageUrl(product10.getImageUrl());
    productDTO10.setStartAtPromotion(null);
    productDTO10.setExpiredAtPromotion(null);
    productDTO11 = new ProductDTO();
    productDTO11.setProductId(product11.getId().toString());
    productDTO11.setProductName(product11.getName());
    productDTO11.setActive(true);
    productDTO11.setAmount(product11.getAmount());
    productDTO11.setBarcode(product11.getBarcode());
    productDTO11.setDescription(product11.getDescription());
    productDTO11.setShopId(product11.getShopId());
    productDTO11.setCountPromotion(0);
    productDTO11.setAmountPromotion(0);
    productDTO11.setProductImageUrl(product11.getImageUrl());
    productDTO11.setStartAtPromotion(null);
    productDTO11.setExpiredAtPromotion(null);
    productDTO13 = new ProductDTO();
    productDTO13.setProductId(product13.getId().toString());
    productDTO13.setProductName(product13.getName());
    productDTO13.setActive(true);
    productDTO13.setAmount(product13.getAmount());
    productDTO13.setBarcode(product13.getBarcode());
    productDTO13.setDescription(product13.getDescription());
    productDTO13.setShopId(product13.getShopId());
    productDTO13.setCountPromotion(0);
    productDTO13.setAmountPromotion(0);
    productDTO13.setProductImageUrl(product13.getImageUrl());
    productDTO13.setStartAtPromotion(null);
    productDTO13.setExpiredAtPromotion(null);
    productDTO14 = new ProductDTO();
    productDTO14.setProductId(product14.getId().toString());
    productDTO14.setProductName(product14.getName());
    productDTO14.setActive(true);
    productDTO14.setAmount(product14.getAmount());
    productDTO14.setBarcode(product14.getBarcode());
    productDTO14.setDescription(product14.getDescription());
    productDTO14.setShopId(product14.getShopId());
    productDTO14.setCountPromotion(0);
    productDTO14.setAmountPromotion(0);
    productDTO14.setProductImageUrl(product14.getImageUrl());
    productDTO14.setStartAtPromotion(null);
    productDTO14.setExpiredAtPromotion(null);
    productDTO15 = new ProductDTO();
    productDTO15.setProductId(product15.getId().toString());
    productDTO15.setProductName(product15.getName());
    productDTO15.setActive(true);
    productDTO15.setAmount(product15.getAmount());
    productDTO15.setBarcode(product15.getBarcode());
    productDTO15.setDescription(product15.getDescription());
    productDTO15.setShopId(product15.getShopId());
    productDTO15.setCountPromotion(0);
    productDTO15.setAmountPromotion(0);
    productDTO15.setProductImageUrl(product15.getImageUrl());
    productDTO15.setStartAtPromotion(null);
    productDTO15.setExpiredAtPromotion(null);
    productDTO17 = new ProductDTO();
    productDTO17.setProductId(product17.getId().toString());
    productDTO17.setProductName(product17.getName());
    productDTO17.setActive(true);
    productDTO17.setAmount(product17.getAmount());
    productDTO17.setBarcode(product17.getBarcode());
    productDTO17.setDescription(product17.getDescription());
    productDTO17.setShopId(product17.getShopId());
    productDTO17.setCountPromotion(0);
    productDTO17.setAmountPromotion(0);
    productDTO17.setProductImageUrl(product17.getImageUrl());
    productDTO17.setStartAtPromotion(null);
    productDTO17.setExpiredAtPromotion(null);
    TextIndexDefinition textIndex =
        new TextIndexDefinition.TextIndexDefinitionBuilder()
            .onFields("description", "name")
            .withDefaultLanguage("none")
            .build();
    mongoTemplate.indexOps(Product.class).ensureIndex(textIndex);
  }

  @AfterEach
  public void cleanUp() {
    promotionRepository.deleteAll();
    accountRepository.deleteAll();
    categoryRepository.deleteAll();
    productRepository.deleteAll();
    shopRepository.deleteAll();
    categoryRepository.deleteAll();
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsOwnerWhenCountFieldAndIsDescendingWithoutText() {
    assertEquals(
        List.of(productDTO15, productDTO14, productDTO1, productDTO3),
        aggregationRepository.getProducts(TEST_PHONE, 0, COUNT_FIELD, true, "", "", ""));
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsOwnerWhenCountFieldAndIsAscendingWithoutText() {
    assertEquals(
        List.of(productDTO3, productDTO1, productDTO14, productDTO15),
        aggregationRepository.getProducts(TEST_PHONE, 0, COUNT_FIELD, false, "", "", ""));
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsOwnerWhenDateFieldAndIsDescendingWithoutText() {
    assertEquals(
        List.of(productDTO14, productDTO15, productDTO1, productDTO3),
        aggregationRepository.getProducts(TEST_PHONE, 0, DATE_FIELD, true, "", "", ""));
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsOwnerWhenDateFieldAndIsAscendingWithoutText() {
    assertEquals(
        List.of(productDTO3, productDTO1, productDTO15, productDTO14),
        aggregationRepository.getProducts(TEST_PHONE, 0, DATE_FIELD, false, "", "", ""));
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsOwnerWhenPriceFieldAndIsDescendingWithoutText() {
    assertEquals(
        List.of(productDTO1, productDTO3, productDTO14, productDTO15),
        aggregationRepository.getProducts(TEST_PHONE, 0, PRICE_FIELD, true, "", "", ""));
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsOwnerWhenPriceFieldAndIsAscendingWithoutText() {
    assertEquals(
        List.of(productDTO15, productDTO14, productDTO3, productDTO1),
        aggregationRepository.getProducts(TEST_PHONE, 0, PRICE_FIELD, false, "", "", ""));
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsOwnerWhenAddedFieldAndIsDescendingWithoutText() {
    assertEquals(
        List.of(productDTO14, productDTO15, productDTO3, productDTO1),
        aggregationRepository.getProducts(TEST_PHONE, 0, ADDED_FIELD, true, "", "", ""));
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsOwnerWhenAddedFieldAndIsAscendingWithoutText() {
    assertEquals(
        List.of(productDTO1, productDTO3, productDTO15, productDTO14),
        aggregationRepository.getProducts(TEST_PHONE, 0, ADDED_FIELD, false, "", "", ""));
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsOwnerWhenCountFieldAndIsDescendingWithText() {
    assertEquals(
        List.of(productDTO1, productDTO3),
        aggregationRepository.getProducts(
            TEST_PHONE, 0, COUNT_FIELD, true, TEST_PRODUCT_NAME, "", ""));
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsWhenCountFieldAndIsDescendingWithoutText() {
    assertEquals(
        List.of(
            productDTO17,
            productDTO15,
            productDTO14,
            productDTO13,
            productDTO11,
            productDTO10,
            productDTO9,
            productDTO8,
            productDTO7,
            productDTO5,
            productDTO1,
            productDTO4),
        aggregationRepository.getProducts(null, 0, COUNT_FIELD, true, "", "", ""));
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsWhenCountFieldAndIsAscendingWithoutText() {
    assertEquals(
        List.of(
            productDTO2,
            productDTO4,
            productDTO1,
            productDTO5,
            productDTO7,
            productDTO8,
            productDTO9,
            productDTO10,
            productDTO11,
            productDTO13,
            productDTO14,
            productDTO15),
        aggregationRepository.getProducts(null, 0, COUNT_FIELD, false, "", "", ""));
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsWhenDateFieldAndIsDescendingWithoutText() {
    assertEquals(
        List.of(
            productDTO13,
            productDTO14,
            productDTO15,
            productDTO10,
            productDTO11,
            productDTO9,
            productDTO8,
            productDTO7,
            productDTO5,
            productDTO4,
            productDTO2,
            productDTO1),
        aggregationRepository.getProducts(null, 0, DATE_FIELD, true, "", "", ""));
  }

  @Test
  public void shouldReturnListOfProductGetDTOAtGetProductsWhenDateFieldAndIsAscendingWithoutText() {
    assertEquals(
        List.of(
            productDTO17,
            productDTO1,
            productDTO2,
            productDTO4,
            productDTO5,
            productDTO7,
            productDTO8,
            productDTO9,
            productDTO11,
            productDTO10,
            productDTO15,
            productDTO14),
        aggregationRepository.getProducts(null, 0, DATE_FIELD, false, "", "", ""));
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsWhenPriceFieldAndIsDescendingWithoutText() {
    assertEquals(
        List.of(
            productDTO1,
            productDTO2,
            productDTO14,
            productDTO10,
            productDTO17,
            productDTO15,
            productDTO13,
            productDTO11,
            productDTO9,
            productDTO8,
            productDTO7,
            productDTO5),
        aggregationRepository.getProducts(null, 0, PRICE_FIELD, true, "", "", ""));
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsWhenPriceFieldAndIsAscendingWithoutText() {
    assertEquals(
        List.of(
            productDTO4,
            productDTO5,
            productDTO7,
            productDTO8,
            productDTO9,
            productDTO11,
            productDTO13,
            productDTO15,
            productDTO17,
            productDTO10,
            productDTO14,
            productDTO2),
        aggregationRepository.getProducts(null, 0, PRICE_FIELD, false, "", "", ""));
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsWhenAddedFieldAndIsDescendingWithoutText() {
    assertEquals(
        List.of(
            productDTO17,
            productDTO14,
            productDTO15,
            productDTO11,
            productDTO13,
            productDTO10,
            productDTO9,
            productDTO8,
            productDTO7,
            productDTO5,
            productDTO4,
            productDTO2),
        aggregationRepository.getProducts(null, 0, ADDED_FIELD, true, "", "", ""));
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsWhenAddedFieldAndIsAscendingWithoutText() {
    assertEquals(
        List.of(
            productDTO1,
            productDTO2,
            productDTO4,
            productDTO5,
            productDTO7,
            productDTO8,
            productDTO9,
            productDTO10,
            productDTO13,
            productDTO11,
            productDTO15,
            productDTO14),
        aggregationRepository.getProducts(null, 0, ADDED_FIELD, false, "", "", ""));
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsWhenCountFieldAndIsDescendingWithoutTextWithCategory() {
    assertEquals(
        List.of(productDTO13, productDTO10, productDTO9, productDTO1, productDTO2),
        aggregationRepository.getProducts(null, 0, COUNT_FIELD, true, "", TEST_CATEGORY_NAME, ""));
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsWhenCountFieldAndIsDescendingWithoutTextWithShopName() {
    assertEquals(
        List.of(productDTO15, productDTO14, productDTO1),
        aggregationRepository.getProducts(null, 0, COUNT_FIELD, true, "", "", TEST_SHOP_NAME));
  }

  @Test
  public void shouldReturnListOfProductGetDTOAtGetProductsWhenCountFieldAndIsDescendingWithText() {
    assertEquals(
        List.of(productDTO17, productDTO7, productDTO5, productDTO1),
        aggregationRepository.getProducts(null, 0, COUNT_FIELD, true, TEST_PRODUCT_NAME, "", ""));
  }

  @Test
  public void shouldReturn1AtGetMaxPageWhenCount4ProductsAtGetOwnerProducts() {
    assertEquals(1, aggregationRepository.getMaxPage("", TEST_PHONE, "", ""));
  }

  @Test
  public void shouldReturn0AtGetMaxPageWhenCount0ProductsAtGetOwnerProducts() {
    assertEquals(0, aggregationRepository.getMaxPage("", "+48121347392923", "", ""));
  }

  @Test
  public void shouldReturn2AtGetMaxPageWhenCount13ProductsAtGetOwnerProducts() {
    assertEquals(2, aggregationRepository.getMaxPage("", TEST_PHONE3, "", ""));
  }

  @Test
  public void shouldReturn1AtGetMaxPageWhenCount4ProductsAtGetOwnerProductsAndText() {
    assertEquals(1, aggregationRepository.getMaxPage(TEST_PRODUCT_NAME, TEST_PHONE3, "", ""));
  }

  @Test
  public void shouldReturn2AtGetMaxPageWhenCount13ProductsAtGetAllProducts() {
    assertEquals(2, aggregationRepository.getMaxPage("", null, "", ""));
  }

  @Test
  public void shouldReturn1AtGetMaxPageWhenCount5ProductsAtGetAllProductsAndCategory() {
    assertEquals(1, aggregationRepository.getMaxPage("", null, TEST_CATEGORY_NAME, ""));
  }

  @Test
  public void shouldReturn1AtGetMaxPageWhenCount3ProductsAtGetAllProductsAndShopName() {
    assertEquals(1, aggregationRepository.getMaxPage("", null, "", TEST_SHOP_NAME));
  }

  @Test
  public void shouldReturn2AtGetMaxPageWhenCount4ProductsAtGetAllProductsAndText() {
    assertEquals(1, aggregationRepository.getMaxPage(TEST_PRODUCT_NAME, null, "", ""));
  }
}
