package com.thepapiok.multiplecard.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.BlockedProduct;
import com.thepapiok.multiplecard.collections.Category;
import com.thepapiok.multiplecard.collections.Order;
import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.collections.Promotion;
import com.thepapiok.multiplecard.collections.ReservedProduct;
import com.thepapiok.multiplecard.collections.Role;
import com.thepapiok.multiplecard.collections.Shop;
import com.thepapiok.multiplecard.collections.User;
import com.thepapiok.multiplecard.configs.DbConfig;
import com.thepapiok.multiplecard.dto.CategoryDTO;
import com.thepapiok.multiplecard.dto.PageCategoryDTO;
import com.thepapiok.multiplecard.dto.PageOwnerProductsDTO;
import com.thepapiok.multiplecard.dto.PageProductsDTO;
import com.thepapiok.multiplecard.dto.PageUserDTO;
import com.thepapiok.multiplecard.dto.ProductAtCardDTO;
import com.thepapiok.multiplecard.dto.ProductDTO;
import com.thepapiok.multiplecard.dto.ProductWithShopDTO;
import com.thepapiok.multiplecard.dto.UserDTO;
import com.thepapiok.multiplecard.misc.ProductInfo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

@DataMongoTest
@ActiveProfiles("test")
@Import(DbConfig.class)
public class AggregationRepositoryTest {
  private static final ObjectId TEST_OWNER_ID = new ObjectId("123456789012345678901234");
  private static final ObjectId TEST_ORDER_ID = new ObjectId("123459789012345678901211");
  private static final ObjectId TEST_CARD_ID = new ObjectId("423459789013345678901215");
  private static final ObjectId TEST_OTHER_CARD_ID = new ObjectId("517459789013345678901215");
  private static final String TEST_PHONE = "+48132423412342314231";
  private static final String TEST_PHONE3 = "+48135304342921";
  private static final String TEST_EMAIL = "test@test2";
  private static final String TEST_PRODUCT_NAME = "product";
  private static final String COUNT_FIELD = "count";
  private static final String DATE_FIELD = "date";
  private static final String PRICE_FIELD = "price";
  private static final String ADDED_FIELD = "added";
  private static final String TEST_CATEGORY_NAME = "category";
  private static final String TEST_CATEGORY_OTHER_NAME = "other";
  private static final String TEST_SHOP_NAME = "shop1";
  private static final String TRUE_VALUE = "true";
  private static final String TEST_FIRST_NAME_USER = "firstNameUser3";
  private static final String TEST_LAST_NAME_USER = "lastNameUser3";
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
  private Shop shop1;
  private Shop shop3;
  private Product product1;
  private ProductAtCardDTO productAtCardDTO1;
  private ProductAtCardDTO productAtCardDTO2;
  private ProductAtCardDTO productAtCardDTO3;
  private CategoryDTO categoryDTO1;
  private CategoryDTO categoryDTO2;
  private CategoryDTO categoryDTO3;
  private CategoryDTO categoryDTO4;
  private CategoryDTO categoryDTO5;
  private UserDTO userDTO1;
  private UserDTO userDTO2;
  private UserDTO userDTO3;
  private UserDTO userDTO4;
  private UserDTO userDTO5;
  private UserDTO userDTO6;
  private UserDTO userDTO7;

  @Autowired private MongoTemplate mongoTemplate;
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private ProductRepository productRepository;
  @Autowired private AccountRepository accountRepository;
  @Autowired private PromotionRepository promotionRepository;
  @Autowired private ShopRepository shopRepository;
  @Autowired private ReservedProductsRepository reservedProductsRepository;
  @Autowired private OrderRepository orderRepository;
  @Autowired private UserRepository userRepository;
  private AggregationRepository aggregationRepository;
  @MockBean private MongoTransactionManager mongoTransactionManager;
  @MockBean private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() {
    final ObjectId testOtherCardId = new ObjectId("855456789019940678901112");
    final int testProduct1Price = 1233;
    final int testProduct2Price = 123;
    final int testProduct3Price = 1231;
    final int testProduct4Price = 10;
    final int testProduct5Price = 11;
    final int testProduct6Price = 12;
    final int testProduct7Price = 13;
    final int testProduct8Price = 14;
    final int testProduct9Price = 15;
    final int testProduct10Price = 30;
    final int testProduct11Price = 17;
    final int testProduct12Price = 18;
    final int testProduct13Price = 19;
    final int testProduct14Price = 31;
    final int testProduct15Price = 21;
    final int testProduct16Price = 2;
    final int testProduct17Price = 23;
    final int testPromotion1NewPrice = 100;
    final int testPromotion2NewPrice = 1200;
    final int testPromotion3NewPrice = 5;
    final int testPromotion4NewPrice = 2;
    final int testYearStartAtPromotion1 = 2024;
    final int testMonthStartAtPromotion1 = 5;
    final int testDayStartAtPromotion1 = 3;
    final int testYearStartAtPromotion2 = 2024;
    final int testMonthStartAtPromotion2 = 1;
    final int testDayStartAtPromotion2 = 1;
    final int testYearStartAtPromotion3 = 2020;
    final int testMonthStartAtPromotion3 = 12;
    final int testDayStartAtPromotion3 = 11;
    final int testYearStartAtPromotion4 = 2010;
    final int testMonthStartAtPromotion4 = 9;
    final int testDayStartAtPromotion4 = 1;
    final int testYearExpiredAtPromotion1 = 2025;
    final int testMonthExpiredAtPromotion1 = 10;
    final int testDayExpiredAtPromotion1 = 9;
    final int testYearExpiredAtPromotion2 = 2026;
    final int testMonthExpiredAtPromotion2 = 10;
    final int testDayExpiredAtPromotion2 = 9;
    final int testYearExpiredAtPromotion3 = 2022;
    final int testMonthExpiredAtPromotion3 = 10;
    final int testDayExpiredAtPromotion3 = 9;
    final int testYearExpiredAtPromotion4 = 2010;
    final int testMonthExpiredAtPromotion4 = 11;
    final int testDayExpiredAtPromotion4 = 1;
    final int testOrder1Price = 100;
    final int testOrder2Price = 123;
    final int testOrder3Price = 1200;
    final int testOrder4Price = 10;
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
    final int testPromotionQuantity = 5;
    final int testCount1 = 3;
    final int testCount2 = 2;
    final int testCount3 = 1;
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
    aggregationRepository =
        new AggregationRepository(
            accountRepository,
            mongoTemplate,
            mongoTransactionManager,
            promotionRepository,
            reservedProductsRepository);
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
    category3.setName(TEST_CATEGORY_OTHER_NAME);
    category3 = mongoTemplate.save(category3);
    Category category4 = new Category();
    category4.setOwnerId(TEST_OWNER_ID);
    category4.setName("other category");
    category4 = mongoTemplate.save(category4);
    Category category5 = new Category();
    category5.setOwnerId(TEST_OWNER_ID);
    category5.setName("mat");
    category5 = mongoTemplate.save(category5);
    categoryDTO1 = new CategoryDTO();
    categoryDTO1.setOwnerId(TEST_OWNER_ID.toString());
    categoryDTO1.setName(TEST_CATEGORY_NAME);
    categoryDTO2 = new CategoryDTO();
    categoryDTO2.setOwnerId(TEST_OWNER_ID.toString());
    categoryDTO2.setName("Category2");
    categoryDTO3 = new CategoryDTO();
    categoryDTO3.setOwnerId(TEST_OWNER_ID.toString());
    categoryDTO3.setName(TEST_CATEGORY_OTHER_NAME);
    categoryDTO4 = new CategoryDTO();
    categoryDTO4.setOwnerId(TEST_OWNER_ID.toString());
    categoryDTO4.setName("other category");
    categoryDTO5 = new CategoryDTO();
    categoryDTO5.setOwnerId(TEST_OWNER_ID.toString());
    categoryDTO5.setName("mat");
    Address address = new Address();
    address.setCountry("country");
    address.setCity("city");
    address.setStreet("street");
    address.setProvince("province");
    address.setPostalCode("postalCode");
    address.setHouseNumber("house");
    address.setApartmentNumber(1);
    shop1 = new Shop();
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
    shop3 = new Shop();
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
    product1 = new Product();
    product1.setImageUrl("url1");
    product1.setName(TEST_PRODUCT_NAME);
    product1.setDescription("description1");
    product1.setPrice(testProduct1Price);
    product1.setBarcode("barcode1");
    product1.setShopId(shop1.getId());
    product1.setCategories(List.of(category1.getId()));
    product1.setUpdatedAt(testDate1);
    Product product2 = new Product();
    product2.setImageUrl("url2");
    product2.setName("product2");
    product2.setDescription("description2");
    product2.setPrice(testProduct2Price);
    product2.setBarcode("barcode2");
    product2.setShopId(shop3.getId());
    product2.setCategories(List.of(category1.getId(), category5.getId()));
    product2.setUpdatedAt(testDate2);
    Product product3 = new Product();
    product3.setImageUrl("url3");
    product3.setName(TEST_PRODUCT_NAME);
    product3.setDescription("description3");
    product3.setPrice(testProduct3Price);
    product3.setBarcode("barcode3");
    product3.setShopId(shop1.getId());
    product3.setCategories(List.of(category2.getId()));
    product3.setUpdatedAt(testDate3);
    Product product4 = new Product();
    product4.setImageUrl("url4");
    product4.setName("product4");
    product4.setDescription("description4");
    product4.setPrice(testProduct4Price);
    product4.setBarcode("barcode4");
    product4.setShopId(shop3.getId());
    product4.setCategories(List.of(category2.getId()));
    product4.setUpdatedAt(testDate4);
    Product product5 = new Product();
    product5.setImageUrl("url45");
    product5.setName(TEST_PRODUCT_NAME);
    product5.setDescription("description5");
    product5.setPrice(testProduct5Price);
    product5.setBarcode("barcode5");
    product5.setShopId(shop3.getId());
    product5.setCategories(List.of(category3.getId()));
    product5.setUpdatedAt(testDate5);
    Product product6 = new Product();
    product6.setImageUrl("url6");
    product6.setName(TEST_PRODUCT_NAME);
    product6.setDescription("description6");
    product6.setPrice(testProduct6Price);
    product6.setBarcode("barcode6");
    product6.setShopId(shop3.getId());
    product6.setCategories(List.of(category1.getId(), category2.getId()));
    product6.setUpdatedAt(testDate6);
    Product product7 = new Product();
    product7.setImageUrl("url7");
    product7.setName("product7");
    product7.setDescription(TEST_PRODUCT_NAME);
    product7.setPrice(testProduct7Price);
    product7.setBarcode("barcode7");
    product7.setShopId(shop3.getId());
    product7.setCategories(List.of(category4.getId()));
    product7.setUpdatedAt(testDate7);
    Product product8 = new Product();
    product8.setImageUrl("url8");
    product8.setName("product8");
    product8.setDescription("description8");
    product8.setPrice(testProduct8Price);
    product8.setBarcode("barcode8");
    product8.setShopId(shop3.getId());
    product8.setCategories(List.of(category4.getId()));
    product8.setUpdatedAt(testDate8);
    Product product9 = new Product();
    product9.setImageUrl("url9");
    product9.setName("product9");
    product9.setDescription("description9");
    product9.setPrice(testProduct9Price);
    product9.setBarcode("barcode9");
    product9.setShopId(shop3.getId());
    product9.setCategories(List.of(category1.getId()));
    product9.setUpdatedAt(testDate9);
    Product product10 = new Product();
    product10.setImageUrl("url10");
    product10.setName("product10");
    product10.setDescription("description10");
    product10.setPrice(testProduct10Price);
    product10.setBarcode("barcode10");
    product10.setShopId(shop3.getId());
    product10.setCategories(List.of(category3.getId(), category1.getId()));
    product10.setUpdatedAt(testDate10);
    Product product11 = new Product();
    product11.setImageUrl("url11");
    product11.setName("product11");
    product11.setDescription("description11");
    product11.setPrice(testProduct11Price);
    product11.setBarcode("barcode11");
    product11.setShopId(shop3.getId());
    product11.setCategories(List.of(category3.getId()));
    product11.setUpdatedAt(testDate11);
    Product product12 = new Product();
    product12.setImageUrl("url12");
    product12.setName("product12");
    product12.setDescription("description12");
    product12.setPrice(testProduct12Price);
    product12.setBarcode("barcode12");
    product12.setShopId(shop3.getId());
    product12.setCategories(List.of(category5.getId()));
    product12.setUpdatedAt(testDate12);
    Product product13 = new Product();
    product13.setImageUrl("url13");
    product13.setName("product13");
    product13.setDescription("description13");
    product13.setPrice(testProduct13Price);
    product13.setBarcode("barcode13");
    product13.setShopId(shop3.getId());
    product13.setCategories(List.of(category1.getId()));
    product13.setUpdatedAt(testDate13);
    Product product14 = new Product();
    product14.setImageUrl("url14");
    product14.setName("product14");
    product14.setDescription("description14");
    product14.setPrice(testProduct14Price);
    product14.setBarcode("barcode14");
    product14.setShopId(shop1.getId());
    product14.setCategories(List.of(category2.getId()));
    product14.setUpdatedAt(testDate14);
    Product product15 = new Product();
    product15.setImageUrl("url15");
    product15.setName("product15");
    product15.setDescription("description15");
    product15.setPrice(testProduct15Price);
    product15.setBarcode("barcode15");
    product15.setShopId(shop1.getId());
    product15.setCategories(List.of(category3.getId()));
    product15.setUpdatedAt(testDate15);
    Product product16 = new Product();
    product16.setImageUrl("url16");
    product16.setName("product16");
    product16.setDescription("description16");
    product16.setPrice(testProduct16Price);
    product16.setBarcode("barcode16");
    product16.setShopId(shop3.getId());
    product16.setCategories(List.of(category4.getId()));
    product16.setUpdatedAt(testDate16);
    Product product17 = new Product();
    product17.setImageUrl("url17");
    product17.setName(TEST_PRODUCT_NAME);
    product17.setDescription("description17");
    product17.setPrice(testProduct17Price);
    product17.setBarcode("barcode17");
    product17.setShopId(shop3.getId());
    product17.setCategories(List.of(category5.getId()));
    product17.setUpdatedAt(testDate17);
    product1 = mongoTemplate.save(product1);
    product2 = mongoTemplate.save(product2);
    mongoTemplate.save(product3);
    product4 = mongoTemplate.save(product4);
    mongoTemplate.save(product5);
    product6 = mongoTemplate.save(product6);
    mongoTemplate.save(product7);
    mongoTemplate.save(product8);
    mongoTemplate.save(product9);
    product10 = mongoTemplate.save(product10);
    mongoTemplate.save(product11);
    mongoTemplate.save(product12);
    mongoTemplate.save(product13);
    mongoTemplate.save(product14);
    mongoTemplate.save(product15);
    mongoTemplate.save(product16);
    mongoTemplate.save(product17);
    Promotion promotion1 = new Promotion();
    promotion1.setProductId(product1.getId());
    promotion1.setNewPrice(testPromotion1NewPrice);
    promotion1.setStartAt(
        LocalDate.of(
            testYearStartAtPromotion1, testMonthStartAtPromotion1, testDayStartAtPromotion1));
    promotion1.setExpiredAt(
        LocalDate.of(
            testYearExpiredAtPromotion1, testMonthExpiredAtPromotion1, testDayExpiredAtPromotion1));
    mongoTemplate.save(promotion1);
    Promotion promotion2 = new Promotion();
    promotion2.setProductId(product2.getId());
    promotion2.setNewPrice(testPromotion2NewPrice);
    promotion2.setStartAt(
        LocalDate.of(
            testYearStartAtPromotion2, testMonthStartAtPromotion2, testDayStartAtPromotion2));
    promotion2.setExpiredAt(
        LocalDate.of(
            testYearExpiredAtPromotion2, testMonthExpiredAtPromotion2, testDayExpiredAtPromotion2));
    mongoTemplate.save(promotion2);
    Promotion promotion3 = new Promotion();
    promotion3.setProductId(product4.getId());
    promotion3.setNewPrice(testPromotion3NewPrice);
    promotion3.setQuantity(testPromotionQuantity);
    promotion3.setStartAt(
        LocalDate.of(
            testYearStartAtPromotion3, testMonthStartAtPromotion3, testDayStartAtPromotion3));
    promotion3.setExpiredAt(
        LocalDate.of(
            testYearExpiredAtPromotion3, testMonthExpiredAtPromotion3, testDayExpiredAtPromotion3));
    promotion3 = mongoTemplate.save(promotion3);
    Promotion promotion4 = new Promotion();
    promotion4.setProductId(product7.getId());
    promotion4.setNewPrice(testPromotion4NewPrice);
    promotion4.setQuantity(1);
    promotion4.setStartAt(
        LocalDate.of(
            testYearStartAtPromotion4, testMonthStartAtPromotion4, testDayStartAtPromotion4));
    promotion4.setExpiredAt(
        LocalDate.of(
            testYearExpiredAtPromotion4, testMonthExpiredAtPromotion4, testDayExpiredAtPromotion4));
    promotion4 = mongoTemplate.save(promotion4);
    ReservedProduct reservedProduct1 = new ReservedProduct();
    reservedProduct1.setPromotionId(promotion3.getId());
    reservedProduct1.setOrderId(new ObjectId());
    reservedProduct1.setCardId(new ObjectId());
    reservedProduct1.setEncryptedIp("sdafasdfsfasfad");
    mongoTemplate.save(reservedProduct1);
    ReservedProduct reservedProduct2 = new ReservedProduct();
    reservedProduct2.setPromotionId(promotion3.getId());
    reservedProduct2.setOrderId(new ObjectId());
    reservedProduct2.setCardId(new ObjectId());
    reservedProduct2.setEncryptedIp("sgdfaasdfdas3");
    mongoTemplate.save(reservedProduct2);
    User user1 = new User();
    user1.setRestricted(true);
    user1.setPoints(0);
    user1.setReview(null);
    user1.setCardId(new ObjectId());
    user1.setFirstName("firstNameUser1");
    user1.setLastName("lastNameUser1");
    user1.setAddress(address);
    mongoTemplate.save(user1);
    User user2 = new User();
    user2.setRestricted(false);
    user2.setPoints(0);
    user2.setReview(null);
    user2.setCardId(new ObjectId());
    user2.setFirstName("firstNameUser2");
    user2.setLastName("lastNameUser2");
    user2.setAddress(address);
    mongoTemplate.save(user2);
    User user3 = new User();
    user3.setRestricted(true);
    user3.setPoints(0);
    user3.setReview(null);
    user3.setCardId(new ObjectId());
    user3.setFirstName(TEST_FIRST_NAME_USER);
    user3.setLastName(TEST_LAST_NAME_USER);
    user3.setAddress(address);
    mongoTemplate.save(user3);
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
    account2.setEmail(TEST_EMAIL);
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
    Account account5 = new Account();
    account5.setId(user1.getId());
    account5.setPhone("+48535243252345");
    account5.setEmail("test@test5");
    account5.setRole(Role.ROLE_USER);
    account5.setBanned(false);
    account5.setActive(false);
    account5.setPassword("password5");
    mongoTemplate.save(account5);
    Account account6 = new Account();
    account6.setId(user2.getId());
    account6.setPhone("+481234123452314");
    account6.setEmail("test@test6");
    account6.setRole(Role.ROLE_USER);
    account6.setBanned(true);
    account6.setActive(true);
    account6.setPassword("password6");
    mongoTemplate.save(account6);
    Account account7 = new Account();
    account7.setId(user3.getId());
    account7.setPhone("+4853543535435");
    account7.setEmail("test@test7");
    account7.setRole(Role.ROLE_ADMIN);
    account7.setBanned(true);
    account7.setActive(false);
    account7.setPassword("password7");
    mongoTemplate.save(account7);
    Order order1 = new Order();
    order1.setCardId(TEST_CARD_ID);
    order1.setPrice(testOrder1Price);
    order1.setProductId(product1.getId());
    order1.setUsed(false);
    order1.setCreatedAt(testDate1);
    order1.setShopId(shop1.getId());
    order1.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order1);
    Order order2 = new Order();
    order2.setCardId(TEST_OTHER_CARD_ID);
    order2.setPrice(testOrder2Price);
    order2.setProductId(product1.getId());
    order2.setUsed(false);
    order2.setCreatedAt(testDate2);
    order2.setShopId(shop1.getId());
    order2.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order2);
    Order order3 = new Order();
    order3.setCardId(TEST_OTHER_CARD_ID);
    order3.setPrice(testOrder3Price);
    order3.setProductId(product1.getId());
    order3.setUsed(false);
    order3.setCreatedAt(testDate3);
    order3.setShopId(shop1.getId());
    order3.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order3);
    Order order143 = new Order();
    order143.setCardId(TEST_CARD_ID);
    order143.setPrice(testOrder1Price);
    order143.setProductId(product1.getId());
    order143.setUsed(true);
    order143.setCreatedAt(testDate1);
    order143.setShopId(shop1.getId());
    order143.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order143);
    Order order144 = new Order();
    order144.setCardId(TEST_OTHER_CARD_ID);
    order144.setPrice(testOrder2Price);
    order144.setProductId(product1.getId());
    order144.setUsed(true);
    order144.setCreatedAt(testDate2);
    order144.setShopId(shop1.getId());
    order144.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order144);
    Order order145 = new Order();
    order145.setCardId(TEST_OTHER_CARD_ID);
    order145.setPrice(testOrder3Price);
    order145.setProductId(product1.getId());
    order145.setUsed(true);
    order145.setCreatedAt(testDate3);
    order145.setShopId(shop1.getId());
    order145.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order145);
    Order order4 = new Order();
    order4.setCardId(testOtherCardId);
    order4.setPrice(testOrder4Price);
    order4.setProductId(product2.getId());
    order4.setUsed(true);
    order4.setCreatedAt(testDate4);
    order4.setShopId(shop3.getId());
    order4.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order4);
    Order order139 = new Order();
    order139.setCardId(testOtherCardId);
    order139.setPrice(testOrder4Price);
    order139.setProductId(product2.getId());
    order139.setUsed(false);
    order139.setCreatedAt(testDate4);
    order139.setShopId(shop3.getId());
    order139.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order139);
    Order order140 = new Order();
    order140.setCardId(testOtherCardId);
    order140.setPrice(testOrder4Price);
    order140.setProductId(product2.getId());
    order140.setUsed(false);
    order140.setCreatedAt(testDate4);
    order140.setShopId(shop3.getId());
    order140.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order140);
    Order order5 = new Order();
    order5.setCardId(testOtherCardId);
    order5.setPrice(testOrder4Price);
    order5.setProductId(product4.getId());
    order5.setUsed(true);
    order5.setCreatedAt(testDate5);
    order5.setShopId(shop3.getId());
    order5.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order5);
    Order order6 = new Order();
    order6.setCardId(testOtherCardId);
    order6.setPrice(testOrder4Price);
    order6.setProductId(product4.getId());
    order6.setUsed(true);
    order6.setCreatedAt(testDate5);
    order6.setShopId(shop3.getId());
    order6.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order6);
    Order order8 = new Order();
    order8.setCardId(testOtherCardId);
    order8.setPrice(testOrder4Price);
    order8.setProductId(product5.getId());
    order8.setUsed(true);
    order8.setCreatedAt(testDate6);
    order8.setShopId(shop3.getId());
    order8.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order8);
    Order order9 = new Order();
    order9.setCardId(testOtherCardId);
    order9.setPrice(testOrder4Price);
    order9.setProductId(product5.getId());
    order9.setUsed(true);
    order9.setCreatedAt(testDate6);
    order9.setShopId(shop3.getId());
    order9.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order9);
    Order order10 = new Order();
    order10.setCardId(testOtherCardId);
    order10.setPrice(testOrder4Price);
    order10.setProductId(product5.getId());
    order10.setUsed(true);
    order10.setCreatedAt(testDate6);
    order10.setShopId(shop3.getId());
    order10.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order10);
    Order order11 = new Order();
    order11.setCardId(testOtherCardId);
    order11.setPrice(testOrder4Price);
    order11.setProductId(product5.getId());
    order11.setUsed(true);
    order11.setCreatedAt(testDate6);
    order11.setShopId(shop3.getId());
    order11.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order11);
    Order order141 = new Order();
    order141.setCardId(testOtherCardId);
    order141.setPrice(testOrder4Price);
    order141.setProductId(product5.getId());
    order141.setUsed(false);
    order141.setCreatedAt(testDate6);
    order141.setShopId(shop3.getId());
    order141.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order141);
    Order order142 = new Order();
    order142.setCardId(testOtherCardId);
    order142.setPrice(testOrder4Price);
    order142.setProductId(product5.getId());
    order142.setUsed(false);
    order142.setCreatedAt(testDate6);
    order142.setShopId(shop3.getId());
    order142.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order142);
    Order order12 = new Order();
    order12.setCardId(testOtherCardId);
    order12.setPrice(testOrder4Price);
    order12.setProductId(product6.getId());
    order12.setUsed(true);
    order12.setCreatedAt(testDate7);
    order12.setShopId(shop3.getId());
    order12.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order12);
    Order order13 = new Order();
    order13.setCardId(testOtherCardId);
    order13.setPrice(testOrder4Price);
    order13.setProductId(product6.getId());
    order13.setUsed(true);
    order13.setCreatedAt(testDate7);
    order13.setShopId(shop3.getId());
    order13.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order13);
    Order order14 = new Order();
    order14.setCardId(testOtherCardId);
    order14.setPrice(testOrder4Price);
    order14.setProductId(product6.getId());
    order14.setUsed(true);
    order14.setCreatedAt(testDate7);
    order14.setShopId(shop3.getId());
    order14.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order14);
    Order order15 = new Order();
    order15.setCardId(TEST_CARD_ID);
    order15.setPrice(testOrder3Price);
    order15.setProductId(product6.getId());
    order15.setUsed(false);
    order15.setCreatedAt(testDate7);
    order15.setShopId(shop3.getId());
    order15.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order15);
    Order order16 = new Order();
    order16.setCardId(TEST_CARD_ID);
    order16.setPrice(testOrder3Price);
    order16.setProductId(product6.getId());
    order16.setUsed(false);
    order16.setCreatedAt(testDate7);
    order16.setShopId(shop3.getId());
    order16.setOrderId(TEST_ORDER_ID);
    order16 = mongoTemplate.save(order16);
    Order order17 = new Order();
    order17.setCardId(testOtherCardId);
    order17.setPrice(testOrder4Price);
    order17.setProductId(product7.getId());
    order17.setUsed(true);
    order17.setCreatedAt(testDate8);
    order17.setShopId(shop3.getId());
    order17.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order17);
    Order order18 = new Order();
    order18.setCardId(testOtherCardId);
    order18.setPrice(testOrder4Price);
    order18.setProductId(product7.getId());
    order18.setUsed(true);
    order18.setCreatedAt(testDate8);
    order18.setShopId(shop3.getId());
    order18.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order18);
    Order order19 = new Order();
    order19.setCardId(testOtherCardId);
    order19.setPrice(testOrder4Price);
    order19.setProductId(product7.getId());
    order19.setUsed(true);
    order19.setCreatedAt(testDate8);
    order19.setShopId(shop3.getId());
    order19.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order19);
    Order order20 = new Order();
    order20.setCardId(testOtherCardId);
    order20.setPrice(testOrder4Price);
    order20.setProductId(product7.getId());
    order20.setUsed(true);
    order20.setCreatedAt(testDate8);
    order20.setShopId(shop3.getId());
    order20.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order20);
    Order order21 = new Order();
    order21.setCardId(testOtherCardId);
    order21.setPrice(testOrder4Price);
    order21.setProductId(product7.getId());
    order21.setUsed(true);
    order21.setCreatedAt(testDate8);
    order21.setShopId(shop3.getId());
    order21.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order21);
    Order order22 = new Order();
    order22.setCardId(testOtherCardId);
    order22.setPrice(testOrder4Price);
    order22.setProductId(product7.getId());
    order22.setUsed(true);
    order22.setCreatedAt(testDate8);
    order22.setShopId(shop3.getId());
    order22.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order22);
    Order order23 = new Order();
    order23.setCardId(testOtherCardId);
    order23.setPrice(testOrder4Price);
    order23.setProductId(product8.getId());
    order23.setUsed(true);
    order23.setCreatedAt(testDate9);
    order23.setShopId(shop3.getId());
    order23.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order23);
    Order order24 = new Order();
    order24.setCardId(testOtherCardId);
    order24.setPrice(testOrder4Price);
    order24.setProductId(product8.getId());
    order24.setUsed(true);
    order24.setCreatedAt(testDate9);
    order24.setShopId(shop3.getId());
    order24.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order24);
    Order order25 = new Order();
    order25.setCardId(testOtherCardId);
    order25.setPrice(testOrder4Price);
    order25.setProductId(product8.getId());
    order25.setUsed(true);
    order25.setCreatedAt(testDate9);
    order25.setShopId(shop3.getId());
    order25.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order25);
    Order order26 = new Order();
    order26.setCardId(testOtherCardId);
    order26.setPrice(testOrder4Price);
    order26.setProductId(product8.getId());
    order26.setUsed(true);
    order26.setCreatedAt(testDate9);
    order26.setShopId(shop3.getId());
    order26.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order26);
    Order order27 = new Order();
    order27.setCardId(testOtherCardId);
    order27.setPrice(testOrder4Price);
    order27.setProductId(product8.getId());
    order27.setUsed(true);
    order27.setCreatedAt(testDate9);
    order27.setShopId(shop3.getId());
    order27.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order27);
    Order order28 = new Order();
    order28.setCardId(testOtherCardId);
    order28.setPrice(testOrder4Price);
    order28.setProductId(product8.getId());
    order28.setUsed(true);
    order28.setCreatedAt(testDate9);
    order28.setShopId(shop3.getId());
    order28.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order28);
    Order order29 = new Order();
    order29.setCardId(testOtherCardId);
    order29.setPrice(testOrder4Price);
    order29.setProductId(product8.getId());
    order29.setUsed(true);
    order29.setCreatedAt(testDate9);
    order29.setShopId(shop3.getId());
    order29.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order29);
    Order order30 = new Order();
    order30.setCardId(testOtherCardId);
    order30.setPrice(testOrder4Price);
    order30.setProductId(product9.getId());
    order30.setUsed(true);
    order30.setCreatedAt(testDate10);
    order30.setShopId(shop3.getId());
    order30.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order30);
    Order order31 = new Order();
    order31.setCardId(testOtherCardId);
    order31.setPrice(testOrder4Price);
    order31.setProductId(product9.getId());
    order31.setUsed(true);
    order31.setCreatedAt(testDate10);
    order31.setShopId(shop3.getId());
    order31.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order31);
    Order order32 = new Order();
    order32.setCardId(testOtherCardId);
    order32.setPrice(testOrder4Price);
    order32.setProductId(product9.getId());
    order32.setUsed(true);
    order32.setCreatedAt(testDate10);
    order32.setShopId(shop3.getId());
    order32.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order32);
    Order order33 = new Order();
    order33.setCardId(testOtherCardId);
    order33.setPrice(testOrder4Price);
    order33.setProductId(product9.getId());
    order33.setUsed(true);
    order33.setCreatedAt(testDate10);
    order33.setShopId(shop3.getId());
    order33.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order33);
    Order order34 = new Order();
    order34.setCardId(testOtherCardId);
    order34.setPrice(testOrder4Price);
    order34.setProductId(product9.getId());
    order34.setUsed(true);
    order34.setCreatedAt(testDate10);
    order34.setShopId(shop3.getId());
    order34.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order34);
    Order order36 = new Order();
    order36.setCardId(testOtherCardId);
    order36.setPrice(testOrder4Price);
    order36.setProductId(product9.getId());
    order36.setUsed(true);
    order36.setCreatedAt(testDate10);
    order36.setShopId(shop3.getId());
    order36.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order36);
    Order order37 = new Order();
    order37.setCardId(testOtherCardId);
    order37.setPrice(testOrder4Price);
    order37.setProductId(product9.getId());
    order37.setUsed(true);
    order37.setCreatedAt(testDate10);
    order37.setShopId(shop3.getId());
    order37.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order37);
    Order order38 = new Order();
    order38.setCardId(testOtherCardId);
    order38.setPrice(testOrder4Price);
    order38.setProductId(product9.getId());
    order38.setUsed(true);
    order38.setCreatedAt(testDate10);
    order38.setShopId(shop3.getId());
    order38.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order38);
    Order order146 = new Order();
    order146.setCardId(testOtherCardId);
    order146.setPrice(testOrder4Price);
    order146.setProductId(product10.getId());
    order146.setUsed(true);
    order146.setCreatedAt(testDate11);
    order146.setShopId(shop3.getId());
    order146.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order146);
    Order order147 = new Order();
    order147.setCardId(testOtherCardId);
    order147.setPrice(testOrder4Price);
    order147.setProductId(product10.getId());
    order147.setUsed(true);
    order147.setCreatedAt(testDate11);
    order147.setShopId(shop3.getId());
    order147.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order147);
    Order order148 = new Order();
    order148.setCardId(testOtherCardId);
    order148.setPrice(testOrder4Price);
    order148.setProductId(product10.getId());
    order148.setUsed(true);
    order148.setCreatedAt(testDate11);
    order148.setShopId(shop3.getId());
    order148.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order148);
    Order order39 = new Order();
    order39.setCardId(TEST_CARD_ID);
    order39.setPrice(testOrder4Price);
    order39.setProductId(product10.getId());
    order39.setUsed(false);
    order39.setCreatedAt(testDate11);
    order39.setShopId(shop3.getId());
    order39.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order39);
    Order order40 = new Order();
    order40.setCardId(TEST_CARD_ID);
    order40.setPrice(testOrder4Price);
    order40.setProductId(product10.getId());
    order40.setUsed(false);
    order40.setCreatedAt(testDate11);
    order40.setShopId(shop3.getId());
    order40.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order40);
    Order order41 = new Order();
    order41.setCardId(testOtherCardId);
    order41.setPrice(testOrder4Price);
    order41.setProductId(product10.getId());
    order41.setUsed(true);
    order41.setCreatedAt(testDate11);
    order41.setShopId(shop3.getId());
    order41.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order41);
    Order order42 = new Order();
    order42.setCardId(testOtherCardId);
    order42.setPrice(testOrder4Price);
    order42.setProductId(product10.getId());
    order42.setUsed(true);
    order42.setCreatedAt(testDate11);
    order42.setShopId(shop3.getId());
    order42.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order42);
    Order order43 = new Order();
    order43.setCardId(testOtherCardId);
    order43.setPrice(testOrder4Price);
    order43.setProductId(product10.getId());
    order43.setUsed(true);
    order43.setCreatedAt(testDate11);
    order43.setShopId(shop3.getId());
    order43.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order43);
    Order order44 = new Order();
    order44.setCardId(testOtherCardId);
    order44.setPrice(testOrder4Price);
    order44.setProductId(product10.getId());
    order44.setUsed(true);
    order44.setCreatedAt(testDate11);
    order44.setShopId(shop3.getId());
    order44.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order44);
    Order order45 = new Order();
    order45.setCardId(testOtherCardId);
    order45.setPrice(testOrder4Price);
    order45.setProductId(product10.getId());
    order45.setUsed(true);
    order45.setCreatedAt(testDate11);
    order45.setShopId(shop3.getId());
    order45.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order45);
    Order order46 = new Order();
    order46.setCardId(TEST_CARD_ID);
    order46.setPrice(testOrder4Price);
    order46.setProductId(product10.getId());
    order46.setUsed(false);
    order46.setCreatedAt(testDate11);
    order46.setShopId(shop3.getId());
    order46.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order46);
    Order order47 = new Order();
    order47.setCardId(testOtherCardId);
    order47.setPrice(testOrder4Price);
    order47.setProductId(product10.getId());
    order47.setUsed(true);
    order47.setCreatedAt(testDate11);
    order47.setShopId(shop3.getId());
    order47.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order47);
    Order order48 = new Order();
    order48.setCardId(testOtherCardId);
    order48.setPrice(testOrder4Price);
    order48.setProductId(product11.getId());
    order48.setUsed(true);
    order48.setCreatedAt(testDate12);
    order48.setShopId(shop3.getId());
    order48.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order48);
    Order order49 = new Order();
    order49.setCardId(testOtherCardId);
    order49.setPrice(testOrder4Price);
    order49.setProductId(product11.getId());
    order49.setUsed(true);
    order49.setCreatedAt(testDate12);
    order49.setShopId(shop3.getId());
    order49.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order49);
    Order order50 = new Order();
    order50.setCardId(testOtherCardId);
    order50.setPrice(testOrder4Price);
    order50.setProductId(product11.getId());
    order50.setUsed(true);
    order50.setCreatedAt(testDate12);
    order50.setShopId(shop3.getId());
    order50.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order50);
    Order order51 = new Order();
    order51.setCardId(testOtherCardId);
    order51.setPrice(testOrder4Price);
    order51.setProductId(product11.getId());
    order51.setUsed(true);
    order51.setCreatedAt(testDate12);
    order51.setShopId(shop3.getId());
    order51.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order51);
    Order order52 = new Order();
    order52.setCardId(testOtherCardId);
    order52.setPrice(testOrder4Price);
    order52.setProductId(product11.getId());
    order52.setUsed(true);
    order52.setCreatedAt(testDate12);
    order52.setShopId(shop3.getId());
    order52.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order52);
    Order order53 = new Order();
    order53.setCardId(testOtherCardId);
    order53.setPrice(testOrder4Price);
    order53.setProductId(product11.getId());
    order53.setUsed(true);
    order53.setCreatedAt(testDate12);
    order53.setShopId(shop3.getId());
    order53.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order53);
    Order order54 = new Order();
    order54.setCardId(testOtherCardId);
    order54.setPrice(testOrder4Price);
    order54.setProductId(product11.getId());
    order54.setUsed(true);
    order54.setCreatedAt(testDate12);
    order54.setShopId(shop3.getId());
    order54.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order54);
    Order order55 = new Order();
    order55.setCardId(testOtherCardId);
    order55.setPrice(testOrder4Price);
    order55.setProductId(product11.getId());
    order55.setUsed(true);
    order55.setCreatedAt(testDate12);
    order55.setShopId(shop3.getId());
    order55.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order55);
    Order order56 = new Order();
    order56.setCardId(testOtherCardId);
    order56.setPrice(testOrder4Price);
    order56.setProductId(product11.getId());
    order56.setUsed(true);
    order56.setCreatedAt(testDate12);
    order56.setShopId(shop3.getId());
    order56.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order56);
    Order order57 = new Order();
    order57.setCardId(testOtherCardId);
    order57.setPrice(testOrder4Price);
    order57.setProductId(product11.getId());
    order57.setUsed(true);
    order57.setCreatedAt(testDate12);
    order57.setShopId(shop3.getId());
    order57.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order57);
    Order order58 = new Order();
    order58.setCardId(testOtherCardId);
    order58.setPrice(testOrder4Price);
    order58.setProductId(product12.getId());
    order58.setUsed(true);
    order58.setCreatedAt(testDate13);
    order58.setShopId(shop3.getId());
    order58.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order58);
    Order order59 = new Order();
    order59.setCardId(testOtherCardId);
    order59.setPrice(testOrder4Price);
    order59.setProductId(product12.getId());
    order59.setUsed(true);
    order59.setCreatedAt(testDate13);
    order59.setShopId(shop3.getId());
    order59.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order59);
    Order order60 = new Order();
    order60.setCardId(testOtherCardId);
    order60.setPrice(testOrder4Price);
    order60.setProductId(product12.getId());
    order60.setUsed(true);
    order60.setCreatedAt(testDate13);
    order60.setShopId(shop3.getId());
    order60.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order60);
    Order order61 = new Order();
    order61.setCardId(testOtherCardId);
    order61.setPrice(testOrder4Price);
    order61.setProductId(product12.getId());
    order61.setUsed(true);
    order61.setCreatedAt(testDate13);
    order61.setShopId(shop3.getId());
    order61.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order61);
    Order order62 = new Order();
    order62.setCardId(testOtherCardId);
    order62.setPrice(testOrder4Price);
    order62.setProductId(product12.getId());
    order62.setUsed(true);
    order62.setCreatedAt(testDate13);
    order62.setShopId(shop3.getId());
    order62.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order62);
    Order order63 = new Order();
    order63.setCardId(testOtherCardId);
    order63.setPrice(testOrder4Price);
    order63.setProductId(product12.getId());
    order63.setUsed(true);
    order63.setCreatedAt(testDate13);
    order63.setShopId(shop3.getId());
    order63.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order63);
    Order order64 = new Order();
    order64.setCardId(testOtherCardId);
    order64.setPrice(testOrder4Price);
    order64.setProductId(product12.getId());
    order64.setUsed(true);
    order64.setCreatedAt(testDate13);
    order64.setShopId(shop3.getId());
    order64.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order64);
    Order order65 = new Order();
    order65.setCardId(testOtherCardId);
    order65.setPrice(testOrder4Price);
    order65.setProductId(product12.getId());
    order65.setUsed(true);
    order65.setCreatedAt(testDate13);
    order65.setShopId(shop3.getId());
    order65.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order65);
    Order order66 = new Order();
    order66.setCardId(testOtherCardId);
    order66.setPrice(testOrder4Price);
    order66.setProductId(product12.getId());
    order66.setUsed(true);
    order66.setCreatedAt(testDate13);
    order66.setShopId(shop3.getId());
    order66.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order66);
    Order order67 = new Order();
    order67.setCardId(testOtherCardId);
    order67.setPrice(testOrder4Price);
    order67.setProductId(product12.getId());
    order67.setUsed(true);
    order67.setCreatedAt(testDate13);
    order67.setShopId(shop3.getId());
    order67.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order67);
    Order order68 = new Order();
    order68.setCardId(testOtherCardId);
    order68.setPrice(testOrder4Price);
    order68.setProductId(product12.getId());
    order68.setUsed(true);
    order68.setCreatedAt(testDate13);
    order68.setShopId(shop3.getId());
    order68.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order68);
    Order order69 = new Order();
    order69.setCardId(testOtherCardId);
    order69.setPrice(testOrder4Price);
    order69.setProductId(product13.getId());
    order69.setUsed(true);
    order69.setCreatedAt(testDate14);
    order69.setShopId(shop3.getId());
    order69.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order69);
    Order order70 = new Order();
    order70.setCardId(testOtherCardId);
    order70.setPrice(testOrder4Price);
    order70.setProductId(product13.getId());
    order70.setUsed(true);
    order70.setCreatedAt(testDate14);
    order70.setShopId(shop3.getId());
    order70.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order70);
    Order order71 = new Order();
    order71.setCardId(testOtherCardId);
    order71.setPrice(testOrder4Price);
    order71.setProductId(product13.getId());
    order71.setUsed(true);
    order71.setCreatedAt(testDate14);
    order71.setShopId(shop3.getId());
    order71.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order71);
    Order order72 = new Order();
    order72.setCardId(testOtherCardId);
    order72.setPrice(testOrder4Price);
    order72.setProductId(product13.getId());
    order72.setUsed(true);
    order72.setCreatedAt(testDate14);
    order72.setShopId(shop3.getId());
    order72.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order72);
    Order order73 = new Order();
    order73.setCardId(testOtherCardId);
    order73.setPrice(testOrder4Price);
    order73.setProductId(product13.getId());
    order73.setUsed(true);
    order73.setCreatedAt(testDate14);
    order73.setShopId(shop3.getId());
    order73.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order73);
    Order order74 = new Order();
    order74.setCardId(testOtherCardId);
    order74.setPrice(testOrder4Price);
    order74.setProductId(product13.getId());
    order74.setUsed(true);
    order74.setCreatedAt(testDate14);
    order74.setShopId(shop3.getId());
    order74.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order74);
    Order order75 = new Order();
    order75.setCardId(testOtherCardId);
    order75.setPrice(testOrder4Price);
    order75.setProductId(product13.getId());
    order75.setUsed(true);
    order75.setCreatedAt(testDate14);
    order75.setShopId(shop3.getId());
    order75.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order75);
    Order order76 = new Order();
    order76.setCardId(testOtherCardId);
    order76.setPrice(testOrder4Price);
    order76.setProductId(product13.getId());
    order76.setUsed(true);
    order76.setCreatedAt(testDate14);
    order76.setShopId(shop3.getId());
    order76.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order76);
    Order order77 = new Order();
    order77.setCardId(testOtherCardId);
    order77.setPrice(testOrder4Price);
    order77.setProductId(product13.getId());
    order77.setUsed(true);
    order77.setCreatedAt(testDate14);
    order77.setShopId(shop3.getId());
    order77.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order77);
    Order order78 = new Order();
    order78.setCardId(testOtherCardId);
    order78.setPrice(testOrder4Price);
    order78.setProductId(product13.getId());
    order78.setUsed(true);
    order78.setCreatedAt(testDate14);
    order78.setShopId(shop3.getId());
    order78.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order78);
    Order order79 = new Order();
    order79.setCardId(testOtherCardId);
    order79.setPrice(testOrder4Price);
    order79.setProductId(product13.getId());
    order79.setUsed(true);
    order79.setCreatedAt(testDate14);
    order79.setShopId(shop3.getId());
    order79.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order79);
    Order order80 = new Order();
    order80.setCardId(testOtherCardId);
    order80.setPrice(testOrder4Price);
    order80.setProductId(product13.getId());
    order80.setUsed(true);
    order80.setCreatedAt(testDate14);
    order80.setShopId(shop3.getId());
    order80.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order80);
    Order order81 = new Order();
    order81.setCardId(testOtherCardId);
    order81.setPrice(testOrder4Price);
    order81.setProductId(product14.getId());
    order81.setUsed(true);
    order81.setCreatedAt(testDate15);
    order81.setShopId(shop1.getId());
    order81.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order81);
    Order order82 = new Order();
    order82.setCardId(testOtherCardId);
    order82.setPrice(testOrder4Price);
    order82.setProductId(product14.getId());
    order82.setUsed(true);
    order82.setCreatedAt(testDate15);
    order82.setShopId(shop1.getId());
    order82.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order82);
    Order order83 = new Order();
    order83.setCardId(testOtherCardId);
    order83.setPrice(testOrder4Price);
    order83.setProductId(product14.getId());
    order83.setUsed(true);
    order83.setCreatedAt(testDate15);
    order83.setShopId(shop1.getId());
    order83.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order83);
    Order order84 = new Order();
    order84.setCardId(testOtherCardId);
    order84.setPrice(testOrder4Price);
    order84.setProductId(product14.getId());
    order84.setUsed(true);
    order84.setCreatedAt(testDate15);
    order84.setShopId(shop1.getId());
    order84.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order84);
    Order order85 = new Order();
    order85.setCardId(testOtherCardId);
    order85.setPrice(testOrder4Price);
    order85.setProductId(product14.getId());
    order85.setUsed(true);
    order85.setCreatedAt(testDate15);
    order85.setShopId(shop1.getId());
    order85.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order85);
    Order order86 = new Order();
    order86.setCardId(testOtherCardId);
    order86.setPrice(testOrder4Price);
    order86.setProductId(product14.getId());
    order86.setUsed(true);
    order86.setCreatedAt(testDate15);
    order86.setShopId(shop1.getId());
    order86.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order86);
    Order order87 = new Order();
    order87.setCardId(testOtherCardId);
    order87.setPrice(testOrder4Price);
    order87.setProductId(product14.getId());
    order87.setUsed(true);
    order87.setCreatedAt(testDate15);
    order87.setShopId(shop1.getId());
    order87.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order87);
    Order order88 = new Order();
    order88.setCardId(testOtherCardId);
    order88.setPrice(testOrder4Price);
    order88.setProductId(product14.getId());
    order88.setUsed(true);
    order88.setCreatedAt(testDate15);
    order88.setShopId(shop1.getId());
    order88.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order88);
    Order order89 = new Order();
    order89.setCardId(testOtherCardId);
    order89.setPrice(testOrder4Price);
    order89.setProductId(product14.getId());
    order89.setUsed(true);
    order89.setCreatedAt(testDate15);
    order89.setShopId(shop1.getId());
    order89.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order89);
    Order order90 = new Order();
    order90.setCardId(testOtherCardId);
    order90.setPrice(testOrder4Price);
    order90.setProductId(product14.getId());
    order90.setUsed(true);
    order90.setCreatedAt(testDate15);
    order90.setShopId(shop1.getId());
    order90.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order90);
    Order order91 = new Order();
    order91.setCardId(testOtherCardId);
    order91.setPrice(testOrder4Price);
    order91.setProductId(product14.getId());
    order91.setUsed(true);
    order91.setCreatedAt(testDate15);
    order91.setShopId(shop1.getId());
    order91.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order91);
    Order order92 = new Order();
    order92.setCardId(testOtherCardId);
    order92.setPrice(testOrder4Price);
    order92.setProductId(product14.getId());
    order92.setUsed(true);
    order92.setCreatedAt(testDate15);
    order92.setShopId(shop1.getId());
    order92.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order92);
    Order order93 = new Order();
    order93.setCardId(testOtherCardId);
    order93.setPrice(testOrder4Price);
    order93.setProductId(product14.getId());
    order93.setUsed(true);
    order93.setCreatedAt(testDate15);
    order93.setShopId(shop1.getId());
    order93.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order93);
    Order order94 = new Order();
    order94.setCardId(testOtherCardId);
    order94.setPrice(testOrder4Price);
    order94.setProductId(product15.getId());
    order94.setUsed(true);
    order94.setCreatedAt(testDate16);
    order94.setShopId(shop1.getId());
    order94.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order94);
    Order order95 = new Order();
    order95.setCardId(testOtherCardId);
    order95.setPrice(testOrder4Price);
    order95.setProductId(product15.getId());
    order95.setUsed(true);
    order95.setCreatedAt(testDate16);
    order95.setShopId(shop1.getId());
    order95.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order95);
    Order order96 = new Order();
    order96.setCardId(testOtherCardId);
    order96.setPrice(testOrder4Price);
    order96.setProductId(product15.getId());
    order96.setUsed(true);
    order96.setCreatedAt(testDate16);
    order96.setShopId(shop1.getId());
    order96.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order96);
    Order order97 = new Order();
    order97.setCardId(testOtherCardId);
    order97.setPrice(testOrder4Price);
    order97.setProductId(product15.getId());
    order97.setUsed(true);
    order97.setCreatedAt(testDate16);
    order97.setShopId(shop1.getId());
    order97.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order97);
    Order order98 = new Order();
    order98.setCardId(testOtherCardId);
    order98.setPrice(testOrder4Price);
    order98.setProductId(product15.getId());
    order98.setUsed(true);
    order98.setCreatedAt(testDate16);
    order98.setShopId(shop1.getId());
    order98.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order98);
    Order order99 = new Order();
    order99.setCardId(testOtherCardId);
    order99.setPrice(testOrder4Price);
    order99.setProductId(product15.getId());
    order99.setUsed(true);
    order99.setCreatedAt(testDate16);
    order99.setShopId(shop1.getId());
    order99.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order99);
    Order order100 = new Order();
    order100.setCardId(testOtherCardId);
    order100.setPrice(testOrder4Price);
    order100.setProductId(product15.getId());
    order100.setUsed(true);
    order100.setCreatedAt(testDate16);
    order100.setShopId(shop1.getId());
    order100.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order100);
    Order order101 = new Order();
    order101.setCardId(testOtherCardId);
    order101.setPrice(testOrder4Price);
    order101.setProductId(product15.getId());
    order101.setUsed(true);
    order101.setCreatedAt(testDate16);
    order101.setShopId(shop1.getId());
    order101.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order101);
    Order order102 = new Order();
    order102.setCardId(testOtherCardId);
    order102.setPrice(testOrder4Price);
    order102.setProductId(product15.getId());
    order102.setUsed(true);
    order102.setCreatedAt(testDate16);
    order102.setShopId(shop1.getId());
    order102.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order102);
    Order order103 = new Order();
    order103.setCardId(testOtherCardId);
    order103.setPrice(testOrder4Price);
    order103.setProductId(product15.getId());
    order103.setUsed(true);
    order103.setCreatedAt(testDate16);
    order103.setShopId(shop1.getId());
    order103.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order103);
    Order order104 = new Order();
    order104.setCardId(testOtherCardId);
    order104.setPrice(testOrder4Price);
    order104.setProductId(product15.getId());
    order104.setUsed(true);
    order104.setCreatedAt(testDate16);
    order104.setShopId(shop1.getId());
    order104.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order104);
    Order order105 = new Order();
    order105.setCardId(testOtherCardId);
    order105.setPrice(testOrder4Price);
    order105.setProductId(product15.getId());
    order105.setUsed(true);
    order105.setCreatedAt(testDate16);
    order105.setShopId(shop1.getId());
    order105.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order105);
    Order order106 = new Order();
    order106.setCardId(testOtherCardId);
    order106.setPrice(testOrder4Price);
    order106.setProductId(product15.getId());
    order106.setUsed(true);
    order106.setCreatedAt(testDate16);
    order106.setShopId(shop1.getId());
    order106.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order106);
    Order order107 = new Order();
    order107.setCardId(testOtherCardId);
    order107.setPrice(testOrder4Price);
    order107.setProductId(product15.getId());
    order107.setUsed(true);
    order107.setCreatedAt(testDate16);
    order107.setShopId(shop1.getId());
    order107.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order107);
    Order order108 = new Order();
    order108.setCardId(testOtherCardId);
    order108.setPrice(testOrder4Price);
    order108.setProductId(product16.getId());
    order108.setUsed(true);
    order108.setCreatedAt(testDate17);
    order108.setShopId(shop3.getId());
    order108.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order108);
    Order order109 = new Order();
    order109.setCardId(testOtherCardId);
    order109.setPrice(testOrder4Price);
    order109.setProductId(product16.getId());
    order109.setUsed(true);
    order109.setCreatedAt(testDate17);
    order109.setShopId(shop3.getId());
    order109.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order109);
    Order order110 = new Order();
    order110.setCardId(testOtherCardId);
    order110.setPrice(testOrder4Price);
    order110.setProductId(product16.getId());
    order110.setUsed(true);
    order110.setCreatedAt(testDate17);
    order110.setShopId(shop3.getId());
    order110.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order110);
    Order order111 = new Order();
    order111.setCardId(testOtherCardId);
    order111.setPrice(testOrder4Price);
    order111.setProductId(product16.getId());
    order111.setUsed(true);
    order111.setCreatedAt(testDate17);
    order111.setShopId(shop3.getId());
    order111.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order111);
    Order order112 = new Order();
    order112.setCardId(testOtherCardId);
    order112.setPrice(testOrder4Price);
    order112.setProductId(product16.getId());
    order112.setUsed(true);
    order112.setCreatedAt(testDate17);
    order112.setShopId(shop3.getId());
    order112.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order112);
    Order order113 = new Order();
    order113.setCardId(testOtherCardId);
    order113.setPrice(testOrder4Price);
    order113.setProductId(product16.getId());
    order113.setUsed(true);
    order113.setCreatedAt(testDate17);
    order113.setShopId(shop3.getId());
    order113.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order113);
    Order order114 = new Order();
    order114.setCardId(testOtherCardId);
    order114.setPrice(testOrder4Price);
    order114.setProductId(product16.getId());
    order114.setUsed(true);
    order114.setCreatedAt(testDate17);
    order114.setShopId(shop3.getId());
    order114.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order114);
    Order order115 = new Order();
    order115.setCardId(testOtherCardId);
    order115.setPrice(testOrder4Price);
    order115.setProductId(product16.getId());
    order115.setUsed(true);
    order115.setCreatedAt(testDate17);
    order115.setShopId(shop3.getId());
    order115.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order115);
    Order order116 = new Order();
    order116.setCardId(testOtherCardId);
    order116.setPrice(testOrder4Price);
    order116.setProductId(product16.getId());
    order116.setUsed(true);
    order116.setCreatedAt(testDate17);
    order116.setShopId(shop3.getId());
    order116.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order116);
    Order order117 = new Order();
    order117.setCardId(testOtherCardId);
    order117.setPrice(testOrder4Price);
    order117.setProductId(product16.getId());
    order117.setUsed(true);
    order117.setCreatedAt(testDate17);
    order117.setShopId(shop3.getId());
    order117.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order117);
    Order order118 = new Order();
    order118.setCardId(testOtherCardId);
    order118.setPrice(testOrder4Price);
    order118.setProductId(product16.getId());
    order118.setUsed(true);
    order118.setCreatedAt(testDate17);
    order118.setShopId(shop3.getId());
    order118.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order118);
    Order order119 = new Order();
    order119.setCardId(testOtherCardId);
    order119.setPrice(testOrder4Price);
    order119.setProductId(product16.getId());
    order119.setUsed(true);
    order119.setCreatedAt(testDate17);
    order119.setShopId(shop3.getId());
    order119.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order119);
    Order order120 = new Order();
    order120.setCardId(testOtherCardId);
    order120.setPrice(testOrder4Price);
    order120.setProductId(product16.getId());
    order120.setUsed(true);
    order120.setCreatedAt(testDate17);
    order120.setShopId(shop3.getId());
    order120.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order120);
    Order order121 = new Order();
    order121.setCardId(testOtherCardId);
    order121.setPrice(testOrder4Price);
    order121.setProductId(product16.getId());
    order121.setUsed(true);
    order121.setCreatedAt(testDate17);
    order121.setShopId(shop3.getId());
    order121.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order121);
    Order order122 = new Order();
    order122.setCardId(testOtherCardId);
    order122.setPrice(testOrder4Price);
    order122.setProductId(product16.getId());
    order122.setUsed(true);
    order122.setCreatedAt(testDate17);
    order122.setShopId(shop3.getId());
    order122.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order122);
    Order order123 = new Order();
    order123.setCardId(testOtherCardId);
    order123.setPrice(testOrder4Price);
    order123.setProductId(product17.getId());
    order123.setUsed(true);
    order123.setCreatedAt(testDate18);
    order123.setShopId(shop3.getId());
    order123.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order123);
    Order order124 = new Order();
    order124.setCardId(testOtherCardId);
    order124.setPrice(testOrder4Price);
    order124.setProductId(product17.getId());
    order124.setUsed(true);
    order124.setCreatedAt(testDate18);
    order124.setShopId(shop3.getId());
    order124.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order124);
    Order order125 = new Order();
    order125.setCardId(testOtherCardId);
    order125.setPrice(testOrder4Price);
    order125.setProductId(product17.getId());
    order125.setUsed(true);
    order125.setCreatedAt(testDate18);
    order125.setShopId(shop3.getId());
    order125.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order125);
    Order order126 = new Order();
    order126.setCardId(testOtherCardId);
    order126.setPrice(testOrder4Price);
    order126.setProductId(product17.getId());
    order126.setUsed(true);
    order126.setCreatedAt(testDate18);
    order126.setShopId(shop3.getId());
    order126.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order126);
    Order order127 = new Order();
    order127.setCardId(testOtherCardId);
    order127.setPrice(testOrder4Price);
    order127.setProductId(product17.getId());
    order127.setUsed(true);
    order127.setCreatedAt(testDate18);
    order127.setShopId(shop3.getId());
    order127.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order127);
    Order order128 = new Order();
    order128.setCardId(testOtherCardId);
    order128.setPrice(testOrder4Price);
    order128.setProductId(product17.getId());
    order128.setUsed(true);
    order128.setCreatedAt(testDate18);
    order128.setShopId(shop3.getId());
    order128.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order128);
    Order order129 = new Order();
    order129.setCardId(testOtherCardId);
    order129.setPrice(testOrder4Price);
    order129.setProductId(product17.getId());
    order129.setUsed(true);
    order129.setCreatedAt(testDate18);
    order129.setShopId(shop3.getId());
    order129.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order129);
    Order order130 = new Order();
    order130.setCardId(testOtherCardId);
    order130.setPrice(testOrder4Price);
    order130.setProductId(product17.getId());
    order130.setUsed(true);
    order130.setCreatedAt(testDate18);
    order130.setShopId(shop3.getId());
    order130.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order130);
    Order order131 = new Order();
    order131.setCardId(testOtherCardId);
    order131.setPrice(testOrder4Price);
    order131.setProductId(product17.getId());
    order131.setUsed(true);
    order131.setCreatedAt(testDate18);
    order131.setShopId(shop3.getId());
    order131.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order131);
    Order order132 = new Order();
    order132.setCardId(testOtherCardId);
    order132.setPrice(testOrder4Price);
    order132.setProductId(product17.getId());
    order132.setUsed(true);
    order132.setCreatedAt(testDate18);
    order132.setShopId(shop3.getId());
    order132.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order132);
    Order order133 = new Order();
    order133.setCardId(testOtherCardId);
    order133.setPrice(testOrder4Price);
    order133.setProductId(product17.getId());
    order133.setUsed(true);
    order133.setCreatedAt(testDate18);
    order133.setShopId(shop3.getId());
    order133.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order133);
    Order order134 = new Order();
    order134.setCardId(testOtherCardId);
    order134.setPrice(testOrder4Price);
    order134.setProductId(product17.getId());
    order134.setUsed(true);
    order134.setCreatedAt(testDate18);
    order134.setShopId(shop3.getId());
    order134.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order134);
    Order order135 = new Order();
    order135.setCardId(testOtherCardId);
    order135.setPrice(testOrder4Price);
    order135.setProductId(product17.getId());
    order135.setUsed(true);
    order135.setCreatedAt(testDate18);
    order135.setShopId(shop3.getId());
    order135.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order135);
    Order order136 = new Order();
    order136.setCardId(testOtherCardId);
    order136.setPrice(testOrder4Price);
    order136.setProductId(product17.getId());
    order136.setUsed(true);
    order136.setCreatedAt(testDate18);
    order136.setShopId(shop3.getId());
    order136.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order136);
    Order order137 = new Order();
    order137.setCardId(testOtherCardId);
    order137.setPrice(testOrder4Price);
    order137.setProductId(product17.getId());
    order137.setUsed(true);
    order137.setCreatedAt(testDate18);
    order137.setShopId(shop3.getId());
    order137.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order137);
    Order order138 = new Order();
    order138.setCardId(testOtherCardId);
    order138.setPrice(testOrder4Price);
    order138.setProductId(product17.getId());
    order138.setUsed(true);
    order138.setCreatedAt(testDate18);
    order138.setShopId(shop3.getId());
    order138.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order138);
    BlockedProduct blockedProduct1 = new BlockedProduct();
    blockedProduct1.setExpiredAt(LocalDate.now().plusYears(1));
    blockedProduct1.setProductId(product3.getId());
    mongoTemplate.save(blockedProduct1);
    BlockedProduct blockedProduct2 = new BlockedProduct();
    blockedProduct2.setExpiredAt(LocalDate.now().plusYears(1));
    blockedProduct2.setProductId(product6.getId());
    mongoTemplate.save(blockedProduct2);
    BlockedProduct blockedProduct3 = new BlockedProduct();
    blockedProduct3.setExpiredAt(LocalDate.now().plusYears(1));
    blockedProduct3.setProductId(product12.getId());
    mongoTemplate.save(blockedProduct3);
    BlockedProduct blockedProduct4 = new BlockedProduct();
    blockedProduct4.setExpiredAt(LocalDate.now().plusYears(1));
    blockedProduct4.setProductId(product16.getId());
    mongoTemplate.save(blockedProduct4);
    productDTO1 = new ProductDTO();
    productDTO1.setProductId(product1.getId().toString());
    productDTO1.setProductName(product1.getName());
    productDTO1.setActive(true);
    productDTO1.setPrice(product1.getPrice());
    productDTO1.setDescription(product1.getDescription());
    productDTO1.setShopId(product1.getShopId());
    productDTO1.setQuantityPromotion(promotion1.getQuantity());
    productDTO1.setNewPricePromotion(promotion1.getNewPrice());
    productDTO1.setProductImageUrl(product1.getImageUrl());
    productDTO1.setStartAtPromotion(promotion1.getStartAt());
    productDTO1.setExpiredAtPromotion(promotion1.getExpiredAt());
    productDTO2 = new ProductDTO();
    productDTO2.setProductName(product2.getName());
    productDTO2.setProductId(product2.getId().toString());
    productDTO2.setActive(true);
    productDTO2.setPrice(product2.getPrice());
    productDTO2.setDescription(product2.getDescription());
    productDTO2.setShopId(product2.getShopId());
    productDTO2.setQuantityPromotion(promotion2.getQuantity());
    productDTO2.setNewPricePromotion(promotion2.getNewPrice());
    productDTO2.setProductImageUrl(product2.getImageUrl());
    productDTO2.setStartAtPromotion(promotion2.getStartAt());
    productDTO2.setExpiredAtPromotion(promotion2.getExpiredAt());
    productDTO3 = new ProductDTO();
    productDTO3.setProductId(product3.getId().toString());
    productDTO3.setProductName(product3.getName());
    productDTO3.setActive(false);
    productDTO3.setPrice(product3.getPrice());
    productDTO3.setDescription(product3.getDescription());
    productDTO3.setShopId(product3.getShopId());
    productDTO3.setQuantityPromotion(null);
    productDTO3.setNewPricePromotion(0);
    productDTO3.setProductImageUrl(product3.getImageUrl());
    productDTO3.setStartAtPromotion(null);
    productDTO3.setExpiredAtPromotion(null);
    productDTO4 = new ProductDTO();
    productDTO4.setProductId(product4.getId().toString());
    productDTO4.setProductName(product4.getName());
    productDTO4.setActive(true);
    productDTO4.setPrice(product4.getPrice());
    productDTO4.setDescription(product4.getDescription());
    productDTO4.setShopId(product4.getShopId());
    productDTO4.setQuantityPromotion(promotion3.getQuantity() - 2);
    productDTO4.setNewPricePromotion(promotion3.getNewPrice());
    productDTO4.setProductImageUrl(product4.getImageUrl());
    productDTO4.setStartAtPromotion(promotion3.getStartAt());
    productDTO4.setExpiredAtPromotion(promotion3.getExpiredAt());
    productDTO5 = new ProductDTO();
    productDTO5.setProductId(product5.getId().toString());
    productDTO5.setProductName(product5.getName());
    productDTO5.setActive(true);
    productDTO5.setPrice(product5.getPrice());
    productDTO5.setDescription(product5.getDescription());
    productDTO5.setShopId(product5.getShopId());
    productDTO5.setQuantityPromotion(null);
    productDTO5.setNewPricePromotion(0);
    productDTO5.setProductImageUrl(product5.getImageUrl());
    productDTO5.setStartAtPromotion(null);
    productDTO5.setExpiredAtPromotion(null);
    productDTO7 = new ProductDTO();
    productDTO7.setProductId(product7.getId().toString());
    productDTO7.setProductName(product7.getName());
    productDTO7.setActive(true);
    productDTO7.setPrice(product7.getPrice());
    productDTO7.setDescription(product7.getDescription());
    productDTO7.setShopId(product7.getShopId());
    productDTO7.setQuantityPromotion(promotion4.getQuantity());
    productDTO7.setNewPricePromotion(promotion4.getNewPrice());
    productDTO7.setProductImageUrl(product7.getImageUrl());
    productDTO7.setStartAtPromotion(promotion4.getStartAt());
    productDTO7.setExpiredAtPromotion(promotion4.getExpiredAt());
    productDTO8 = new ProductDTO();
    productDTO8.setProductId(product8.getId().toString());
    productDTO8.setProductName(product8.getName());
    productDTO8.setActive(true);
    productDTO8.setPrice(product8.getPrice());
    productDTO8.setDescription(product8.getDescription());
    productDTO8.setShopId(product8.getShopId());
    productDTO8.setQuantityPromotion(null);
    productDTO8.setNewPricePromotion(0);
    productDTO8.setProductImageUrl(product8.getImageUrl());
    productDTO8.setStartAtPromotion(null);
    productDTO8.setExpiredAtPromotion(null);
    productDTO9 = new ProductDTO();
    productDTO9.setProductId(product9.getId().toString());
    productDTO9.setProductName(product9.getName());
    productDTO9.setActive(true);
    productDTO9.setPrice(product9.getPrice());
    productDTO9.setDescription(product9.getDescription());
    productDTO9.setShopId(product9.getShopId());
    productDTO9.setQuantityPromotion(null);
    productDTO9.setNewPricePromotion(0);
    productDTO9.setProductImageUrl(product9.getImageUrl());
    productDTO9.setStartAtPromotion(null);
    productDTO9.setExpiredAtPromotion(null);
    productDTO10 = new ProductDTO();
    productDTO10.setProductId(product10.getId().toString());
    productDTO10.setProductName(product10.getName());
    productDTO10.setActive(true);
    productDTO10.setPrice(product10.getPrice());
    productDTO10.setDescription(product10.getDescription());
    productDTO10.setShopId(product10.getShopId());
    productDTO10.setQuantityPromotion(null);
    productDTO10.setNewPricePromotion(0);
    productDTO10.setProductImageUrl(product10.getImageUrl());
    productDTO10.setStartAtPromotion(null);
    productDTO10.setExpiredAtPromotion(null);
    productDTO11 = new ProductDTO();
    productDTO11.setProductId(product11.getId().toString());
    productDTO11.setProductName(product11.getName());
    productDTO11.setActive(true);
    productDTO11.setPrice(product11.getPrice());
    productDTO11.setDescription(product11.getDescription());
    productDTO11.setShopId(product11.getShopId());
    productDTO11.setQuantityPromotion(null);
    productDTO11.setNewPricePromotion(0);
    productDTO11.setProductImageUrl(product11.getImageUrl());
    productDTO11.setStartAtPromotion(null);
    productDTO11.setExpiredAtPromotion(null);
    productDTO13 = new ProductDTO();
    productDTO13.setProductId(product13.getId().toString());
    productDTO13.setProductName(product13.getName());
    productDTO13.setActive(true);
    productDTO13.setPrice(product13.getPrice());
    productDTO13.setDescription(product13.getDescription());
    productDTO13.setShopId(product13.getShopId());
    productDTO13.setQuantityPromotion(null);
    productDTO13.setNewPricePromotion(0);
    productDTO13.setProductImageUrl(product13.getImageUrl());
    productDTO13.setStartAtPromotion(null);
    productDTO13.setExpiredAtPromotion(null);
    productDTO14 = new ProductDTO();
    productDTO14.setProductId(product14.getId().toString());
    productDTO14.setProductName(product14.getName());
    productDTO14.setActive(true);
    productDTO14.setPrice(product14.getPrice());
    productDTO14.setDescription(product14.getDescription());
    productDTO14.setShopId(product14.getShopId());
    productDTO14.setQuantityPromotion(null);
    productDTO14.setNewPricePromotion(0);
    productDTO14.setProductImageUrl(product14.getImageUrl());
    productDTO14.setStartAtPromotion(null);
    productDTO14.setExpiredAtPromotion(null);
    productDTO15 = new ProductDTO();
    productDTO15.setProductId(product15.getId().toString());
    productDTO15.setProductName(product15.getName());
    productDTO15.setActive(true);
    productDTO15.setPrice(product15.getPrice());
    productDTO15.setDescription(product15.getDescription());
    productDTO15.setShopId(product15.getShopId());
    productDTO15.setQuantityPromotion(null);
    productDTO15.setNewPricePromotion(0);
    productDTO15.setProductImageUrl(product15.getImageUrl());
    productDTO15.setStartAtPromotion(null);
    productDTO15.setExpiredAtPromotion(null);
    productDTO17 = new ProductDTO();
    productDTO17.setProductId(product17.getId().toString());
    productDTO17.setProductName(product17.getName());
    productDTO17.setActive(true);
    productDTO17.setPrice(product17.getPrice());
    productDTO17.setDescription(product17.getDescription());
    productDTO17.setShopId(product17.getShopId());
    productDTO17.setQuantityPromotion(null);
    productDTO17.setNewPricePromotion(0);
    productDTO17.setProductImageUrl(product17.getImageUrl());
    productDTO17.setStartAtPromotion(null);
    productDTO17.setExpiredAtPromotion(null);
    productAtCardDTO1 = new ProductAtCardDTO();
    productAtCardDTO1.setCount(testCount1);
    productAtCardDTO1.setPrice(testOrder4Price);
    productAtCardDTO1.setProductName(product10.getName());
    productAtCardDTO1.setProductImageUrl(product10.getImageUrl());
    productAtCardDTO1.setDescription(product10.getDescription());
    productAtCardDTO1.setShopImageUrl(shop3.getImageUrl());
    productAtCardDTO1.setShopName(shop3.getName());
    productAtCardDTO2 = new ProductAtCardDTO();
    productAtCardDTO2.setCount(testCount2);
    productAtCardDTO2.setPrice(testOrder3Price);
    productAtCardDTO2.setProductName(product6.getName());
    productAtCardDTO2.setProductImageUrl(product6.getImageUrl());
    productAtCardDTO2.setDescription(product6.getDescription());
    productAtCardDTO2.setShopImageUrl(shop3.getImageUrl());
    productAtCardDTO2.setShopName(shop3.getName());
    productAtCardDTO3 = new ProductAtCardDTO();
    productAtCardDTO3.setCount(testCount3);
    productAtCardDTO3.setPrice(testOrder1Price);
    productAtCardDTO3.setProductName(this.product1.getName());
    productAtCardDTO3.setProductImageUrl(this.product1.getImageUrl());
    productAtCardDTO3.setDescription(this.product1.getDescription());
    productAtCardDTO3.setShopImageUrl(shop1.getImageUrl());
    productAtCardDTO3.setShopName(shop1.getName());
    userDTO1 = new UserDTO();
    userDTO1.setActive(true);
    userDTO1.setBanned(false);
    userDTO1.setEmail("test@test1");
    userDTO1.setPhone(TEST_PHONE);
    userDTO1.setId(shop1.getId().toString());
    userDTO1.setRole(Role.ROLE_SHOP.name());
    userDTO1.setFirstName("firstNameShop1");
    userDTO1.setLastName("lastNameShop1");
    userDTO1.setShopName(TEST_SHOP_NAME);
    userDTO1.setRestricted(null);
    userDTO2 = new UserDTO();
    userDTO2.setId(shop2.getId().toString());
    userDTO2.setPhone("+48235324342423");
    userDTO2.setEmail(TEST_EMAIL);
    userDTO2.setRole(Role.ROLE_SHOP.name());
    userDTO2.setBanned(false);
    userDTO2.setActive(true);
    userDTO2.setFirstName("firstNameShop2");
    userDTO2.setLastName("lastNameShop2");
    userDTO2.setShopName("shop2");
    userDTO2.setRestricted(null);
    userDTO3 = new UserDTO();
    userDTO3.setId(shop3.getId().toString());
    userDTO3.setPhone(TEST_PHONE3);
    userDTO3.setEmail("test@test3");
    userDTO3.setRole(Role.ROLE_SHOP.name());
    userDTO3.setBanned(false);
    userDTO3.setActive(true);
    userDTO3.setFirstName("firstNameShop3");
    userDTO3.setLastName("lastNameShop3");
    userDTO3.setShopName("shop3");
    userDTO3.setRestricted(null);
    userDTO4 = new UserDTO();
    userDTO4.setId(shop4.getId().toString());
    userDTO4.setPhone("+48121347392923");
    userDTO4.setEmail("test@test4");
    userDTO4.setRole(Role.ROLE_SHOP.name());
    userDTO4.setBanned(false);
    userDTO4.setActive(true);
    userDTO4.setFirstName("firstNameShop4");
    userDTO4.setLastName("lastNameShop4");
    userDTO4.setShopName("shop4");
    userDTO4.setRestricted(null);
    userDTO5 = new UserDTO();
    userDTO5.setId(user1.getId().toString());
    userDTO5.setPhone("+48535243252345");
    userDTO5.setEmail("test@test5");
    userDTO5.setRole(Role.ROLE_USER.name());
    userDTO5.setBanned(false);
    userDTO5.setActive(false);
    userDTO5.setFirstName("firstNameUser1");
    userDTO5.setLastName("lastNameUser1");
    userDTO5.setShopName(null);
    userDTO5.setRestricted(true);
    userDTO6 = new UserDTO();
    userDTO6.setId(user2.getId().toString());
    userDTO6.setPhone("+481234123452314");
    userDTO6.setEmail("test@test6");
    userDTO6.setRole(Role.ROLE_USER.name());
    userDTO6.setBanned(true);
    userDTO6.setActive(true);
    userDTO6.setFirstName("firstNameUser2");
    userDTO6.setLastName("lastNameUser2");
    userDTO6.setShopName(null);
    userDTO6.setRestricted(false);
    userDTO7 = new UserDTO();
    userDTO7.setId(user3.getId().toString());
    userDTO7.setPhone("+4853543535435");
    userDTO7.setEmail("test@test7");
    userDTO7.setRole(Role.ROLE_ADMIN.name());
    userDTO7.setBanned(true);
    userDTO7.setActive(false);
    userDTO7.setFirstName(TEST_FIRST_NAME_USER);
    userDTO7.setLastName(TEST_LAST_NAME_USER);
    userDTO7.setShopName(null);
    userDTO7.setRestricted(true);
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
    reservedProductsRepository.deleteAll();
    orderRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  public void
      shouldReturnPageOfProductDTOAtGetProductsOwnerWhenCountFieldAndIsDescendingWithoutText() {
    PageProductsDTO page = new PageProductsDTO();
    page.setProducts(List.of(productDTO15, productDTO14, productDTO1, productDTO3));
    page.setMaxPage(1);

    assertEquals(
        page,
        aggregationRepository.getProducts(TEST_PHONE, 0, COUNT_FIELD, true, "", "", "", false));
  }

  @Test
  public void
      shouldReturnPageOfProductDTOAtGetProductsOwnerWhenCountFieldAndIsAscendingWithoutText() {
    PageProductsDTO page = new PageProductsDTO();
    page.setProducts(List.of(productDTO3, productDTO1, productDTO14, productDTO15));
    page.setMaxPage(1);

    assertEquals(
        page,
        aggregationRepository.getProducts(TEST_PHONE, 0, COUNT_FIELD, false, "", "", "", false));
  }

  @Test
  public void
      shouldReturnPageOfProductDTOAtGetProductsOwnerWhenDateFieldAndIsDescendingWithoutText() {
    PageProductsDTO page = new PageProductsDTO();
    page.setProducts(List.of(productDTO14, productDTO15, productDTO1, productDTO3));
    page.setMaxPage(1);

    assertEquals(
        page,
        aggregationRepository.getProducts(TEST_PHONE, 0, DATE_FIELD, true, "", "", "", false));
  }

  @Test
  public void
      shouldReturnPageOfProductDTOAtGetProductsOwnerWhenDateFieldAndIsAscendingWithoutText() {
    PageProductsDTO page = new PageProductsDTO();
    page.setProducts(List.of(productDTO3, productDTO1, productDTO15, productDTO14));
    page.setMaxPage(1);

    assertEquals(
        page,
        aggregationRepository.getProducts(TEST_PHONE, 0, DATE_FIELD, false, "", "", "", false));
  }

  @Test
  public void
      shouldReturnPageOfProductDTOAtGetProductsOwnerWhenPriceFieldAndIsDescendingWithoutText() {
    PageProductsDTO page = new PageProductsDTO();
    page.setProducts(List.of(productDTO3, productDTO1, productDTO14, productDTO15));
    page.setMaxPage(1);

    assertEquals(
        page,
        aggregationRepository.getProducts(TEST_PHONE, 0, PRICE_FIELD, true, "", "", "", false));
  }

  @Test
  public void
      shouldReturnPageOfProductDTOAtGetProductsOwnerWhenPriceFieldAndIsAscendingWithoutText() {
    PageProductsDTO page = new PageProductsDTO();
    page.setProducts(List.of(productDTO15, productDTO14, productDTO1, productDTO3));
    page.setMaxPage(1);

    assertEquals(
        page,
        aggregationRepository.getProducts(TEST_PHONE, 0, PRICE_FIELD, false, "", "", "", false));
  }

  @Test
  public void
      shouldReturnPageOfProductDTOAtGetProductsOwnerWhenAddedFieldAndIsDescendingWithoutText() {
    PageProductsDTO page = new PageProductsDTO();
    page.setProducts(List.of(productDTO14, productDTO15, productDTO3, productDTO1));
    page.setMaxPage(1);

    assertEquals(
        page,
        aggregationRepository.getProducts(TEST_PHONE, 0, ADDED_FIELD, true, "", "", "", false));
  }

  @Test
  public void
      shouldReturnPageOfProductDTOAtGetProductsOwnerWhenAddedFieldAndIsAscendingWithoutText() {
    PageProductsDTO page = new PageProductsDTO();
    page.setProducts(List.of(productDTO1, productDTO3, productDTO15, productDTO14));
    page.setMaxPage(1);

    assertEquals(
        page,
        aggregationRepository.getProducts(TEST_PHONE, 0, ADDED_FIELD, false, "", "", "", false));
  }

  @Test
  public void
      shouldReturnPageOfProductDTOAtGetProductsOwnerWhenCountFieldAndIsDescendingWithText() {
    PageProductsDTO page = new PageProductsDTO();
    page.setProducts(List.of(productDTO1, productDTO3));
    page.setMaxPage(1);

    assertEquals(
        page,
        aggregationRepository.getProducts(
            TEST_PHONE, 0, COUNT_FIELD, true, TEST_PRODUCT_NAME, "", "", false));
  }

  @Test
  public void shouldReturnPageOfProductDTOAtGetProductsWhenCountFieldAndIsDescendingWithoutText() {
    PageProductsDTO expectedPage = new PageProductsDTO();
    expectedPage.setProducts(
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
            productDTO4));
    expectedPage.setMaxPage(2);

    PageProductsDTO page =
        aggregationRepository.getProducts(null, 0, COUNT_FIELD, true, "", "", "", true);
    assertEquals(expectedPage.getMaxPage(), page.getMaxPage());
    List<ProductDTO> products = page.getProducts();
    assertTrue(products.containsAll(expectedPage.getProducts()));
  }

  @Test
  public void shouldReturnPageOfProductDTOAtGetProductsWhenCountFieldAndIsAscendingWithoutText() {
    PageProductsDTO expectedPage = new PageProductsDTO();
    expectedPage.setProducts(
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
            productDTO15));
    expectedPage.setMaxPage(2);

    PageProductsDTO page =
        aggregationRepository.getProducts(null, 0, COUNT_FIELD, false, "", "", "", true);
    assertEquals(expectedPage.getMaxPage(), page.getMaxPage());
    List<ProductDTO> products = page.getProducts();
    assertTrue(products.containsAll(expectedPage.getProducts()));
  }

  @Test
  public void shouldReturnPageOfProductDTOAtGetProductsWhenDateFieldAndIsDescendingWithoutText() {
    PageProductsDTO page = new PageProductsDTO();
    page.setProducts(
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
            productDTO1));
    page.setMaxPage(2);

    assertEquals(
        page, aggregationRepository.getProducts(null, 0, DATE_FIELD, true, "", "", "", true));
  }

  @Test
  public void shouldReturnPageOfProductDTOAtGetProductsWhenDateFieldAndIsAscendingWithoutText() {
    PageProductsDTO page = new PageProductsDTO();
    page.setProducts(
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
            productDTO14));
    page.setMaxPage(2);

    assertEquals(
        page, aggregationRepository.getProducts(null, 0, DATE_FIELD, false, "", "", "", true));
  }

  @Test
  public void shouldReturnPageOfProductDTOAtGetProductsWhenPriceFieldAndIsDescendingWithoutText() {
    PageProductsDTO page = new PageProductsDTO();
    page.setProducts(
        List.of(
            productDTO2,
            productDTO1,
            productDTO14,
            productDTO10,
            productDTO17,
            productDTO15,
            productDTO13,
            productDTO11,
            productDTO9,
            productDTO8,
            productDTO5,
            productDTO4));
    page.setMaxPage(2);

    assertEquals(
        page, aggregationRepository.getProducts(null, 0, PRICE_FIELD, true, "", "", "", true));
  }

  @Test
  public void shouldReturnPageOfProductDTOAtGetProductsWhenPriceFieldAndIsAscendingWithoutText() {
    PageProductsDTO page = new PageProductsDTO();
    page.setProducts(
        List.of(
            productDTO7,
            productDTO4,
            productDTO5,
            productDTO8,
            productDTO9,
            productDTO11,
            productDTO13,
            productDTO15,
            productDTO17,
            productDTO10,
            productDTO14,
            productDTO1));
    page.setMaxPage(2);

    assertEquals(
        page, aggregationRepository.getProducts(null, 0, PRICE_FIELD, false, "", "", "", true));
  }

  @Test
  public void shouldReturnPageOfProductDTOAtGetProductsWhenAddedFieldAndIsDescendingWithoutText() {
    PageProductsDTO page = new PageProductsDTO();
    page.setProducts(
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
            productDTO2));
    page.setMaxPage(2);

    assertEquals(
        page, aggregationRepository.getProducts(null, 0, ADDED_FIELD, true, "", "", "", true));
  }

  @Test
  public void shouldReturnPageOfProductDTOAtGetProductsWhenAddedFieldAndIsAscendingWithoutText() {
    PageProductsDTO page = new PageProductsDTO();
    page.setProducts(
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
            productDTO14));
    page.setMaxPage(2);

    assertEquals(
        page, aggregationRepository.getProducts(null, 0, ADDED_FIELD, false, "", "", "", true));
  }

  @Test
  public void
      shouldReturnPageOfProductDTOAtGetProductsWhenCountFieldAndIsDescendingWithoutTextWithCategory() {
    PageProductsDTO page = new PageProductsDTO();
    page.setProducts(List.of(productDTO13, productDTO10, productDTO9, productDTO1, productDTO2));
    page.setMaxPage(1);

    assertEquals(
        page,
        aggregationRepository.getProducts(
            null, 0, COUNT_FIELD, true, "", TEST_CATEGORY_NAME, "", true));
  }

  @Test
  public void
      shouldReturnPageOfProductDTOAtGetProductsWhenCountFieldAndIsDescendingWithoutTextWithShopName() {
    PageProductsDTO page = new PageProductsDTO();
    page.setProducts(List.of(productDTO15, productDTO14, productDTO1));
    page.setMaxPage(1);

    assertEquals(
        page,
        aggregationRepository.getProducts(
            null, 0, COUNT_FIELD, true, "", "", TEST_SHOP_NAME, true));
  }

  @Test
  public void shouldReturnPageOfProductDTOAtGetProductsWhenCountFieldAndIsDescendingWithText() {
    PageProductsDTO page = new PageProductsDTO();
    page.setProducts(List.of(productDTO17, productDTO7, productDTO5, productDTO1));
    page.setMaxPage(1);

    assertEquals(
        page,
        aggregationRepository.getProducts(
            null, 0, COUNT_FIELD, true, TEST_PRODUCT_NAME, "", "", true));
  }

  @Test
  public void shouldReturnListOfProductWithShopDTOAtFindProductsByIdsAndTypeWhenEverythingOk() {
    ProductInfo productInfo1 = new ProductInfo(productDTO1.getProductId(), true);
    ProductInfo productInfo2 = new ProductInfo(productDTO4.getProductId(), true);
    ProductInfo productInfo3 = new ProductInfo(productDTO7.getProductId(), true);
    ProductInfo productInfo4 = new ProductInfo(productDTO7.getProductId(), false);
    ProductInfo productInfo5 = new ProductInfo(productDTO3.getProductId(), false);
    ProductWithShopDTO productWithShopDTO1 = new ProductWithShopDTO();
    productWithShopDTO1.setProductId(productDTO1.getProductId());
    productWithShopDTO1.setProductName(productDTO1.getProductName());
    productWithShopDTO1.setActive(true);
    productWithShopDTO1.setPrice(productDTO1.getPrice());
    productWithShopDTO1.setDescription(productDTO1.getDescription());
    productWithShopDTO1.setShopId(productDTO1.getShopId());
    productWithShopDTO1.setQuantityPromotion(productDTO1.getQuantityPromotion());
    productWithShopDTO1.setNewPricePromotion(productDTO1.getNewPricePromotion());
    productWithShopDTO1.setProductImageUrl(productDTO1.getProductImageUrl());
    productWithShopDTO1.setStartAtPromotion(productDTO1.getStartAtPromotion());
    productWithShopDTO1.setExpiredAtPromotion(productDTO1.getExpiredAtPromotion());
    productWithShopDTO1.setShopName(shop1.getName());
    productWithShopDTO1.setShopImageUrl(shop1.getImageUrl());
    ProductWithShopDTO productWithShopDTO2 = new ProductWithShopDTO();
    productWithShopDTO2.setProductId(productDTO4.getProductId());
    productWithShopDTO2.setProductName(productDTO4.getProductName());
    productWithShopDTO2.setActive(true);
    productWithShopDTO2.setPrice(productDTO4.getPrice());
    productWithShopDTO2.setDescription(productDTO4.getDescription());
    productWithShopDTO2.setShopId(productDTO4.getShopId());
    productWithShopDTO2.setQuantityPromotion(productDTO4.getQuantityPromotion());
    productWithShopDTO2.setNewPricePromotion(productDTO4.getNewPricePromotion());
    productWithShopDTO2.setProductImageUrl(productDTO4.getProductImageUrl());
    productWithShopDTO2.setStartAtPromotion(productDTO4.getStartAtPromotion());
    productWithShopDTO2.setExpiredAtPromotion(productDTO4.getExpiredAtPromotion());
    productWithShopDTO2.setShopName(shop3.getName());
    productWithShopDTO2.setShopImageUrl(shop3.getImageUrl());
    ProductWithShopDTO productWithShopDTO3 = new ProductWithShopDTO();
    productWithShopDTO3.setProductId(productDTO7.getProductId());
    productWithShopDTO3.setProductName(productDTO7.getProductName());
    productWithShopDTO3.setActive(true);
    productWithShopDTO3.setPrice(productDTO7.getPrice());
    productWithShopDTO3.setDescription(productDTO7.getDescription());
    productWithShopDTO3.setShopId(productDTO7.getShopId());
    productWithShopDTO3.setQuantityPromotion(productDTO7.getQuantityPromotion());
    productWithShopDTO3.setNewPricePromotion(productDTO7.getNewPricePromotion());
    productWithShopDTO3.setProductImageUrl(productDTO7.getProductImageUrl());
    productWithShopDTO3.setStartAtPromotion(productDTO7.getStartAtPromotion());
    productWithShopDTO3.setExpiredAtPromotion(productDTO7.getExpiredAtPromotion());
    productWithShopDTO3.setShopName(shop3.getName());
    productWithShopDTO3.setShopImageUrl(shop3.getImageUrl());
    ProductWithShopDTO productWithShopDTO4 = new ProductWithShopDTO();
    productWithShopDTO4.setProductId(productDTO7.getProductId());
    productWithShopDTO4.setProductName(productDTO7.getProductName());
    productWithShopDTO4.setActive(true);
    productWithShopDTO4.setPrice(productDTO7.getPrice());
    productWithShopDTO4.setDescription(productDTO7.getDescription());
    productWithShopDTO4.setShopId(productDTO7.getShopId());
    productWithShopDTO4.setQuantityPromotion(null);
    productWithShopDTO4.setNewPricePromotion(0);
    productWithShopDTO4.setProductImageUrl(productDTO7.getProductImageUrl());
    productWithShopDTO4.setStartAtPromotion(null);
    productWithShopDTO4.setExpiredAtPromotion(null);
    productWithShopDTO4.setShopName(shop3.getName());
    productWithShopDTO4.setShopImageUrl(shop3.getImageUrl());
    ProductWithShopDTO productWithShopDTO5 = new ProductWithShopDTO();
    productWithShopDTO5.setProductId(productDTO3.getProductId());
    productWithShopDTO5.setProductName(productDTO3.getProductName());
    productWithShopDTO5.setActive(true);
    productWithShopDTO5.setPrice(productDTO3.getPrice());
    productWithShopDTO5.setDescription(productDTO3.getDescription());
    productWithShopDTO5.setShopId(productDTO3.getShopId());
    productWithShopDTO5.setQuantityPromotion(null);
    productWithShopDTO5.setNewPricePromotion(0);
    productWithShopDTO5.setProductImageUrl(productDTO3.getProductImageUrl());
    productWithShopDTO5.setStartAtPromotion(null);
    productWithShopDTO5.setExpiredAtPromotion(null);
    productWithShopDTO5.setShopName(shop1.getName());
    productWithShopDTO5.setShopImageUrl(shop1.getImageUrl());

    assertEquals(
        List.of(
            productWithShopDTO1,
            productWithShopDTO5,
            productWithShopDTO2,
            productWithShopDTO3,
            productWithShopDTO4),
        aggregationRepository.findProductsByIdsAndType(
            List.of(productInfo1, productInfo2, productInfo3, productInfo4, productInfo5)));
  }

  @Test
  public void shouldReturnTrueAtReservedProductsWhenEverythingOk() {
    final long countBeforeUpdate = reservedProductsRepository.count();
    final long countOfNewReservedProducts = 2;
    Map<ObjectId, Integer> reducedProducts = new HashMap<>();
    reducedProducts.put(new ObjectId(productDTO1.getProductId()), 1);
    reducedProducts.put(new ObjectId(productDTO2.getProductId()), 1);
    reducedProducts.put(new ObjectId(productDTO4.getProductId()), 1);
    reducedProducts.put(new ObjectId(productDTO7.getProductId()), 1);

    assertTrue(
        aggregationRepository.reservedProducts(
            reducedProducts,
            "sadfasbsd234",
            new ObjectId("123356789012345678903333"),
            new ObjectId()));
    assertEquals(
        countBeforeUpdate + countOfNewReservedProducts, reservedProductsRepository.count());
  }

  @Test
  public void
      shouldReturnPageOwnerProductsDTOAtGetProductsByOwnerCardWhenCountParamAndDescending() {
    List<ProductAtCardDTO> products = new ArrayList<>();
    PageOwnerProductsDTO page = new PageOwnerProductsDTO();
    products.add(productAtCardDTO1);
    products.add(productAtCardDTO2);
    products.add(productAtCardDTO3);
    page.setMaxPage(1);
    page.setProducts(products);

    assertEquals(
        page,
        aggregationRepository.getProductsByOwnerCard(
            0, COUNT_FIELD, true, "", "", "", TEST_CARD_ID.toString()));
  }

  @Test
  public void shouldReturnPageOwnerProductsDTOAtGetProductsByOwnerCardWhenCountParamAndAscending() {
    List<ProductAtCardDTO> products = new ArrayList<>();
    PageOwnerProductsDTO page = new PageOwnerProductsDTO();
    products.add(productAtCardDTO3);
    products.add(productAtCardDTO2);
    products.add(productAtCardDTO1);
    page.setMaxPage(1);
    page.setProducts(products);

    assertEquals(
        page,
        aggregationRepository.getProductsByOwnerCard(
            0, COUNT_FIELD, false, "", "", "", TEST_CARD_ID.toString()));
  }

  @Test
  public void shouldReturnPageOwnerProductsDTOAtGetProductsByOwnerCardWhenDateParamAndDescending() {
    List<ProductAtCardDTO> products = new ArrayList<>();
    PageOwnerProductsDTO page = new PageOwnerProductsDTO();
    products.add(productAtCardDTO1);
    products.add(productAtCardDTO2);
    products.add(productAtCardDTO3);
    page.setMaxPage(1);
    page.setProducts(products);

    assertEquals(
        page,
        aggregationRepository.getProductsByOwnerCard(
            0, DATE_FIELD, true, "", "", "", TEST_CARD_ID.toString()));
  }

  @Test
  public void shouldReturnPageOwnerProductsDTOAtGetProductsByOwnerCardWhenDateParamAndAscending() {
    List<ProductAtCardDTO> products = new ArrayList<>();
    PageOwnerProductsDTO page = new PageOwnerProductsDTO();
    products.add(productAtCardDTO3);
    products.add(productAtCardDTO2);
    products.add(productAtCardDTO1);
    page.setMaxPage(1);
    page.setProducts(products);

    assertEquals(
        page,
        aggregationRepository.getProductsByOwnerCard(
            0, DATE_FIELD, false, "", "", "", TEST_CARD_ID.toString()));
  }

  @Test
  public void
      shouldReturnPageOwnerProductsDTOAtGetProductsByOwnerCardWhenPriceParamAndDescending() {
    List<ProductAtCardDTO> products = new ArrayList<>();
    PageOwnerProductsDTO page = new PageOwnerProductsDTO();
    products.add(productAtCardDTO2);
    products.add(productAtCardDTO3);
    products.add(productAtCardDTO1);
    page.setMaxPage(1);
    page.setProducts(products);

    assertEquals(
        page,
        aggregationRepository.getProductsByOwnerCard(
            0, PRICE_FIELD, true, "", "", "", TEST_CARD_ID.toString()));
  }

  @Test
  public void shouldReturnPageOwnerProductsDTOAtGetProductsByOwnerCardWhenPriceParamAndAscending() {
    List<ProductAtCardDTO> products = new ArrayList<>();
    PageOwnerProductsDTO page = new PageOwnerProductsDTO();
    products.add(productAtCardDTO1);
    products.add(productAtCardDTO3);
    products.add(productAtCardDTO2);
    page.setMaxPage(1);
    page.setProducts(products);

    assertEquals(
        page,
        aggregationRepository.getProductsByOwnerCard(
            0, PRICE_FIELD, false, "", "", "", TEST_CARD_ID.toString()));
  }

  @Test
  public void shouldReturnPageOwnerProductsDTOAtGetProductsByOwnerCardWhenTextNotEmpty() {
    List<ProductAtCardDTO> products = new ArrayList<>();
    PageOwnerProductsDTO page = new PageOwnerProductsDTO();
    products.add(productAtCardDTO1);
    page.setMaxPage(1);
    page.setProducts(products);

    assertEquals(
        page,
        aggregationRepository.getProductsByOwnerCard(
            0, COUNT_FIELD, true, "product10", "", "", TEST_CARD_ID.toString()));
  }

  @Test
  public void shouldReturnPageOwnerProductsDTOAtGetProductsByOwnerCardWhenCategoryNotEmpty() {
    List<ProductAtCardDTO> products = new ArrayList<>();
    PageOwnerProductsDTO page = new PageOwnerProductsDTO();
    products.add(productAtCardDTO1);
    page.setMaxPage(1);
    page.setProducts(products);

    assertEquals(
        page,
        aggregationRepository.getProductsByOwnerCard(
            0, COUNT_FIELD, true, "", TEST_CATEGORY_OTHER_NAME, "", TEST_CARD_ID.toString()));
  }

  @Test
  public void shouldReturnPageOwnerProductsDTOAtGetProductsByOwnerCardWhenShopNameNotEmpty() {
    List<ProductAtCardDTO> products = new ArrayList<>();
    PageOwnerProductsDTO page = new PageOwnerProductsDTO();
    products.add(productAtCardDTO3);
    page.setMaxPage(1);
    page.setProducts(products);

    assertEquals(
        page,
        aggregationRepository.getProductsByOwnerCard(
            0, COUNT_FIELD, true, "", "", TEST_SHOP_NAME, TEST_CARD_ID.toString()));
  }

  @Test
  public void
      shouldReturnPageOwnerProductsDTOAtGetProductsByOwnerCardWhenProductsHasMoreThanOnePrice() {
    final int testPrice1 = 123;
    final int testPrice2 = 1200;
    List<ProductAtCardDTO> products = new ArrayList<>();
    ProductAtCardDTO product1 = new ProductAtCardDTO();
    product1.setCount(1);
    product1.setPrice(testPrice2);
    product1.setProductName(this.product1.getName());
    product1.setProductImageUrl(this.product1.getImageUrl());
    product1.setDescription(this.product1.getDescription());
    product1.setShopImageUrl(shop1.getImageUrl());
    product1.setShopName(shop1.getName());
    ProductAtCardDTO product2 = new ProductAtCardDTO();
    product2.setCount(1);
    product2.setPrice(testPrice1);
    product2.setProductName(this.product1.getName());
    product2.setProductImageUrl(this.product1.getImageUrl());
    product2.setDescription(this.product1.getDescription());
    product2.setShopImageUrl(shop1.getImageUrl());
    product2.setShopName(shop1.getName());
    PageOwnerProductsDTO expectedPage = new PageOwnerProductsDTO();
    products.add(product1);
    products.add(product2);
    expectedPage.setMaxPage(1);
    expectedPage.setProducts(products);

    PageOwnerProductsDTO page =
        aggregationRepository.getProductsByOwnerCard(
            0, COUNT_FIELD, true, "", "", "", TEST_OTHER_CARD_ID.toString());
    assertEquals(expectedPage.getMaxPage(), page.getMaxPage());
    assertTrue(expectedPage.getProducts().containsAll(page.getProducts()));
  }

  @Test
  public void shouldReturnPageCategoryDTOAtGetCategoriesWhenWithoutName() {
    PageCategoryDTO pageCategoryDTO = new PageCategoryDTO();
    pageCategoryDTO.setMaxPage(1);
    pageCategoryDTO.setCategories(
        List.of(categoryDTO1, categoryDTO2, categoryDTO3, categoryDTO4, categoryDTO5));

    assertEquals(pageCategoryDTO, aggregationRepository.getCategories(0, ""));
  }

  @Test
  public void shouldReturnPageCategoryDTOAtGetCategoriesWhenWithName() {
    PageCategoryDTO pageCategoryDTO = new PageCategoryDTO();
    pageCategoryDTO.setCategories(List.of(categoryDTO1, categoryDTO4));
    pageCategoryDTO.setMaxPage(1);

    assertEquals(pageCategoryDTO, aggregationRepository.getCategories(0, TEST_CATEGORY_NAME));
  }

  @Test
  public void shouldReturnPageUserDTOAtGetUsersWhenNotType() {
    PageUserDTO pageUserDTO = new PageUserDTO();
    pageUserDTO.setMaxPage(1);
    pageUserDTO.setUsers(
        List.of(userDTO1, userDTO2, userDTO3, userDTO4, userDTO5, userDTO6, userDTO7));

    assertEquals(pageUserDTO, aggregationRepository.getUsers("", "", 0));
  }

  @Test
  public void shouldReturnPageUserDTOAtGetUsersWhenType0() {
    PageUserDTO pageUserDTO = new PageUserDTO();
    pageUserDTO.setMaxPage(1);
    pageUserDTO.setUsers(List.of(userDTO3));

    assertEquals(pageUserDTO, aggregationRepository.getUsers("0", shop3.getId().toString(), 0));
  }

  @Test
  public void shouldReturnPageUserDTOAtGetUsersWhenType1() {
    PageUserDTO pageUserDTO = new PageUserDTO();
    pageUserDTO.setMaxPage(1);
    pageUserDTO.setUsers(List.of(userDTO1));

    assertEquals(pageUserDTO, aggregationRepository.getUsers("1", TEST_SHOP_NAME, 0));
  }

  @Test
  public void shouldReturnPageUserDTOAtGetUsersWhenType2() {
    PageUserDTO pageUserDTO = new PageUserDTO();
    pageUserDTO.setMaxPage(1);
    pageUserDTO.setUsers(List.of(userDTO7));

    assertEquals(pageUserDTO, aggregationRepository.getUsers("2", TEST_FIRST_NAME_USER, 0));
  }

  @Test
  public void shouldReturnPageUserDTOAtGetUsersWhenType3() {
    PageUserDTO pageUserDTO = new PageUserDTO();
    pageUserDTO.setMaxPage(1);
    pageUserDTO.setUsers(List.of(userDTO7));

    assertEquals(pageUserDTO, aggregationRepository.getUsers("3", TEST_LAST_NAME_USER, 0));
  }

  @Test
  public void shouldReturnPageUserDTOAtGetUsersWhenType4() {
    PageUserDTO pageUserDTO = new PageUserDTO();
    pageUserDTO.setMaxPage(1);
    pageUserDTO.setUsers(List.of(userDTO6));

    assertEquals(pageUserDTO, aggregationRepository.getUsers("4", " 481234123452314", 0));
  }

  @Test
  public void shouldReturnPageUserDTOAtGetUsersWhenType5() {
    PageUserDTO pageUserDTO = new PageUserDTO();
    pageUserDTO.setMaxPage(1);
    pageUserDTO.setUsers(List.of(userDTO2));

    assertEquals(pageUserDTO, aggregationRepository.getUsers("5", TEST_EMAIL, 0));
  }

  @Test
  public void shouldReturnPageUserDTOAtGetUsersWhenType6() {
    PageUserDTO pageUserDTO = new PageUserDTO();
    pageUserDTO.setMaxPage(1);
    pageUserDTO.setUsers(List.of(userDTO1, userDTO2, userDTO3, userDTO4));

    assertEquals(pageUserDTO, aggregationRepository.getUsers("6", "ROLE_SHOP", 0));
  }

  @Test
  public void shouldReturnPageUserDTOAtGetUsersWhenType7() {
    PageUserDTO pageUserDTO = new PageUserDTO();
    pageUserDTO.setMaxPage(1);
    pageUserDTO.setUsers(List.of(userDTO1, userDTO2, userDTO3, userDTO4, userDTO6));

    assertEquals(pageUserDTO, aggregationRepository.getUsers("7", TRUE_VALUE, 0));
  }

  @Test
  public void shouldReturnPageUserDTOAtGetUsersWhenType8() {
    PageUserDTO pageUserDTO = new PageUserDTO();
    pageUserDTO.setMaxPage(1);
    pageUserDTO.setUsers(List.of(userDTO6, userDTO7));

    assertEquals(pageUserDTO, aggregationRepository.getUsers("8", TRUE_VALUE, 0));
  }

  @Test
  public void shouldReturnPageUserDTOAtGetUsersWhenType9() {
    PageUserDTO pageUserDTO = new PageUserDTO();
    pageUserDTO.setMaxPage(1);
    pageUserDTO.setUsers(List.of(userDTO5, userDTO7));

    assertEquals(pageUserDTO, aggregationRepository.getUsers("9", TRUE_VALUE, 0));
  }

  @Test
  public void shouldReturnPageUserDTOAtGetUsersWhenNotFound() {
    PageUserDTO pageUserDTO = new PageUserDTO();
    pageUserDTO.setMaxPage(0);
    pageUserDTO.setUsers(List.of());

    assertEquals(pageUserDTO, aggregationRepository.getUsers("2", "sdafdasfsadfsd", 0));
  }
}
