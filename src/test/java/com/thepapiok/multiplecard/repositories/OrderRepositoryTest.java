package com.thepapiok.multiplecard.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.thepapiok.multiplecard.collections.Order;
import com.thepapiok.multiplecard.configs.DbConfig;
import java.time.LocalDateTime;
import java.util.List;
import org.bson.types.ObjectId;
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

  @Autowired private OrderRepository orderRepository;
  @Autowired private MongoTemplate mongoTemplate;
  @MockBean private RestTemplate restTemplate;

  @Test
  public void shouldReturn() {
    final int testAmount1 = 555;
    final int testAmount2 = 111;
    final int testYearOfCreatedAt = 2024;
    final int testMonthOfCreatedAt = 5;
    final int testDayOfCreatedAt = 5;
    final int testHourOfCreatedAt = 5;
    final int testMinuteOfCreatedAt = 1;
    final int testSecondOfCreatedAt = 1;
    final ObjectId testProduct1Id = new ObjectId("123456789012345678901234");
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
    Order order1 = new Order();
    order1.setUsed(false);
    order1.setAmount(testAmount1);
    order1.setCreatedAt(localDateTime);
    order1.setProductId(testProduct1Id);
    order1.setCardId(testCardId);
    mongoTemplate.save(order1);
    Order order2 = new Order();
    order2.setUsed(false);
    order2.setAmount(testAmount1);
    order2.setCreatedAt(localDateTime);
    order2.setProductId(testProduct1Id);
    order2.setCardId(testCardId);
    mongoTemplate.save(order2);
    Order order3 = new Order();
    order3.setUsed(true);
    order3.setAmount(testAmount2);
    order3.setCreatedAt(localDateTime);
    order3.setProductId(testProduct1Id);
    order3.setCardId(testCardId);
    mongoTemplate.save(order3);
    Order order4 = new Order();
    order4.setUsed(false);
    order4.setAmount(testAmount2);
    order4.setCreatedAt(localDateTime);
    order4.setProductId(testProduct2Id);
    order4.setCardId(testCardId);
    mongoTemplate.save(order4);

    assertEquals(
        List.of(order1, order2),
        orderRepository.findAllByProductIdAndIsUsed(testProduct1Id, false));
  }
}
