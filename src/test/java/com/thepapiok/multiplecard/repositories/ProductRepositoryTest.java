package com.thepapiok.multiplecard.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.thepapiok.multiplecard.collections.Address;
import com.thepapiok.multiplecard.collections.Category;
import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.collections.Shop;
import com.thepapiok.multiplecard.configs.DbConfig;
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
  private Product product;

  @Autowired private ProductRepository productRepository;
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private PromotionRepository promotionRepository;
  @Autowired private ShopRepository shopRepository;
  @Autowired private BlockedProductRepository blockedProductRepository;
  @Autowired private MongoTemplate mongoTemplate;
  @MockBean private RestTemplate restTemplate;

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
    shop.setAccountNumber("accountNumber1");
    shop.setPoints(List.of(address));
    testShop = mongoTemplate.save(shop);
    Category category = new Category();
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
    blockedProductRepository.deleteAll();
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
}
