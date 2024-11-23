package com.thepapiok.multiplecard.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.thepapiok.multiplecard.collections.Order;
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
public class OrderRepositoryTest {
  private static final ObjectId TEST_PRODUCT_ID = new ObjectId("123456789012345678901234");
  private static final ObjectId TEST_SHOP_ID = new ObjectId("123456789012345178901231");
  private static final ObjectId TEST_ORDER_ID = new ObjectId("563456789012345178901231");
  private Order order1;
  private Order order2;

  @Autowired private OrderRepository orderRepository;
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
    final ObjectId testCardId = new ObjectId("123456789012345678901231");
    final LocalDateTime localDateTime =
        LocalDateTime.of(
            testYearOfCreatedAt,
            testMonthOfCreatedAt,
            testDayOfCreatedAt,
            testHourOfCreatedAt,
            testMinuteOfCreatedAt,
            testSecondOfCreatedAt);
    order1 = new Order();
    order1.setUsed(false);
    order1.setPrice(testPrice1);
    order1.setCreatedAt(localDateTime);
    order1.setProductId(TEST_PRODUCT_ID);
    order1.setCardId(testCardId);
    order1.setShopId(TEST_SHOP_ID);
    order1.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order1);
    order2 = new Order();
    order2.setUsed(false);
    order2.setPrice(testPrice1);
    order2.setCreatedAt(localDateTime);
    order2.setProductId(TEST_PRODUCT_ID);
    order2.setCardId(testCardId);
    order2.setShopId(TEST_SHOP_ID);
    order2.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order2);
    Order order3 = new Order();
    order3.setUsed(true);
    order3.setPrice(testPrice2);
    order3.setCreatedAt(localDateTime);
    order3.setProductId(TEST_PRODUCT_ID);
    order3.setCardId(testCardId);
    order3.setShopId(TEST_SHOP_ID);
    order3.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order3);
    Order order4 = new Order();
    order4.setUsed(true);
    order4.setPrice(testPrice2);
    order4.setCreatedAt(localDateTime);
    order4.setProductId(testProduct2Id);
    order4.setCardId(testCardId);
    order4.setShopId(TEST_SHOP_ID);
    order4.setOrderId(TEST_ORDER_ID);
    mongoTemplate.save(order4);
  }

  @AfterEach
  public void cleanUp() {
    orderRepository.deleteAll();
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
}
