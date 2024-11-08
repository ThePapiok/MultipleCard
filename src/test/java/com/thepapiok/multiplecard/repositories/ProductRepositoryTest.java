package com.thepapiok.multiplecard.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.thepapiok.multiplecard.collections.Category;
import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.collections.Promotion;
import com.thepapiok.multiplecard.configs.DbConfig;
import com.thepapiok.multiplecard.dto.ProductWithPromotionDTO;
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
  static final ObjectId TEST_SHOP_ID = new ObjectId("123456789012345678901234");

  @Autowired private ProductRepository productRepository;
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private PromotionRepository promotionRepository;
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
    category = new Category();
    category.setName("category");
    category.setOwnerId(TEST_SHOP_ID);
    mongoTemplate.save(category);
    product = new Product();
    product.setShopId(TEST_SHOP_ID);
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
  }

  @Test
  public void shouldReturnProductAtFindShopIdByIdWhenEverythingOk() {
    ObjectId productId;
    productId = product.getId();
    Product expectedProduct = new Product();
    expectedProduct.setShopId(TEST_SHOP_ID);

    assertEquals(expectedProduct, productRepository.findShopIdById(productId));
  }

  @Test
  public void shouldReturnListOfProductWithPromotionDTOAtFindProductsByIds() {
    final ObjectId shopId = new ObjectId("123456789012345678901231");
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
    Product otherProduct = new Product();
    otherProduct.setShopId(shopId);
    otherProduct.setImageUrl("url1");
    otherProduct.setName("name1");
    otherProduct.setBarcode("barcode1");
    otherProduct.setDescription("description1");
    otherProduct.setAmount(amount);
    otherProduct.setCategories(List.of(category.getId()));
    otherProduct.setUpdatedAt(localDateTime);
    otherProduct = mongoTemplate.save(otherProduct);
    Promotion promotion = new Promotion();
    promotion.setProductId(otherProduct.getId());
    promotion.setCount(0);
    promotion.setAmount(amountPromotion);
    promotion.setStartAt(LocalDate.now());
    promotion.setExpiredAt(LocalDate.now().plusYears(1));
    promotionRepository.save(promotion);
    ProductWithPromotionDTO product1 = new ProductWithPromotionDTO();
    product1.setId(product.getId().toString());
    product1.setDescription(product.getDescription());
    product1.setBarcode(product.getBarcode());
    product1.setAmount(product.getAmount());
    product1.setCategories(product.getCategories());
    product1.setName(product.getName());
    product1.setShopId(product.getShopId());
    product1.setImageUrl(product.getImageUrl());
    product1.setUpdatedAt(product.getUpdatedAt());
    product1.setPromotion(null);
    ProductWithPromotionDTO product2 = new ProductWithPromotionDTO();
    product2.setId(otherProduct.getId().toString());
    product2.setDescription(otherProduct.getDescription());
    product2.setBarcode(otherProduct.getBarcode());
    product2.setAmount(otherProduct.getAmount());
    product2.setCategories(otherProduct.getCategories());
    product2.setName(otherProduct.getName());
    product2.setShopId(otherProduct.getShopId());
    product2.setImageUrl(otherProduct.getImageUrl());
    product2.setUpdatedAt(otherProduct.getUpdatedAt());
    product2.setPromotion(promotion);

    assertEquals(
        List.of(product1, product2),
        productRepository.findProductsByIds(List.of(product.getId(), otherProduct.getId()), 0));
  }
}
