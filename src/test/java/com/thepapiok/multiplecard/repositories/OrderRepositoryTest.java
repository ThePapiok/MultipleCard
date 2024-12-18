package com.thepapiok.multiplecard.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.thepapiok.multiplecard.collections.Category;
import com.thepapiok.multiplecard.collections.Order;
import com.thepapiok.multiplecard.collections.Product;
import com.thepapiok.multiplecard.configs.DbConfig;
import com.thepapiok.multiplecard.dto.ProductOrderDTO;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
public class OrderRepositoryTest {
  private static final ObjectId TEST_PRODUCT_ID = new ObjectId("123456789012345678901234");
  private static final ObjectId TEST_SHOP_ID = new ObjectId("123456789012345178901231");
  private static final ObjectId TEST_ORDER_ID = new ObjectId("563456789012345178901231");
  private static final ObjectId TEST_CARD_ID = new ObjectId("123456789012345678901231");
  private Order order1;
  private Order order2;
  private Product product;

  @Autowired private OrderRepository orderRepository;
  @Autowired private ProductRepository productRepository;
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private MongoTemplate mongoTemplate;
  @MockBean private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() {
    final int testPrice1 = 555;
    final int testPrice2 = 111;
    final int testYearOfCreatedAt = 2024;
    final int testMonthOfCreatedAt = 5;
    final int testDayOfCreatedAt = 5;
    final int testHourOfCreatedAt = 5;
    final int testMinuteOfCreatedAt = 1;
    final int testSecondOfCreatedAt = 1;
    final ObjectId testProduct2Id = new ObjectId("123456789012345678901236");
    final LocalDateTime localDateTime =
        LocalDateTime.of(
            testYearOfCreatedAt,
            testMonthOfCreatedAt,
            testDayOfCreatedAt,
            testHourOfCreatedAt,
            testMinuteOfCreatedAt,
            testSecondOfCreatedAt);
    final String testUrl = "url";
    final String testName = "name";
    final String testDescription = "description";
    final String testBarcode = "1234123412314312";
    final String testCategoryName = "category";
    final int testPrice = 500;
    order1 = new Order();
    order1.setUsed(false);
    order1.setPrice(testPrice1);
    order1.setCreatedAt(localDateTime);
    order1.setProductId(TEST_PRODUCT_ID);
    order1.setCardId(TEST_CARD_ID);
    order1.setShopId(TEST_SHOP_ID);
    order1.setOrderId(TEST_ORDER_ID);
    order1 = mongoTemplate.save(order1);
    order2 = new Order();
    order2.setUsed(false);
    order2.setPrice(testPrice1);
    order2.setCreatedAt(localDateTime);
    order2.setProductId(TEST_PRODUCT_ID);
    order2.setCardId(TEST_CARD_ID);
    order2.setShopId(TEST_SHOP_ID);
    order2.setOrderId(TEST_ORDER_ID);
    order2 = mongoTemplate.save(order2);
    Order order3 = new Order();
    order3.setUsed(true);
    order3.setPrice(testPrice2);
    order3.setCreatedAt(localDateTime);
    order3.setProductId(TEST_PRODUCT_ID);
    order3.setCardId(TEST_CARD_ID);
    order3.setShopId(TEST_SHOP_ID);
    order3.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order3);
    Order order4 = new Order();
    order4.setUsed(true);
    order4.setPrice(testPrice2);
    order4.setCreatedAt(localDateTime);
    order4.setProductId(testProduct2Id);
    order4.setCardId(TEST_CARD_ID);
    order4.setShopId(TEST_SHOP_ID);
    order4.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order4);
    Category category = new Category();
    category.setOwnerId(TEST_SHOP_ID);
    category.setName(testCategoryName);
    category = mongoTemplate.save(category);
    product = new Product();
    product.setId(TEST_PRODUCT_ID);
    product.setImageUrl(testUrl);
    product.setName(testName);
    product.setDescription(testDescription);
    product.setBarcode(testBarcode);
    product.setShopId(TEST_SHOP_ID);
    product.setPrice(testPrice);
    product.setUpdatedAt(LocalDateTime.now());
    product.setCategories(List.of(category.getId()));
    mongoTemplate.save(product);
  }

  @AfterEach
  public void cleanUp() {
    orderRepository.deleteAll();
    productRepository.deleteAll();
    categoryRepository.deleteAll();
  }

  @Test
  public void shouldReturnListOf2OrdersAtFindAllByProductIdAndIsUsedWhenEverythingOk() {
    assertEquals(
        List.of(order1, order2),
        orderRepository.findAllByProductIdAndIsUsed(TEST_PRODUCT_ID, false));
  }

  @Test
  public void shouldReturnEmptyListAtFindAllByProductIdAndIsUsedWhenNotFound() {
    assertEquals(
        List.of(),
        orderRepository.findAllByProductIdAndIsUsed(
            new ObjectId("098765432112345678901234"), false));
  }

  @Test
  public void shouldReturn222AtSumTotalAmountForShopWhenEverythingOk() {
    final Long totalAmount = 222L;

    assertEquals(totalAmount, orderRepository.sumTotalAmountForShop(TEST_SHOP_ID));
  }

  @Test
  public void shouldReturnNullAtSumTotalAmountForShopWhenNotFound() {
    assertNull(orderRepository.sumTotalAmountForShop(new ObjectId("098765432112345678901234")));
  }

  @Test
  public void shouldReturnListOfProductOrderDTOAtGetProductsAtCardWhenEverythingOk() {
    List<ProductOrderDTO> productOrderDTOS = new ArrayList<>();
    ProductOrderDTO productOrderDTO1 = new ProductOrderDTO();
    productOrderDTO1.setId(order1.getId().toString());
    productOrderDTO1.setName(product.getName());
    productOrderDTO1.setBarcode(product.getBarcode());
    productOrderDTO1.setDescription(product.getDescription());
    productOrderDTO1.setImageUrl(product.getImageUrl());
    ProductOrderDTO productOrderDTO2 = new ProductOrderDTO();
    productOrderDTO2.setId(order2.getId().toString());
    productOrderDTO2.setName(product.getName());
    productOrderDTO2.setBarcode(product.getBarcode());
    productOrderDTO2.setDescription(product.getDescription());
    productOrderDTO2.setImageUrl(product.getImageUrl());
    productOrderDTOS.add(productOrderDTO1);
    productOrderDTOS.add(productOrderDTO2);

    assertEquals(productOrderDTOS, orderRepository.getProductsAtCard(TEST_SHOP_ID, TEST_CARD_ID));
  }
}
