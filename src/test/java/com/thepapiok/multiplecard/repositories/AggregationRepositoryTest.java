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
  private ProductGetDTO productGetDTO1;
  private ProductGetDTO productGetDTO2;
  private ProductGetDTO productGetDTO3;
  private ProductGetDTO productGetDTO4;
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
    final int testProduct10Amount = 16;
    final int testProduct11Amount = 17;
    final int testProduct12Amount = 18;
    final int testProduct13Amount = 19;
    final int testProduct14Amount = 20;
    final int testProduct15Amount = 21;
    final int testProduct16Amount = 22;
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
    final int testYearCreatedAtOrder1 = 2014;
    final int testMonthCreatedAtOrder1 = 1;
    final int testDayCreatedAtOrder1 = 1;
    final int testHourCreatedAtOrder1 = 1;
    final int testMinuteCreatedAtOrder1 = 1;
    final int testYearCreatedAtOrder2 = 2015;
    final int testMonthCreatedAtOrder2 = 12;
    final int testDayCreatedAtOrder2 = 2;
    final int testHourCreatedAtOrder2 = 3;
    final int testMinuteCreatedAtOrder2 = 10;
    final int testYearCreatedAtOrder3 = 2019;
    final int testMonthCreatedAtOrder3 = 12;
    final int testDayCreatedAtOrder3 = 2;
    final int testHourCreatedAtOrder3 = 3;
    final int testMinuteCreatedAtOrder3 = 10;
    final int testYearCreatedAtOrder4 = 2022;
    final int testMonthCreatedAtOrder4 = 12;
    final int testDayCreatedAtOrder4 = 2;
    final int testHourCreatedAtOrder4 = 3;
    final int testMinuteCreatedAtOrder4 = 10;
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
    Product product2 = new Product();
    product2.setImageUrl("url2");
    product2.setName("product2");
    product2.setDescription("description2");
    product2.setAmount(testProduct2Amount);
    product2.setBarcode("barcode2");
    product2.setShopId(TEST_OWNER_ID);
    product2.setCategories(List.of(category.getId()));
    Product product3 = new Product();
    product3.setImageUrl("url3");
    product3.setName(TEST_PRODUCT_NAME);
    product3.setDescription("description3");
    product3.setAmount(testProduct3Amount);
    product3.setBarcode("barcode3");
    product3.setShopId(TEST_OWNER_ID);
    product3.setCategories(List.of(category.getId()));
    Product product4 = new Product();
    product4.setImageUrl("url4");
    product4.setName("product4");
    product4.setDescription("description4");
    product4.setAmount(testProduct4Amount);
    product4.setBarcode("barcode4");
    product4.setShopId(testOtherShopId);
    product4.setCategories(List.of(category.getId()));
    Product product5 = new Product();
    product5.setImageUrl("url45");
    product5.setName("product5");
    product5.setDescription("description5");
    product5.setAmount(testProduct5Amount);
    product5.setBarcode("barcode5");
    product5.setShopId(testOther3ShopId);
    product5.setCategories(List.of(category.getId()));
    Product product6 = new Product();
    product6.setImageUrl("url6");
    product6.setName("product6");
    product6.setDescription("description6");
    product6.setAmount(testProduct6Amount);
    product6.setBarcode("barcode6");
    product6.setShopId(testOther3ShopId);
    product6.setCategories(List.of(category.getId()));
    Product product7 = new Product();
    product7.setImageUrl("url7");
    product7.setName("product7");
    product7.setDescription("description7");
    product7.setAmount(testProduct7Amount);
    product7.setBarcode("barcode7");
    product7.setShopId(testOther3ShopId);
    product7.setCategories(List.of(category.getId()));
    Product product8 = new Product();
    product8.setImageUrl("url8");
    product8.setName("product8");
    product8.setDescription("description8");
    product8.setAmount(testProduct8Amount);
    product8.setBarcode("barcode8");
    product8.setShopId(testOther3ShopId);
    product8.setCategories(List.of(category.getId()));
    Product product9 = new Product();
    product9.setImageUrl("url9");
    product9.setName("product9");
    product9.setDescription("description9");
    product9.setAmount(testProduct9Amount);
    product9.setBarcode("barcode9");
    product9.setShopId(testOther3ShopId);
    product9.setCategories(List.of(category.getId()));
    Product product10 = new Product();
    product10.setImageUrl("url10");
    product10.setName("product10");
    product10.setDescription("description10");
    product10.setAmount(testProduct10Amount);
    product10.setBarcode("barcode10");
    product10.setShopId(testOther3ShopId);
    product10.setCategories(List.of(category.getId()));
    Product product11 = new Product();
    product11.setImageUrl("url11");
    product11.setName("product11");
    product11.setDescription("description11");
    product11.setAmount(testProduct11Amount);
    product11.setBarcode("barcode11");
    product11.setShopId(testOther3ShopId);
    product11.setCategories(List.of(category.getId()));
    Product product12 = new Product();
    product12.setImageUrl("url12");
    product12.setName("product12");
    product12.setDescription("description12");
    product12.setAmount(testProduct12Amount);
    product12.setBarcode("barcode12");
    product12.setShopId(testOther3ShopId);
    product12.setCategories(List.of(category.getId()));
    Product product13 = new Product();
    product13.setImageUrl("url13");
    product13.setName("product13");
    product13.setDescription("description13");
    product13.setAmount(testProduct13Amount);
    product13.setBarcode("barcode13");
    product13.setShopId(testOther3ShopId);
    product13.setCategories(List.of(category.getId()));
    Product product14 = new Product();
    product14.setImageUrl("url14");
    product14.setName("product14");
    product14.setDescription("description14");
    product14.setAmount(testProduct14Amount);
    product14.setBarcode("barcode14");
    product14.setShopId(testOther3ShopId);
    product14.setCategories(List.of(category.getId()));
    Product product15 = new Product();
    product15.setImageUrl("url15");
    product15.setName("product15");
    product15.setDescription("description15");
    product15.setAmount(testProduct15Amount);
    product15.setBarcode("barcode15");
    product15.setShopId(testOther3ShopId);
    product15.setCategories(List.of(category.getId()));
    Product product16 = new Product();
    product16.setImageUrl("url16");
    product16.setName("product16");
    product16.setDescription("description16");
    product16.setAmount(testProduct16Amount);
    product16.setBarcode("barcode16");
    product16.setShopId(testOther3ShopId);
    product16.setCategories(List.of(category.getId()));
    Product product17 = new Product();
    product17.setImageUrl("url17");
    product17.setName("product17");
    product17.setDescription("description17");
    product17.setAmount(testProduct17Amount);
    product17.setBarcode("barcode17");
    product17.setShopId(testOther3ShopId);
    product17.setCategories(List.of(category.getId()));
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
    order1.setCreatedAt(
        LocalDateTime.of(
            testYearCreatedAtOrder1,
            testMonthCreatedAtOrder1,
            testDayCreatedAtOrder1,
            testHourCreatedAtOrder1,
            testMinuteCreatedAtOrder1));
    mongoTemplate.save(order1);
    Order order2 = new Order();
    order2.setCardId(testCardId);
    order2.setAmount(testOrder2Amount);
    order2.setProductId(product1.getId());
    order2.setUsed(false);
    order2.setCreatedAt(
        LocalDateTime.of(
            testYearCreatedAtOrder2,
            testMonthCreatedAtOrder2,
            testDayCreatedAtOrder2,
            testHourCreatedAtOrder2,
            testMinuteCreatedAtOrder2));
    mongoTemplate.save(order2);
    Order order3 = new Order();
    order3.setCardId(testCardId);
    order3.setAmount(testOrder3Amount);
    order3.setProductId(product1.getId());
    order3.setUsed(false);
    order3.setCreatedAt(
        LocalDateTime.of(
            testYearCreatedAtOrder3,
            testMonthCreatedAtOrder3,
            testDayCreatedAtOrder3,
            testHourCreatedAtOrder3,
            testMinuteCreatedAtOrder3));
    mongoTemplate.save(order3);
    Order order4 = new Order();
    order4.setCardId(testOtherCardId);
    order4.setAmount(testOrder4Amount);
    order4.setProductId(product2.getId());
    order4.setUsed(false);
    order4.setCreatedAt(
        LocalDateTime.of(
            testYearCreatedAtOrder4,
            testMonthCreatedAtOrder4,
            testDayCreatedAtOrder4,
            testHourCreatedAtOrder4,
            testMinuteCreatedAtOrder4));
    mongoTemplate.save(order4);
    Blocked blocked = new Blocked();
    blocked.setExpiredAt(LocalDate.now().plusYears(1));
    blocked.setProductId(product3.getId());
    blocked = mongoTemplate.save(blocked);
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
    productGetDTO3.setBlocked(blocked);
    productGetDTO4 = new ProductGetDTO();
    productGetDTO4.setProduct(product4);
    productGetDTO4.setPromotion(promotion3);
    productGetDTO4.setBlocked(null);
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
        aggregationRepository.getProductsOwner(TEST_PHONE, 0, COUNT_FIELD, true, ""));
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsOwnerWhenCountFieldAndIsAscendingWithoutText() {
    assertEquals(
        List.of(productGetDTO3, productGetDTO2, productGetDTO1),
        aggregationRepository.getProductsOwner(TEST_PHONE, 0, COUNT_FIELD, false, ""));
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsOwnerWhenDateFieldAndIsDescendingWithoutText() {
    assertEquals(
        List.of(productGetDTO2, productGetDTO1, productGetDTO3),
        aggregationRepository.getProductsOwner(TEST_PHONE, 0, DATE_FIELD, true, ""));
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsOwnerWhenDateFieldAndIsAscendingWithoutText() {
    assertEquals(
        List.of(productGetDTO3, productGetDTO1, productGetDTO2),
        aggregationRepository.getProductsOwner(TEST_PHONE, 0, DATE_FIELD, false, ""));
  }

  @Test
  public void
      shouldReturnListOfProductGetDTOAtGetProductsOwnerWhenCountFieldAndIsDescendingWithText() {
    assertEquals(
        List.of(productGetDTO1, productGetDTO3),
        aggregationRepository.getProductsOwner(
            TEST_PHONE, 0, COUNT_FIELD, true, TEST_PRODUCT_NAME));
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
}
