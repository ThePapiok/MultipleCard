package com.thepapiok.multiplecard.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.Category;
import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.collections.Promotion;
import com.thepapiok.multiplecard.collections.Shop;
import com.thepapiok.multiplecard.configs.DbConfig;
import com.thepapiok.multiplecard.dto.ProductWithShopDTO;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

@DataMongoTest
@ActiveProfiles("test")
@Import(DbConfig.class)
public class ProductRepositoryTest {
  private static Shop testShop;

  @Autowired private ProductRepository productRepository;
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private PromotionRepository promotionRepository;
  @Autowired private ShopRepository shopRepository;
  @Autowired private BlockedRepository blockedRepository;
  @Autowired private MongoTemplate mongoTemplate;
  @MockBean private RestTemplate restTemplate;
  private Product product;
  private Category category;

  @BeforeEach
  public void setUp() {
    final int amount = 500;
    final int testYearOfCreatedAt = 2024;
    final int testMonthOfCreatedAt = 5;
    final int testDayOfCreatedAt = 5;
    final int testHourOfCreatedAt = 5;
    final int testMinuteOfCreatedAt = 1;
    final int testSecondOfCreatedAt = 1;
    final LocalDateTime localDateTime =
        LocalDateTime.of(
            testYearOfCreatedAt,
            testMonthOfCreatedAt,
            testDayOfCreatedAt,
            testHourOfCreatedAt,
            testMinuteOfCreatedAt,
            testSecondOfCreatedAt);
    Address address = new Address();
    address.setHouseNumber("0");
    address.setCountry("country1");
    address.setProvince("province1");
    address.setStreet("street1");
    address.setCity("city1");
    address.setPostalCode("postalCode1");
    address.setApartmentNumber(null);
    Shop shop = new Shop();
    shop.setName("shop1");
    shop.setImageUrl("shopImageUrl1");
    shop.setFirstName("firstName1");
    shop.setLastName("lastName1");
    shop.setTotalAmount(0L);
    shop.setAccountNumber("accountNumber1");
    shop.setPoints(List.of(address));
    testShop = mongoTemplate.save(shop);
    category = new Category();
    category.setName("category");
    category.setOwnerId(testShop.getId());
    mongoTemplate.save(category);
    product = new Product();
    product.setShopId(shop.getId());
    product.setImageUrl("url");
    product.setName("name");
    product.setBarcode("barcode");
    product.setDescription("description");
    product.setAmount(amount);
    product.setCategories(List.of(category.getId()));
    product.setUpdatedAt(localDateTime);
    product = mongoTemplate.save(product);
  }

  @AfterEach
  public void cleanUp() {
    productRepository.deleteAll();
    categoryRepository.deleteAll();
    promotionRepository.deleteAll();
    blockedRepository.deleteAll();
    shopRepository.deleteAll();
  }

  @Test
  public void shouldReturnProductAtFindShopIdByIdWhenEverythingOk() {
    ObjectId productId;
    productId = product.getId();
    Product expectedProduct = new Product();
    expectedProduct.setShopId(testShop.getId());

    assertEquals(expectedProduct, productRepository.findShopIdById(productId));
  }

  @Test
  public void shouldReturnListOfProductWithPromotionDTOAtFindProductsByIds() {
    final int amount = 510;
    final int amountPromotion = 500;
    final int testYearOfCreatedAt = 2024;
    final int testMonthOfCreatedAt = 5;
    final int testDayOfCreatedAt = 5;
    final int testHourOfCreatedAt = 5;
    final int testMinuteOfCreatedAt = 1;
    final int testSecondOfCreatedAt = 1;
    final LocalDateTime localDateTime =
        LocalDateTime.of(
            testYearOfCreatedAt,
            testMonthOfCreatedAt,
            testDayOfCreatedAt,
            testHourOfCreatedAt,
            testMinuteOfCreatedAt,
            testSecondOfCreatedAt);
    Address address = new Address();
    address.setHouseNumber("1B");
    address.setCountry("country2");
    address.setProvince("province2");
    address.setStreet("street2");
    address.setCity("city2");
    address.setPostalCode("postalCode2");
    address.setApartmentNumber(1);
    Shop shop = new Shop();
    shop.setName("shop2");
    shop.setImageUrl("shopImageUrl2");
    shop.setFirstName("firstName2");
    shop.setLastName("lastName2");
    shop.setTotalAmount(0L);
    shop.setAccountNumber("accountNumber2");
    shop.setPoints(List.of(address));
    shop = mongoTemplate.save(shop);
    Product product1 = new Product();
    product1.setShopId(shop.getId());
    product1.setImageUrl("url1");
    product1.setName("name1");
    product1.setBarcode("barcode1");
    product1.setDescription("description1");
    product1.setAmount(amount);
    product1.setCategories(List.of(category.getId()));
    product1.setUpdatedAt(localDateTime);
    product1 = mongoTemplate.save(product1);
    Promotion promotion = new Promotion();
    promotion.setProductId(product1.getId());
    promotion.setCount(0);
    promotion.setAmount(amountPromotion);
    promotion.setStartAt(LocalDate.now());
    promotion.setExpiredAt(LocalDate.now().plusYears(1));
    promotionRepository.save(promotion);
    ProductWithShopDTO productWithShopDTO1 = new ProductWithShopDTO();
    productWithShopDTO1.setDescription(product.getDescription());
    productWithShopDTO1.setBarcode(product.getBarcode());
    productWithShopDTO1.setAmount(product.getAmount());
    productWithShopDTO1.setShopId(product.getShopId());
    productWithShopDTO1.setProductId(product.getId().toString());
    productWithShopDTO1.setProductName(product.getName());
    productWithShopDTO1.setProductImageUrl(product.getImageUrl());
    productWithShopDTO1.setActive(true);
    productWithShopDTO1.setAmountPromotion(0);
    productWithShopDTO1.setCountPromotion(0);
    productWithShopDTO1.setStartAtPromotion(null);
    productWithShopDTO1.setExpiredAtPromotion(null);
    productWithShopDTO1.setShopName(testShop.getName());
    productWithShopDTO1.setShopImageUrl(testShop.getImageUrl());
    ProductWithShopDTO productWithShopDTO2 = new ProductWithShopDTO();
    productWithShopDTO2.setDescription(product1.getDescription());
    productWithShopDTO2.setBarcode(product1.getBarcode());
    productWithShopDTO2.setAmount(product1.getAmount());
    productWithShopDTO2.setShopId(product1.getShopId());
    productWithShopDTO2.setProductId(product1.getId().toString());
    productWithShopDTO2.setProductName(product1.getName());
    productWithShopDTO2.setProductImageUrl(product1.getImageUrl());
    productWithShopDTO2.setActive(true);
    productWithShopDTO2.setAmountPromotion(promotion.getAmount());
    productWithShopDTO2.setCountPromotion(promotion.getCount());
    productWithShopDTO2.setStartAtPromotion(promotion.getStartAt());
    productWithShopDTO2.setExpiredAtPromotion(promotion.getExpiredAt());
    productWithShopDTO2.setShopName(shop.getName());
    productWithShopDTO2.setShopImageUrl(shop.getImageUrl());

    assertEquals(
        List.of(productWithShopDTO1, productWithShopDTO2),
        productRepository.findProductsByIds(List.of(product.getId(), product1.getId()), 0));
  }
}
