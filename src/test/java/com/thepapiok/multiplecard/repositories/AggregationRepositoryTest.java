package com.thepapiok.multiplecard.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Blocked;
import com.thepapiok.multiplecard.collections.Category;
import com.thepapiok.multiplecard.collections.Order;
import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.collections.Promotion;
import com.thepapiok.multiplecard.collections.Role;
import com.thepapiok.multiplecard.configs.DbConfig;
import com.thepapiok.multiplecard.dto.ProductGetDTO;
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
  private static final String TEST_PHONE2 = "+48235324342423";
  private static final String TEST_PHONE3 = "+48135304342921";
  private static final String TEST_PHONE4 = "+48121347392923";
  private static final String TEST_PRODUCT_NAME = "product";
  private static final String COUNT_FIELD = "count";
  private static final String DATE_FIELD = "date";
  private static final String PRICE_FIELD = "price";
  private static final String ADDED_FIELD = "added";
  private ProductGetDTO productGetDTO1;
  private ProductGetDTO productGetDTO2;
  private ProductGetDTO productGetDTO3;
  private ProductGetDTO productGetDTO4;
  private ProductGetDTO productGetDTO5;
  private ProductGetDTO productGetDTO7;
  private ProductGetDTO productGetDTO8;
  private ProductGetDTO productGetDTO9;
  private ProductGetDTO productGetDTO10;
  private ProductGetDTO productGetDTO11;
  private ProductGetDTO productGetDTO13;
  private ProductGetDTO productGetDTO14;
  private ProductGetDTO productGetDTO15;
  private ProductGetDTO productGetDTO17;
  @Autowired private MongoTemplate mongoTemplate;
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private ProductRepository productRepository;
  @Autowired private AccountRepository accountRepository;
  @Autowired private PromotionRepository promotionRepository;
  private AggregationRepository aggregationRepository;
  @MockBean private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() {
    final ObjectId testOtherShopId = new ObjectId("223956789012545678901231");
    final ObjectId testOther2ShopId = new ObjectId("123959789092545678971230");
    final ObjectId testOther3ShopId = new ObjectId("110326789012045678901830");
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
    Category category = new Category();
    category.setOwnerId(TEST_OWNER_ID);
    category.setName("category");
    category = mongoTemplate.save(category);
    Product product1 = new Product();
    product1.setImageUrl("url1");
    product1.setName(TEST_PRODUCT_NAME);
    product1.setDescription("description1");
    product1.setAmount(testProduct1Amount);
    product1.setBarcode("barcode1");
    product1.setShopId(TEST_OWNER_ID);
    product1.setCategories(List.of(category.getId()));
    product1.setUpdatedAt(testDate1);
    Product product2 = new Product();
    product2.setImageUrl("url2");
    product2.setName("product2");
    product2.setDescription("description2");
    product2.setAmount(testProduct2Amount);
    product2.setBarcode("barcode2");
    product2.setShopId(TEST_OWNER_ID);
    product2.setCategories(List.of(category.getId()));
    product2.setUpdatedAt(testDate2);
    Product product3 = new Product();
    product3.setImageUrl("url3");
    product3.setName(TEST_PRODUCT_NAME);
    product3.setDescription("description3");
    product3.setAmount(testProduct3Amount);
    product3.setBarcode("barcode3");
    product3.setShopId(TEST_OWNER_ID);
    product3.setCategories(List.of(category.getId()));
    product3.setUpdatedAt(testDate3);
    Product product4 = new Product();
    product4.setImageUrl("url4");
    product4.setName("product4");
    product4.setDescription("description4");
    product4.setAmount(testProduct4Amount);
    product4.setBarcode("barcode4");
    product4.setShopId(testOtherShopId);
    product4.setCategories(List.of(category.getId()));
    product4.setUpdatedAt(testDate4);
    Product product5 = new Product();
    product5.setImageUrl("url45");
    product5.setName(TEST_PRODUCT_NAME);
    product5.setDescription("description5");
    product5.setAmount(testProduct5Amount);
    product5.setBarcode("barcode5");
    product5.setShopId(testOther3ShopId);
    product5.setCategories(List.of(category.getId()));
    product5.setUpdatedAt(testDate5);
    Product product6 = new Product();
    product6.setImageUrl("url6");
    product6.setName(TEST_PRODUCT_NAME);
    product6.setDescription("description6");
    product6.setAmount(testProduct6Amount);
    product6.setBarcode("barcode6");
    product6.setShopId(testOther3ShopId);
    product6.setCategories(List.of(category.getId()));
    product6.setUpdatedAt(testDate6);
    Product product7 = new Product();
    product7.setImageUrl("url7");
    product7.setName("product7");
    product7.setDescription(TEST_PRODUCT_NAME);
    product7.setAmount(testProduct7Amount);
    product7.setBarcode("barcode7");
    product7.setShopId(testOther3ShopId);
    product7.setCategories(List.of(category.getId()));
    product7.setUpdatedAt(testDate7);
    Product product8 = new Product();
    product8.setImageUrl("url8");
    product8.setName("product8");
    product8.setDescription("description8");
    product8.setAmount(testProduct8Amount);
    product8.setBarcode("barcode8");
    product8.setShopId(testOther3ShopId);
    product8.setCategories(List.of(category.getId()));
    product8.setUpdatedAt(testDate8);
    Product product9 = new Product();
    product9.setImageUrl("url9");
    product9.setName("product9");
    product9.setDescription("description9");
    product9.setAmount(testProduct9Amount);
    product9.setBarcode("barcode9");
    product9.setShopId(testOther3ShopId);
    product9.setCategories(List.of(category.getId()));
    product9.setUpdatedAt(testDate9);
    Product product10 = new Product();
    product10.setImageUrl("url10");
    product10.setName("product10");
    product10.setDescription("description10");
    product10.setAmount(testProduct10Amount);
    product10.setBarcode("barcode10");
    product10.setShopId(testOther3ShopId);
    product10.setCategories(List.of(category.getId()));
    product10.setUpdatedAt(testDate10);
    Product product11 = new Product();
    product11.setImageUrl("url11");
    product11.setName("product11");
    product11.setDescription("description11");
    product11.setAmount(testProduct11Amount);
    product11.setBarcode("barcode11");
    product11.setShopId(testOther3ShopId);
    product11.setCategories(List.of(category.getId()));
    product11.setUpdatedAt(testDate11);
    Product product12 = new Product();
    product12.setImageUrl("url12");
    product12.setName("product12");
    product12.setDescription("description12");
    product12.setAmount(testProduct12Amount);
    product12.setBarcode("barcode12");
    product12.setShopId(testOther3ShopId);
    product12.setCategories(List.of(category.getId()));
    product12.setUpdatedAt(testDate12);
    Product product13 = new Product();
    product13.setImageUrl("url13");
    product13.setName("product13");
    product13.setDescription("description13");
    product13.setAmount(testProduct13Amount);
    product13.setBarcode("barcode13");
    product13.setShopId(testOther3ShopId);
    product13.setCategories(List.of(category.getId()));
    product13.setUpdatedAt(testDate13);
    Product product14 = new Product();
    product14.setImageUrl("url14");
    product14.setName("product14");
    product14.setDescription("description14");
    product14.setAmount(testProduct14Amount);
    product14.setBarcode("barcode14");
    product14.setShopId(testOther3ShopId);
    product14.setCategories(List.of(category.getId()));
    product14.setUpdatedAt(testDate14);
    Product product15 = new Product();
    product15.setImageUrl("url15");
    product15.setName("product15");
    product15.setDescription("description15");
    product15.setAmount(testProduct15Amount);
    product15.setBarcode("barcode15");
    product15.setShopId(testOther3ShopId);
    product15.setCategories(List.of(category.getId()));
    product15.setUpdatedAt(testDate15);
    Product product16 = new Product();
    product16.setImageUrl("url16");
    product16.setName("product16");
    product16.setDescription("description16");
    product16.setAmount(testProduct16Amount);
    product16.setBarcode("barcode16");
    product16.setShopId(testOther3ShopId);
    product16.setCategories(List.of(category.getId()));
    product16.setUpdatedAt(testDate16);
    Product product17 = new Product();
    product17.setImageUrl("url17");
    product17.setName(TEST_PRODUCT_NAME);
    product17.setDescription("description17");
    product17.setAmount(testProduct17Amount);
    product17.setBarcode("barcode17");
    product17.setShopId(testOther3ShopId);
    product17.setCategories(List.of(category.getId()));
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
    account1.setId(TEST_OWNER_ID);
    account1.setPhone(TEST_PHONE);
    account1.setEmail("test@test1");
    account1.setRole(Role.ROLE_SHOP);
    account1.setBanned(false);
    account1.setActive(true);
    account1.setPassword("password1");
    mongoTemplate.save(account1);
    Account account2 = new Account();
    account2.setId(testOtherShopId);
    account2.setPhone(TEST_PHONE2);
    account2.setEmail("test@test2");
    account2.setRole(Role.ROLE_SHOP);
    account2.setBanned(false);
    account2.setActive(true);
    account2.setPassword("password2");
    mongoTemplate.save(account2);
    Account account3 = new Account();
    account3.setId(testOther2ShopId);
    account3.setPhone(TEST_PHONE3);
    account3.setEmail("test@test3");
    account3.setRole(Role.ROLE_SHOP);
    account3.setBanned(false);
    account3.setActive(true);
    account3.setPassword("password3");
    mongoTemplate.save(account3);
    Account account4 = new Account();
    account4.setId(testOther3ShopId);
    account4.setPhone(TEST_PHONE4);
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
    order1.setUsed(false);
    order1.setCreatedAt(testDate1);
    mongoTemplate.save(order1);
    Order order2 = new Order();
    order2.setCardId(testCardId);
    order2.setAmount(testOrder2Amount);
    order2.setProductId(product1.getId());
    order2.setUsed(false);
    order2.setCreatedAt(testDate2);
    mongoTemplate.save(order2);
    Order order3 = new Order();
    order3.setCardId(testCardId);
    order3.setAmount(testOrder3Amount);
    order3.setProductId(product1.getId());
    order3.setUsed(false);
    order3.setCreatedAt(testDate3);
    mongoTemplate.save(order3);
    Order order4 = new Order();
    order4.setCardId(testOtherCardId);
    order4.setAmount(testOrder4Amount);
    order4.setProductId(product2.getId());
    order4.setUsed(false);
    order4.setCreatedAt(testDate4);
    mongoTemplate.save(order4);
    Order order5 = new Order();
    order5.setCardId(testOtherCardId);
    order5.setAmount(testOrder4Amount);
    order5.setProductId(product4.getId());
    order5.setUsed(false);
    order5.setCreatedAt(testDate5);
    mongoTemplate.save(order5);
    Order order6 = new Order();
    order6.setCardId(testOtherCardId);
    order6.setAmount(testOrder4Amount);
    order6.setProductId(product4.getId());
    order6.setUsed(false);
    order6.setCreatedAt(testDate5);
    mongoTemplate.save(order6);
    Order order8 = new Order();
    order8.setCardId(testOtherCardId);
    order8.setAmount(testOrder4Amount);
    order8.setProductId(product5.getId());
    order8.setUsed(false);
    order8.setCreatedAt(testDate6);
    mongoTemplate.save(order8);
    Order order9 = new Order();
    order9.setCardId(testOtherCardId);
    order9.setAmount(testOrder4Amount);
    order9.setProductId(product5.getId());
    order9.setUsed(false);
    order9.setCreatedAt(testDate6);
    mongoTemplate.save(order9);
    Order order10 = new Order();
    order10.setCardId(testOtherCardId);
    order10.setAmount(testOrder4Amount);
    order10.setProductId(product5.getId());
    order10.setUsed(false);
    order10.setCreatedAt(testDate6);
    mongoTemplate.save(order10);
    Order order11 = new Order();
    order11.setCardId(testOtherCardId);
    order11.setAmount(testOrder4Amount);
    order11.setProductId(product5.getId());
    order11.setUsed(false);
    order11.setCreatedAt(testDate6);
    mongoTemplate.save(order11);
    Order order12 = new Order();
    order12.setCardId(testOtherCardId);
    order12.setAmount(testOrder4Amount);
    order12.setProductId(product6.getId());
    order12.setUsed(false);
    order12.setCreatedAt(testDate7);
    mongoTemplate.save(order12);
    Order order13 = new Order();
    order13.setCardId(testOtherCardId);
    order13.setAmount(testOrder4Amount);
    order13.setProductId(product6.getId());
    order13.setUsed(false);
    order13.setCreatedAt(testDate7);
    mongoTemplate.save(order13);
    Order order14 = new Order();
    order14.setCardId(testOtherCardId);
    order14.setAmount(testOrder4Amount);
    order14.setProductId(product6.getId());
    order14.setUsed(false);
    order14.setCreatedAt(testDate7);
    mongoTemplate.save(order14);
    Order order15 = new Order();
    order15.setCardId(testOtherCardId);
    order15.setAmount(testOrder4Amount);
    order15.setProductId(product6.getId());
    order15.setUsed(false);
    order15.setCreatedAt(testDate7);
    mongoTemplate.save(order15);
    Order order16 = new Order();
    order16.setCardId(testOtherCardId);
    order16.setAmount(testOrder4Amount);
    order16.setProductId(product6.getId());
    order16.setUsed(false);
    order16.setCreatedAt(testDate7);
    mongoTemplate.save(order16);
    Order order17 = new Order();
    order17.setCardId(testOtherCardId);
    order17.setAmount(testOrder4Amount);
    order17.setProductId(product7.getId());
    order17.setUsed(false);
    order17.setCreatedAt(testDate8);
    mongoTemplate.save(order17);
    Order order18 = new Order();
    order18.setCardId(testOtherCardId);
    order18.setAmount(testOrder4Amount);
    order18.setProductId(product7.getId());
    order18.setUsed(false);
    order18.setCreatedAt(testDate8);
    mongoTemplate.save(order18);
    Order order19 = new Order();
    order19.setCardId(testOtherCardId);
    order19.setAmount(testOrder4Amount);
    order19.setProductId(product7.getId());
    order19.setUsed(false);
    order19.setCreatedAt(testDate8);
    mongoTemplate.save(order19);
    Order order20 = new Order();
    order20.setCardId(testOtherCardId);
    order20.setAmount(testOrder4Amount);
    order20.setProductId(product7.getId());
    order20.setUsed(false);
    order20.setCreatedAt(testDate8);
    mongoTemplate.save(order20);
    Order order21 = new Order();
    order21.setCardId(testOtherCardId);
    order21.setAmount(testOrder4Amount);
    order21.setProductId(product7.getId());
    order21.setUsed(false);
    order21.setCreatedAt(testDate8);
    mongoTemplate.save(order21);
    Order order22 = new Order();
    order22.setCardId(testOtherCardId);
    order22.setAmount(testOrder4Amount);
    order22.setProductId(product7.getId());
    order22.setUsed(false);
    order22.setCreatedAt(testDate8);
    mongoTemplate.save(order22);
    Order order23 = new Order();
    order23.setCardId(testOtherCardId);
    order23.setAmount(testOrder4Amount);
    order23.setProductId(product8.getId());
    order23.setUsed(false);
    order23.setCreatedAt(testDate9);
    mongoTemplate.save(order23);
    Order order24 = new Order();
    order24.setCardId(testOtherCardId);
    order24.setAmount(testOrder4Amount);
    order24.setProductId(product8.getId());
    order24.setUsed(false);
    order24.setCreatedAt(testDate9);
    mongoTemplate.save(order24);
    Order order25 = new Order();
    order25.setCardId(testOtherCardId);
    order25.setAmount(testOrder4Amount);
    order25.setProductId(product8.getId());
    order25.setUsed(false);
    order25.setCreatedAt(testDate9);
    mongoTemplate.save(order25);
    Order order26 = new Order();
    order26.setCardId(testOtherCardId);
    order26.setAmount(testOrder4Amount);
    order26.setProductId(product8.getId());
    order26.setUsed(false);
    order26.setCreatedAt(testDate9);
    mongoTemplate.save(order26);
    Order order27 = new Order();
    order27.setCardId(testOtherCardId);
    order27.setAmount(testOrder4Amount);
    order27.setProductId(product8.getId());
    order27.setUsed(false);
    order27.setCreatedAt(testDate9);
    mongoTemplate.save(order27);
    Order order28 = new Order();
    order28.setCardId(testOtherCardId);
    order28.setAmount(testOrder4Amount);
    order28.setProductId(product8.getId());
    order28.setUsed(false);
    order28.setCreatedAt(testDate9);
    mongoTemplate.save(order28);
    Order order29 = new Order();
    order29.setCardId(testOtherCardId);
    order29.setAmount(testOrder4Amount);
    order29.setProductId(product8.getId());
    order29.setUsed(false);
    order29.setCreatedAt(testDate9);
    mongoTemplate.save(order29);
    Order order30 = new Order();
    order30.setCardId(testOtherCardId);
    order30.setAmount(testOrder4Amount);
    order30.setProductId(product9.getId());
    order30.setUsed(false);
    order30.setCreatedAt(testDate10);
    mongoTemplate.save(order30);
    Order order31 = new Order();
    order31.setCardId(testOtherCardId);
    order31.setAmount(testOrder4Amount);
    order31.setProductId(product9.getId());
    order31.setUsed(false);
    order31.setCreatedAt(testDate10);
    mongoTemplate.save(order31);
    Order order32 = new Order();
    order32.setCardId(testOtherCardId);
    order32.setAmount(testOrder4Amount);
    order32.setProductId(product9.getId());
    order32.setUsed(false);
    order32.setCreatedAt(testDate10);
    mongoTemplate.save(order32);
    Order order33 = new Order();
    order33.setCardId(testOtherCardId);
    order33.setAmount(testOrder4Amount);
    order33.setProductId(product9.getId());
    order33.setUsed(false);
    order33.setCreatedAt(testDate10);
    mongoTemplate.save(order33);
    Order order34 = new Order();
    order34.setCardId(testOtherCardId);
    order34.setAmount(testOrder4Amount);
    order34.setProductId(product9.getId());
    order34.setUsed(false);
    order34.setCreatedAt(testDate10);
    mongoTemplate.save(order34);
    Order order36 = new Order();
    order36.setCardId(testOtherCardId);
    order36.setAmount(testOrder4Amount);
    order36.setProductId(product9.getId());
    order36.setUsed(false);
    order36.setCreatedAt(testDate10);
    mongoTemplate.save(order36);
    Order order37 = new Order();
    order37.setCardId(testOtherCardId);
    order37.setAmount(testOrder4Amount);
    order37.setProductId(product9.getId());
    order37.setUsed(false);
    order37.setCreatedAt(testDate10);
    mongoTemplate.save(order37);
    Order order38 = new Order();
    order38.setCardId(testOtherCardId);
    order38.setAmount(testOrder4Amount);
    order38.setProductId(product9.getId());
    order38.setUsed(false);
    order38.setCreatedAt(testDate10);
    mongoTemplate.save(order38);
    Order order39 = new Order();
    order39.setCardId(testOtherCardId);
    order39.setAmount(testOrder4Amount);
    order39.setProductId(product10.getId());
    order39.setUsed(false);
    order39.setCreatedAt(testDate11);
    mongoTemplate.save(order39);
    Order order40 = new Order();
    order40.setCardId(testOtherCardId);
    order40.setAmount(testOrder4Amount);
    order40.setProductId(product10.getId());
    order40.setUsed(false);
    order40.setCreatedAt(testDate11);
    mongoTemplate.save(order40);
    Order order41 = new Order();
    order41.setCardId(testOtherCardId);
    order41.setAmount(testOrder4Amount);
    order41.setProductId(product10.getId());
    order41.setUsed(false);
    order41.setCreatedAt(testDate11);
    mongoTemplate.save(order41);
    Order order42 = new Order();
    order42.setCardId(testOtherCardId);
    order42.setAmount(testOrder4Amount);
    order42.setProductId(product10.getId());
    order42.setUsed(false);
    order42.setCreatedAt(testDate11);
    mongoTemplate.save(order42);
    Order order43 = new Order();
    order43.setCardId(testOtherCardId);
    order43.setAmount(testOrder4Amount);
    order43.setProductId(product10.getId());
    order43.setUsed(false);
    order43.setCreatedAt(testDate11);
    mongoTemplate.save(order43);
    Order order44 = new Order();
    order44.setCardId(testOtherCardId);
    order44.setAmount(testOrder4Amount);
    order44.setProductId(product10.getId());
    order44.setUsed(false);
    order44.setCreatedAt(testDate11);
    mongoTemplate.save(order44);
    Order order45 = new Order();
    order45.setCardId(testOtherCardId);
    order45.setAmount(testOrder4Amount);
    order45.setProductId(product10.getId());
    order45.setUsed(false);
    order45.setCreatedAt(testDate11);
    mongoTemplate.save(order45);
    Order order46 = new Order();
    order46.setCardId(testOtherCardId);
    order46.setAmount(testOrder4Amount);
    order46.setProductId(product10.getId());
    order46.setUsed(false);
    order46.setCreatedAt(testDate11);
    mongoTemplate.save(order46);
    Order order47 = new Order();
    order47.setCardId(testOtherCardId);
    order47.setAmount(testOrder4Amount);
    order47.setProductId(product10.getId());
    order47.setUsed(false);
    order47.setCreatedAt(testDate11);
    mongoTemplate.save(order47);
    Order order48 = new Order();
    order48.setCardId(testOtherCardId);
    order48.setAmount(testOrder4Amount);
    order48.setProductId(product11.getId());
    order48.setUsed(false);
    order48.setCreatedAt(testDate12);
    mongoTemplate.save(order48);
    Order order49 = new Order();
    order49.setCardId(testOtherCardId);
    order49.setAmount(testOrder4Amount);
    order49.setProductId(product11.getId());
    order49.setUsed(false);
    order49.setCreatedAt(testDate12);
    mongoTemplate.save(order49);
    Order order50 = new Order();
    order50.setCardId(testOtherCardId);
    order50.setAmount(testOrder4Amount);
    order50.setProductId(product11.getId());
    order50.setUsed(false);
    order50.setCreatedAt(testDate12);
    mongoTemplate.save(order50);
    Order order51 = new Order();
    order51.setCardId(testOtherCardId);
    order51.setAmount(testOrder4Amount);
    order51.setProductId(product11.getId());
    order51.setUsed(false);
    order51.setCreatedAt(testDate12);
    mongoTemplate.save(order51);
    Order order52 = new Order();
    order52.setCardId(testOtherCardId);
    order52.setAmount(testOrder4Amount);
    order52.setProductId(product11.getId());
    order52.setUsed(false);
    order52.setCreatedAt(testDate12);
    mongoTemplate.save(order52);
    Order order53 = new Order();
    order53.setCardId(testOtherCardId);
    order53.setAmount(testOrder4Amount);
    order53.setProductId(product11.getId());
    order53.setUsed(false);
    order53.setCreatedAt(testDate12);
    mongoTemplate.save(order53);
    Order order54 = new Order();
    order54.setCardId(testOtherCardId);
    order54.setAmount(testOrder4Amount);
    order54.setProductId(product11.getId());
    order54.setUsed(false);
    order54.setCreatedAt(testDate12);
    mongoTemplate.save(order54);
    Order order55 = new Order();
    order55.setCardId(testOtherCardId);
    order55.setAmount(testOrder4Amount);
    order55.setProductId(product11.getId());
    order55.setUsed(false);
    order55.setCreatedAt(testDate12);
    mongoTemplate.save(order55);
    Order order56 = new Order();
    order56.setCardId(testOtherCardId);
    order56.setAmount(testOrder4Amount);
    order56.setProductId(product11.getId());
    order56.setUsed(false);
    order56.setCreatedAt(testDate12);
    mongoTemplate.save(order56);
    Order order57 = new Order();
    order57.setCardId(testOtherCardId);
    order57.setAmount(testOrder4Amount);
    order57.setProductId(product11.getId());
    order57.setUsed(false);
    order57.setCreatedAt(testDate12);
    mongoTemplate.save(order57);
    Order order58 = new Order();
    order58.setCardId(testOtherCardId);
    order58.setAmount(testOrder4Amount);
    order58.setProductId(product12.getId());
    order58.setUsed(false);
    order58.setCreatedAt(testDate13);
    mongoTemplate.save(order58);
    Order order59 = new Order();
    order59.setCardId(testOtherCardId);
    order59.setAmount(testOrder4Amount);
    order59.setProductId(product12.getId());
    order59.setUsed(false);
    order59.setCreatedAt(testDate13);
    mongoTemplate.save(order59);
    Order order60 = new Order();
    order60.setCardId(testOtherCardId);
    order60.setAmount(testOrder4Amount);
    order60.setProductId(product12.getId());
    order60.setUsed(false);
    order60.setCreatedAt(testDate13);
    mongoTemplate.save(order60);
    Order order61 = new Order();
    order61.setCardId(testOtherCardId);
    order61.setAmount(testOrder4Amount);
    order61.setProductId(product12.getId());
    order61.setUsed(false);
    order61.setCreatedAt(testDate13);
    mongoTemplate.save(order61);
    Order order62 = new Order();
    order62.setCardId(testOtherCardId);
    order62.setAmount(testOrder4Amount);
    order62.setProductId(product12.getId());
    order62.setUsed(false);
    order62.setCreatedAt(testDate13);
    mongoTemplate.save(order62);
    Order order63 = new Order();
    order63.setCardId(testOtherCardId);
    order63.setAmount(testOrder4Amount);
    order63.setProductId(product12.getId());
    order63.setUsed(false);
    order63.setCreatedAt(testDate13);
    mongoTemplate.save(order63);
    Order order64 = new Order();
    order64.setCardId(testOtherCardId);
    order64.setAmount(testOrder4Amount);
    order64.setProductId(product12.getId());
    order64.setUsed(false);
    order64.setCreatedAt(testDate13);
    mongoTemplate.save(order64);
    Order order65 = new Order();
    order65.setCardId(testOtherCardId);
    order65.setAmount(testOrder4Amount);
    order65.setProductId(product12.getId());
    order65.setUsed(false);
    order65.setCreatedAt(testDate13);
    mongoTemplate.save(order65);
    Order order66 = new Order();
    order66.setCardId(testOtherCardId);
    order66.setAmount(testOrder4Amount);
    order66.setProductId(product12.getId());
    order66.setUsed(false);
    order66.setCreatedAt(testDate13);
    mongoTemplate.save(order66);
    Order order67 = new Order();
    order67.setCardId(testOtherCardId);
    order67.setAmount(testOrder4Amount);
    order67.setProductId(product12.getId());
    order67.setUsed(false);
    order67.setCreatedAt(testDate13);
    mongoTemplate.save(order67);
    Order order68 = new Order();
    order68.setCardId(testOtherCardId);
    order68.setAmount(testOrder4Amount);
    order68.setProductId(product12.getId());
    order68.setUsed(false);
    order68.setCreatedAt(testDate13);
    mongoTemplate.save(order68);
    Order order69 = new Order();
    order69.setCardId(testOtherCardId);
    order69.setAmount(testOrder4Amount);
    order69.setProductId(product13.getId());
    order69.setUsed(false);
    order69.setCreatedAt(testDate14);
    mongoTemplate.save(order69);
    Order order70 = new Order();
    order70.setCardId(testOtherCardId);
    order70.setAmount(testOrder4Amount);
    order70.setProductId(product13.getId());
    order70.setUsed(false);
    order70.setCreatedAt(testDate14);
    mongoTemplate.save(order70);
    Order order71 = new Order();
    order71.setCardId(testOtherCardId);
    order71.setAmount(testOrder4Amount);
    order71.setProductId(product13.getId());
    order71.setUsed(false);
    order71.setCreatedAt(testDate14);
    mongoTemplate.save(order71);
    Order order72 = new Order();
    order72.setCardId(testOtherCardId);
    order72.setAmount(testOrder4Amount);
    order72.setProductId(product13.getId());
    order72.setUsed(false);
    order72.setCreatedAt(testDate14);
    mongoTemplate.save(order72);
    Order order73 = new Order();
    order73.setCardId(testOtherCardId);
    order73.setAmount(testOrder4Amount);
    order73.setProductId(product13.getId());
    order73.setUsed(false);
    order73.setCreatedAt(testDate14);
    mongoTemplate.save(order73);
    Order order74 = new Order();
    order74.setCardId(testOtherCardId);
    order74.setAmount(testOrder4Amount);
    order74.setProductId(product13.getId());
    order74.setUsed(false);
    order74.setCreatedAt(testDate14);
    mongoTemplate.save(order74);
    Order order75 = new Order();
    order75.setCardId(testOtherCardId);
    order75.setAmount(testOrder4Amount);
    order75.setProductId(product13.getId());
    order75.setUsed(false);
    order75.setCreatedAt(testDate14);
    mongoTemplate.save(order75);
    Order order76 = new Order();
    order76.setCardId(testOtherCardId);
    order76.setAmount(testOrder4Amount);
    order76.setProductId(product13.getId());
    order76.setUsed(false);
    order76.setCreatedAt(testDate14);
    mongoTemplate.save(order76);
    Order order77 = new Order();
    order77.setCardId(testOtherCardId);
    order77.setAmount(testOrder4Amount);
    order77.setProductId(product13.getId());
    order77.setUsed(false);
    order77.setCreatedAt(testDate14);
    mongoTemplate.save(order77);
    Order order78 = new Order();
    order78.setCardId(testOtherCardId);
    order78.setAmount(testOrder4Amount);
    order78.setProductId(product13.getId());
    order78.setUsed(false);
    order78.setCreatedAt(testDate14);
    mongoTemplate.save(order78);
    Order order79 = new Order();
    order79.setCardId(testOtherCardId);
    order79.setAmount(testOrder4Amount);
    order79.setProductId(product13.getId());
    order79.setUsed(false);
    order79.setCreatedAt(testDate14);
    mongoTemplate.save(order79);
    Order order80 = new Order();
    order80.setCardId(testOtherCardId);
    order80.setAmount(testOrder4Amount);
    order80.setProductId(product13.getId());
    order80.setUsed(false);
    order80.setCreatedAt(testDate14);
    mongoTemplate.save(order80);
    Order order81 = new Order();
    order81.setCardId(testOtherCardId);
    order81.setAmount(testOrder4Amount);
    order81.setProductId(product14.getId());
    order81.setUsed(false);
    order81.setCreatedAt(testDate15);
    mongoTemplate.save(order81);
    Order order82 = new Order();
    order82.setCardId(testOtherCardId);
    order82.setAmount(testOrder4Amount);
    order82.setProductId(product14.getId());
    order82.setUsed(false);
    order82.setCreatedAt(testDate15);
    mongoTemplate.save(order82);
    Order order83 = new Order();
    order83.setCardId(testOtherCardId);
    order83.setAmount(testOrder4Amount);
    order83.setProductId(product14.getId());
    order83.setUsed(false);
    order83.setCreatedAt(testDate15);
    mongoTemplate.save(order83);
    Order order84 = new Order();
    order84.setCardId(testOtherCardId);
    order84.setAmount(testOrder4Amount);
    order84.setProductId(product14.getId());
    order84.setUsed(false);
    order84.setCreatedAt(testDate15);
    mongoTemplate.save(order84);
    Order order85 = new Order();
    order85.setCardId(testOtherCardId);
    order85.setAmount(testOrder4Amount);
    order85.setProductId(product14.getId());
    order85.setUsed(false);
    order85.setCreatedAt(testDate15);
    mongoTemplate.save(order85);
    Order order86 = new Order();
    order86.setCardId(testOtherCardId);
    order86.setAmount(testOrder4Amount);
    order86.setProductId(product14.getId());
    order86.setUsed(false);
    order86.setCreatedAt(testDate15);
    mongoTemplate.save(order86);
    Order order87 = new Order();
    order87.setCardId(testOtherCardId);
    order87.setAmount(testOrder4Amount);
    order87.setProductId(product14.getId());
    order87.setUsed(false);
    order87.setCreatedAt(testDate15);
    mongoTemplate.save(order87);
    Order order88 = new Order();
    order88.setCardId(testOtherCardId);
    order88.setAmount(testOrder4Amount);
    order88.setProductId(product14.getId());
    order88.setUsed(false);
    order88.setCreatedAt(testDate15);
    mongoTemplate.save(order88);
    Order order89 = new Order();
    order89.setCardId(testOtherCardId);
    order89.setAmount(testOrder4Amount);
    order89.setProductId(product14.getId());
    order89.setUsed(false);
    order89.setCreatedAt(testDate15);
    mongoTemplate.save(order89);
    Order order90 = new Order();
    order90.setCardId(testOtherCardId);
    order90.setAmount(testOrder4Amount);
    order90.setProductId(product14.getId());
    order90.setUsed(false);
    order90.setCreatedAt(testDate15);
    mongoTemplate.save(order90);
    Order order91 = new Order();
    order91.setCardId(testOtherCardId);
    order91.setAmount(testOrder4Amount);
    order91.setProductId(product14.getId());
    order91.setUsed(false);
    order91.setCreatedAt(testDate15);
    mongoTemplate.save(order91);
    Order order92 = new Order();
    order92.setCardId(testOtherCardId);
    order92.setAmount(testOrder4Amount);
    order92.setProductId(product14.getId());
    order92.setUsed(false);
    order92.setCreatedAt(testDate15);
    mongoTemplate.save(order92);
    Order order93 = new Order();
    order93.setCardId(testOtherCardId);
    order93.setAmount(testOrder4Amount);
    order93.setProductId(product14.getId());
    order93.setUsed(false);
    order93.setCreatedAt(testDate15);
    mongoTemplate.save(order93);
    Order order94 = new Order();
    order94.setCardId(testOtherCardId);
    order94.setAmount(testOrder4Amount);
    order94.setProductId(product15.getId());
    order94.setUsed(false);
    order94.setCreatedAt(testDate16);
    mongoTemplate.save(order94);
    Order order95 = new Order();
    order95.setCardId(testOtherCardId);
    order95.setAmount(testOrder4Amount);
    order95.setProductId(product15.getId());
    order95.setUsed(false);
    order95.setCreatedAt(testDate16);
    mongoTemplate.save(order95);
    Order order96 = new Order();
    order96.setCardId(testOtherCardId);
    order96.setAmount(testOrder4Amount);
    order96.setProductId(product15.getId());
    order96.setUsed(false);
    order96.setCreatedAt(testDate16);
    mongoTemplate.save(order96);
    Order order97 = new Order();
    order97.setCardId(testOtherCardId);
    order97.setAmount(testOrder4Amount);
    order97.setProductId(product15.getId());
    order97.setUsed(false);
    order97.setCreatedAt(testDate16);
    mongoTemplate.save(order97);
    Order order98 = new Order();
    order98.setCardId(testOtherCardId);
    order98.setAmount(testOrder4Amount);
    order98.setProductId(product15.getId());
    order98.setUsed(false);
    order98.setCreatedAt(testDate16);
    mongoTemplate.save(order98);
    Order order99 = new Order();
    order99.setCardId(testOtherCardId);
    order99.setAmount(testOrder4Amount);
    order99.setProductId(product15.getId());
    order99.setUsed(false);
    order99.setCreatedAt(testDate16);
    mongoTemplate.save(order99);
    Order order100 = new Order();
    order100.setCardId(testOtherCardId);
    order100.setAmount(testOrder4Amount);
    order100.setProductId(product15.getId());
    order100.setUsed(false);
    order100.setCreatedAt(testDate16);
    mongoTemplate.save(order100);
    Order order101 = new Order();
    order101.setCardId(testOtherCardId);
    order101.setAmount(testOrder4Amount);
    order101.setProductId(product15.getId());
    order101.setUsed(false);
    order101.setCreatedAt(testDate16);
    mongoTemplate.save(order101);
    Order order102 = new Order();
    order102.setCardId(testOtherCardId);
    order102.setAmount(testOrder4Amount);
    order102.setProductId(product15.getId());
    order102.setUsed(false);
    order102.setCreatedAt(testDate16);
    mongoTemplate.save(order102);
    Order order103 = new Order();
    order103.setCardId(testOtherCardId);
    order103.setAmount(testOrder4Amount);
    order103.setProductId(product15.getId());
    order103.setUsed(false);
    order103.setCreatedAt(testDate16);
    mongoTemplate.save(order103);
    Order order104 = new Order();
    order104.setCardId(testOtherCardId);
    order104.setAmount(testOrder4Amount);
    order104.setProductId(product15.getId());
    order104.setUsed(false);
    order104.setCreatedAt(testDate16);
    mongoTemplate.save(order104);
    Order order105 = new Order();
    order105.setCardId(testOtherCardId);
    order105.setAmount(testOrder4Amount);
    order105.setProductId(product15.getId());
    order105.setUsed(false);
    order105.setCreatedAt(testDate16);
    mongoTemplate.save(order105);
    Order order106 = new Order();
    order106.setCardId(testOtherCardId);
    order106.setAmount(testOrder4Amount);
    order106.setProductId(product15.getId());
    order106.setUsed(false);
    order106.setCreatedAt(testDate16);
    mongoTemplate.save(order106);
    Order order107 = new Order();
    order107.setCardId(testOtherCardId);
    order107.setAmount(testOrder4Amount);
    order107.setProductId(product15.getId());
    order107.setUsed(false);
    order107.setCreatedAt(testDate16);
    mongoTemplate.save(order107);
    Order order108 = new Order();
    order108.setCardId(testOtherCardId);
    order108.setAmount(testOrder4Amount);
    order108.setProductId(product16.getId());
    order108.setUsed(false);
    order108.setCreatedAt(testDate17);
    mongoTemplate.save(order108);
    Order order109 = new Order();
    order109.setCardId(testOtherCardId);
    order109.setAmount(testOrder4Amount);
    order109.setProductId(product16.getId());
    order109.setUsed(false);
    order109.setCreatedAt(testDate17);
    mongoTemplate.save(order109);
    Order order110 = new Order();
    order110.setCardId(testOtherCardId);
    order110.setAmount(testOrder4Amount);
    order110.setProductId(product16.getId());
    order110.setUsed(false);
    order110.setCreatedAt(testDate17);
    mongoTemplate.save(order110);
    Order order111 = new Order();
    order111.setCardId(testOtherCardId);
    order111.setAmount(testOrder4Amount);
    order111.setProductId(product16.getId());
    order111.setUsed(false);
    order111.setCreatedAt(testDate17);
    mongoTemplate.save(order111);
    Order order112 = new Order();
    order112.setCardId(testOtherCardId);
    order112.setAmount(testOrder4Amount);
    order112.setProductId(product16.getId());
    order112.setUsed(false);
    order112.setCreatedAt(testDate17);
    mongoTemplate.save(order112);
    Order order113 = new Order();
    order113.setCardId(testOtherCardId);
    order113.setAmount(testOrder4Amount);
    order113.setProductId(product16.getId());
    order113.setUsed(false);
    order113.setCreatedAt(testDate17);
    mongoTemplate.save(order113);
    Order order114 = new Order();
    order114.setCardId(testOtherCardId);
    order114.setAmount(testOrder4Amount);
    order114.setProductId(product16.getId());
    order114.setUsed(false);
    order114.setCreatedAt(testDate17);
    mongoTemplate.save(order114);
    Order order115 = new Order();
    order115.setCardId(testOtherCardId);
    order115.setAmount(testOrder4Amount);
    order115.setProductId(product16.getId());
    order115.setUsed(false);
    order115.setCreatedAt(testDate17);
    mongoTemplate.save(order115);
    Order order116 = new Order();
    order116.setCardId(testOtherCardId);
    order116.setAmount(testOrder4Amount);
    order116.setProductId(product16.getId());
    order116.setUsed(false);
    order116.setCreatedAt(testDate17);
    mongoTemplate.save(order116);
    Order order117 = new Order();
    order117.setCardId(testOtherCardId);
    order117.setAmount(testOrder4Amount);
    order117.setProductId(product16.getId());
    order117.setUsed(false);
    order117.setCreatedAt(testDate17);
    mongoTemplate.save(order117);
    Order order118 = new Order();
    order118.setCardId(testOtherCardId);
    order118.setAmount(testOrder4Amount);
    order118.setProductId(product16.getId());
    order118.setUsed(false);
    order118.setCreatedAt(testDate17);
    mongoTemplate.save(order118);
    Order order119 = new Order();
    order119.setCardId(testOtherCardId);
    order119.setAmount(testOrder4Amount);
    order119.setProductId(product16.getId());
    order119.setUsed(false);
    order119.setCreatedAt(testDate17);
    mongoTemplate.save(order119);
    Order order120 = new Order();
    order120.setCardId(testOtherCardId);
    order120.setAmount(testOrder4Amount);
    order120.setProductId(product16.getId());
    order120.setUsed(false);
    order120.setCreatedAt(testDate17);
    mongoTemplate.save(order120);
    Order order121 = new Order();
    order121.setCardId(testOtherCardId);
    order121.setAmount(testOrder4Amount);
    order121.setProductId(product16.getId());
    order121.setUsed(false);
    order121.setCreatedAt(testDate17);
    mongoTemplate.save(order121);
    Order order122 = new Order();
    order122.setCardId(testOtherCardId);
    order122.setAmount(testOrder4Amount);
    order122.setProductId(product16.getId());
    order122.setUsed(false);
    order122.setCreatedAt(testDate17);
    mongoTemplate.save(order122);
    Order order123 = new Order();
    order123.setCardId(testOtherCardId);
    order123.setAmount(testOrder4Amount);
    order123.setProductId(product17.getId());
    order123.setUsed(false);
    order123.setCreatedAt(testDate18);
    mongoTemplate.save(order123);
    Order order124 = new Order();
    order124.setCardId(testOtherCardId);
    order124.setAmount(testOrder4Amount);
    order124.setProductId(product17.getId());
    order124.setUsed(false);
    order124.setCreatedAt(testDate18);
    mongoTemplate.save(order124);
    Order order125 = new Order();
    order125.setCardId(testOtherCardId);
    order125.setAmount(testOrder4Amount);
    order125.setProductId(product17.getId());
    order125.setUsed(false);
    order125.setCreatedAt(testDate18);
    mongoTemplate.save(order125);
    Order order126 = new Order();
    order126.setCardId(testOtherCardId);
    order126.setAmount(testOrder4Amount);
    order126.setProductId(product17.getId());
    order126.setUsed(false);
    order126.setCreatedAt(testDate18);
    mongoTemplate.save(order126);
    Order order127 = new Order();
    order127.setCardId(testOtherCardId);
    order127.setAmount(testOrder4Amount);
    order127.setProductId(product17.getId());
    order127.setUsed(false);
    order127.setCreatedAt(testDate18);
    mongoTemplate.save(order127);
    Order order128 = new Order();
    order128.setCardId(testOtherCardId);
    order128.setAmount(testOrder4Amount);
    order128.setProductId(product17.getId());
    order128.setUsed(false);
    order128.setCreatedAt(testDate18);
    mongoTemplate.save(order128);
    Order order129 = new Order();
    order129.setCardId(testOtherCardId);
    order129.setAmount(testOrder4Amount);
    order129.setProductId(product17.getId());
    order129.setUsed(false);
    order129.setCreatedAt(testDate18);
    mongoTemplate.save(order129);
    Order order130 = new Order();
    order130.setCardId(testOtherCardId);
    order130.setAmount(testOrder4Amount);
    order130.setProductId(product17.getId());
    order130.setUsed(false);
    order130.setCreatedAt(testDate18);
    mongoTemplate.save(order130);
    Order order131 = new Order();
    order131.setCardId(testOtherCardId);
    order131.setAmount(testOrder4Amount);
    order131.setProductId(product17.getId());
    order131.setUsed(false);
    order131.setCreatedAt(testDate18);
    mongoTemplate.save(order131);
    Order order132 = new Order();
    order132.setCardId(testOtherCardId);
    order132.setAmount(testOrder4Amount);
    order132.setProductId(product17.getId());
    order132.setUsed(false);
    order132.setCreatedAt(testDate18);
    mongoTemplate.save(order132);
    Order order133 = new Order();
    order133.setCardId(testOtherCardId);
    order133.setAmount(testOrder4Amount);
    order133.setProductId(product17.getId());
    order133.setUsed(false);
    order133.setCreatedAt(testDate18);
    mongoTemplate.save(order133);
    Order order134 = new Order();
    order134.setCardId(testOtherCardId);
    order134.setAmount(testOrder4Amount);
    order134.setProductId(product17.getId());
    order134.setUsed(false);
    order134.setCreatedAt(testDate18);
    mongoTemplate.save(order134);
    Order order135 = new Order();
    order135.setCardId(testOtherCardId);
    order135.setAmount(testOrder4Amount);
    order135.setProductId(product17.getId());
    order135.setUsed(false);
    order135.setCreatedAt(testDate18);
    mongoTemplate.save(order135);
    Order order136 = new Order();
    order136.setCardId(testOtherCardId);
    order136.setAmount(testOrder4Amount);
    order136.setProductId(product17.getId());
    order136.setUsed(false);
    order136.setCreatedAt(testDate18);
    mongoTemplate.save(order136);
    Order order137 = new Order();
    order137.setCardId(testOtherCardId);
    order137.setAmount(testOrder4Amount);
    order137.setProductId(product17.getId());
    order137.setUsed(false);
    order137.setCreatedAt(testDate18);
    mongoTemplate.save(order137);
    Order order138 = new Order();
    order138.setCardId(testOtherCardId);
    order138.setAmount(testOrder4Amount);
    order138.setProductId(product17.getId());
    order138.setUsed(false);
    order138.setCreatedAt(testDate18);
    mongoTemplate.save(order138);
    Blocked blocked1 = new Blocked();
    blocked1.setExpiredAt(LocalDate.now().plusYears(1));
    blocked1.setProductId(product3.getId());
    blocked1 = mongoTemplate.save(blocked1);
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
    productGetDTO1 = new ProductGetDTO();
    productGetDTO1.setProduct(product1);
    productGetDTO1.setPromotion(promotion1);
    productGetDTO1.setBlocked(null);
    productGetDTO2 = new ProductGetDTO();
    productGetDTO2.setProduct(product2);
    productGetDTO2.setPromotion(promotion2);
    productGetDTO2.setBlocked(null);
    productGetDTO3 = new ProductGetDTO();
    productGetDTO3.setProduct(product3);
    productGetDTO3.setPromotion(null);
    productGetDTO3.setBlocked(blocked1);
    productGetDTO4 = new ProductGetDTO();
    productGetDTO4.setProduct(product4);
    productGetDTO4.setPromotion(promotion3);
    productGetDTO4.setBlocked(null);
    productGetDTO5 = new ProductGetDTO();
    productGetDTO5.setProduct(product5);
    productGetDTO5.setPromotion(null);
    productGetDTO5.setBlocked(null);
    productGetDTO7 = new ProductGetDTO();
    productGetDTO7.setProduct(product7);
    productGetDTO7.setPromotion(null);
    productGetDTO7.setBlocked(null);
    productGetDTO8 = new ProductGetDTO();
    productGetDTO8.setProduct(product8);
    productGetDTO8.setPromotion(null);
    productGetDTO8.setBlocked(null);
    productGetDTO9 = new ProductGetDTO();
    productGetDTO9.setProduct(product9);
    productGetDTO9.setPromotion(null);
    productGetDTO9.setBlocked(null);
    productGetDTO10 = new ProductGetDTO();
    productGetDTO10.setProduct(product10);
    productGetDTO10.setPromotion(null);
    productGetDTO10.setBlocked(null);
    productGetDTO11 = new ProductGetDTO();
    productGetDTO11.setProduct(product11);
    productGetDTO11.setPromotion(null);
    productGetDTO11.setBlocked(null);
    productGetDTO13 = new ProductGetDTO();
    productGetDTO13.setProduct(product13);
    productGetDTO13.setPromotion(null);
    productGetDTO13.setBlocked(null);
    productGetDTO14 = new ProductGetDTO();
    productGetDTO14.setProduct(product14);
    productGetDTO14.setPromotion(null);
    productGetDTO14.setBlocked(null);
    productGetDTO15 = new ProductGetDTO();
    productGetDTO15.setProduct(product15);
    productGetDTO15.setPromotion(null);
    productGetDTO15.setBlocked(null);
    productGetDTO17 = new ProductGetDTO();
    productGetDTO17.setProduct(product17);
    productGetDTO17.setPromotion(null);
    productGetDTO17.setBlocked(null);
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
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsOwnerWhenCountFieldAndIsDescendingWithoutText() {
    assertEquals(
        List.of(productGetDTO1, productGetDTO2, productGetDTO3),
        aggregationRepository.getProducts(TEST_PHONE, 0, COUNT_FIELD, true, ""));
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsOwnerWhenCountFieldAndIsAscendingWithoutText() {
    assertEquals(
        List.of(productGetDTO3, productGetDTO2, productGetDTO1),
        aggregationRepository.getProducts(TEST_PHONE, 0, COUNT_FIELD, false, ""));
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsOwnerWhenDateFieldAndIsDescendingWithoutText() {
    assertEquals(
        List.of(productGetDTO2, productGetDTO1, productGetDTO3),
        aggregationRepository.getProducts(TEST_PHONE, 0, DATE_FIELD, true, ""));
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsOwnerWhenDateFieldAndIsAscendingWithoutText() {
    assertEquals(
        List.of(productGetDTO3, productGetDTO1, productGetDTO2),
        aggregationRepository.getProducts(TEST_PHONE, 0, DATE_FIELD, false, ""));
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsOwnerWhenPriceFieldAndIsDescendingWithoutText() {
    assertEquals(
        List.of(productGetDTO1, productGetDTO3, productGetDTO2),
        aggregationRepository.getProducts(TEST_PHONE, 0, PRICE_FIELD, true, ""));
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsOwnerWhenPriceFieldAndIsAscendingWithoutText() {
    assertEquals(
        List.of(productGetDTO2, productGetDTO3, productGetDTO1),
        aggregationRepository.getProducts(TEST_PHONE, 0, PRICE_FIELD, false, ""));
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsOwnerWhenAddedFieldAndIsDescendingWithoutText() {
    assertEquals(
        List.of(productGetDTO3, productGetDTO2, productGetDTO1),
        aggregationRepository.getProducts(TEST_PHONE, 0, ADDED_FIELD, true, ""));
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsOwnerWhenAddedFieldAndIsAscendingWithoutText() {
    assertEquals(
        List.of(productGetDTO1, productGetDTO2, productGetDTO3),
        aggregationRepository.getProducts(TEST_PHONE, 0, ADDED_FIELD, false, ""));
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsOwnerWhenCountFieldAndIsDescendingWithText() {
    assertEquals(
        List.of(productGetDTO1, productGetDTO3),
        aggregationRepository.getProducts(TEST_PHONE, 0, COUNT_FIELD, true, TEST_PRODUCT_NAME));
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsWhenCountFieldAndIsDescendingWithoutText() {
    assertEquals(
        List.of(
            productGetDTO17,
            productGetDTO15,
            productGetDTO14,
            productGetDTO13,
            productGetDTO11,
            productGetDTO10,
            productGetDTO9,
            productGetDTO8,
            productGetDTO7,
            productGetDTO5,
            productGetDTO1,
            productGetDTO4),
        aggregationRepository.getProducts(null, 0, COUNT_FIELD, true, ""));
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsWhenCountFieldAndIsAscendingWithoutText() {
    assertEquals(
        List.of(
            productGetDTO2,
            productGetDTO4,
            productGetDTO1,
            productGetDTO5,
            productGetDTO7,
            productGetDTO8,
            productGetDTO9,
            productGetDTO10,
            productGetDTO11,
            productGetDTO13,
            productGetDTO14,
            productGetDTO15),
        aggregationRepository.getProducts(null, 0, COUNT_FIELD, false, ""));
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsWhenDateFieldAndIsDescendingWithoutText() {
    assertEquals(
        List.of(
            productGetDTO13,
            productGetDTO14,
            productGetDTO15,
            productGetDTO10,
            productGetDTO11,
            productGetDTO9,
            productGetDTO8,
            productGetDTO7,
            productGetDTO5,
            productGetDTO4,
            productGetDTO2,
            productGetDTO1),
        aggregationRepository.getProducts(null, 0, DATE_FIELD, true, ""));
  }

  @Test
  public void shouldReturnListOfProductGetDTOAtGetProductsWhenDateFieldAndIsAscendingWithoutText() {
    assertEquals(
        List.of(
            productGetDTO17,
            productGetDTO1,
            productGetDTO2,
            productGetDTO4,
            productGetDTO5,
            productGetDTO7,
            productGetDTO8,
            productGetDTO9,
            productGetDTO11,
            productGetDTO10,
            productGetDTO15,
            productGetDTO14),
        aggregationRepository.getProducts(null, 0, DATE_FIELD, false, ""));
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsWhenPriceFieldAndIsDescendingWithoutText() {
    assertEquals(
        List.of(
            productGetDTO1,
            productGetDTO2,
            productGetDTO14,
            productGetDTO10,
            productGetDTO17,
            productGetDTO15,
            productGetDTO13,
            productGetDTO11,
            productGetDTO9,
            productGetDTO8,
            productGetDTO7,
            productGetDTO5),
        aggregationRepository.getProducts(null, 0, PRICE_FIELD, true, ""));
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsWhenPriceFieldAndIsAscendingWithoutText() {
    assertEquals(
        List.of(
            productGetDTO4,
            productGetDTO5,
            productGetDTO7,
            productGetDTO8,
            productGetDTO9,
            productGetDTO11,
            productGetDTO13,
            productGetDTO15,
            productGetDTO17,
            productGetDTO10,
            productGetDTO14,
            productGetDTO2),
        aggregationRepository.getProducts(null, 0, PRICE_FIELD, false, ""));
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsWhenAddedFieldAndIsDescendingWithoutText() {
    assertEquals(
        List.of(
            productGetDTO17,
            productGetDTO14,
            productGetDTO15,
            productGetDTO11,
            productGetDTO13,
            productGetDTO10,
            productGetDTO9,
            productGetDTO8,
            productGetDTO7,
            productGetDTO5,
            productGetDTO4,
            productGetDTO2),
        aggregationRepository.getProducts(null, 0, ADDED_FIELD, true, ""));
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsWhenAddedFieldAndIsAscendingWithoutText() {
    assertEquals(
        List.of(
            productGetDTO1,
            productGetDTO2,
            productGetDTO4,
            productGetDTO5,
            productGetDTO7,
            productGetDTO8,
            productGetDTO9,
            productGetDTO10,
            productGetDTO13,
            productGetDTO11,
            productGetDTO15,
            productGetDTO14),
        aggregationRepository.getProducts(null, 0, ADDED_FIELD, false, ""));
  }

  @Test
  public void shouldReturnListOfProductGetDTOAtGetProductsWhenCountFieldAndIsDescendingWithText() {
    assertEquals(
        List.of(productGetDTO17, productGetDTO7, productGetDTO5, productGetDTO1),
        aggregationRepository.getProducts(null, 0, COUNT_FIELD, true, TEST_PRODUCT_NAME));
  }

  @Test
  public void shouldReturn1AtGetMaxPageWhenCount1Product() {
    assertEquals(1, aggregationRepository.getMaxPage("", TEST_PHONE2));
  }

  @Test
  public void shouldReturn0AtGetMaxPageWhenCount0Product() {
    assertEquals(0, aggregationRepository.getMaxPage("", TEST_PHONE3));
  }

  @Test
  public void shouldReturn2AtGetMaxPageWhenCount13Product() {
    assertEquals(2, aggregationRepository.getMaxPage("", TEST_PHONE4));
  }

  @Test
  public void shouldReturn2AtGetMaxPageWhenCount17Products() {
    assertEquals(2, aggregationRepository.getMaxPage("", null));
  }
}
