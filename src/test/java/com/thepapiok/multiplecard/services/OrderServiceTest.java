package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.thepapiok.multiplecard.collections.Account;
import com.thepapiok.multiplecard.collections.Order;
import com.thepapiok.multiplecard.repositories.AccountRepository;
import com.thepapiok.multiplecard.repositories.OrderRepository;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;

public class OrderServiceTest {
  private static final String TEST_ORDER_ID = "123456789012345678901234";
  private static final String TEST_PHONE = "1234123423";
  private static final ObjectId TEST_ORDER_OBJECT_ID = new ObjectId(TEST_ORDER_ID);
  private static final ObjectId TEST_CARD_ID = new ObjectId("123456789012345678901236");
  private static final ObjectId TEST_SHOP_ID = new ObjectId("123456789012345678901231");
  private OrderService orderService;
  @Mock private OrderRepository orderRepository;
  @Mock private MongoTransactionManager mongoTransactionManager;
  @Mock private MongoTemplate mongoTemplate;
  @Mock private AccountRepository accountRepository;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    orderService =
        new OrderService(
            orderRepository, mongoTransactionManager, mongoTemplate, accountRepository);
  }

  @Test
  public void shouldReturnTrueAtCheckExistsAlreadyOrderWhenOrderExists() {
    when(orderRepository.existsByOrderId(TEST_ORDER_OBJECT_ID)).thenReturn(true);

    assertTrue(orderService.checkExistsAlreadyOrder(TEST_ORDER_ID));
  }

  @Test
  public void shouldReturnFalseAtCheckExistsAlreadyOrderWhenOrderNotExists() {
    when(orderRepository.existsByOrderId(TEST_ORDER_OBJECT_ID)).thenReturn(false);

    assertFalse(orderService.checkExistsAlreadyOrder(TEST_ORDER_ID));
  }

  @Test
  public void shouldReturnFalseAtMakeOrderUsedWhenGetException() {
    final ObjectId testId1 = new ObjectId("223456789012345678901234");
    final ObjectId testId2 = new ObjectId("323456789012345678901234");
    final ObjectId testId3 = new ObjectId("423456789012345678901234");
    List<String> ids = List.of(testId1.toString(), testId2.toString(), testId3.toHexString());
    Order order = new Order();
    order.setId(testId1);
    order.setUsed(false);
    order.setCardId(TEST_CARD_ID);
    order.setShopId(TEST_SHOP_ID);
    Order expectedOrder = new Order();
    expectedOrder.setId(testId1);
    expectedOrder.setUsed(true);
    expectedOrder.setCardId(TEST_CARD_ID);
    expectedOrder.setShopId(TEST_SHOP_ID);
    Account account = new Account();
    account.setId(TEST_SHOP_ID);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(orderRepository.findByIdAndCardIdAndShopId(testId2, TEST_CARD_ID, TEST_SHOP_ID))
        .thenReturn(null);
    when(orderRepository.findByIdAndCardIdAndShopId(testId1, TEST_CARD_ID, TEST_SHOP_ID))
        .thenReturn(order);

    assertFalse(orderService.makeOrdersUsed(ids, TEST_CARD_ID, TEST_PHONE));
    verify(mongoTemplate).save(expectedOrder);
  }

  @Test
  public void shouldReturnTrueAtMakeOrderUsedWhenEverythingOk() {
    final ObjectId testId1 = new ObjectId("223456789012345678901234");
    final ObjectId testId2 = new ObjectId("323456789012345678901234");
    final ObjectId testId3 = new ObjectId("423456789012345678901234");
    List<String> ids = List.of(testId1.toString(), testId2.toString(), testId3.toHexString());
    Order order1 = new Order();
    order1.setId(testId1);
    order1.setUsed(false);
    order1.setCardId(TEST_CARD_ID);
    order1.setShopId(TEST_SHOP_ID);
    Order expectedOrder1 = new Order();
    expectedOrder1.setId(testId1);
    expectedOrder1.setUsed(true);
    expectedOrder1.setCardId(TEST_CARD_ID);
    expectedOrder1.setShopId(TEST_SHOP_ID);
    Order order2 = new Order();
    order2.setId(testId2);
    order2.setUsed(false);
    order2.setCardId(TEST_CARD_ID);
    order2.setShopId(TEST_SHOP_ID);
    Order expectedOrder2 = new Order();
    expectedOrder2.setId(testId2);
    expectedOrder2.setUsed(true);
    expectedOrder2.setCardId(TEST_CARD_ID);
    expectedOrder2.setShopId(TEST_SHOP_ID);
    Order order3 = new Order();
    order3.setId(testId3);
    order3.setUsed(false);
    order3.setCardId(TEST_CARD_ID);
    order3.setShopId(TEST_SHOP_ID);
    Order expectedOrder3 = new Order();
    expectedOrder3.setId(testId3);
    expectedOrder3.setUsed(true);
    expectedOrder3.setCardId(TEST_CARD_ID);
    expectedOrder3.setShopId(TEST_SHOP_ID);
    Account account = new Account();
    account.setId(TEST_SHOP_ID);

    when(accountRepository.findIdByPhone(TEST_PHONE)).thenReturn(account);
    when(orderRepository.findByIdAndCardIdAndShopId(testId1, TEST_CARD_ID, TEST_SHOP_ID))
        .thenReturn(order1);
    when(orderRepository.findByIdAndCardIdAndShopId(testId2, TEST_CARD_ID, TEST_SHOP_ID))
        .thenReturn(order2);
    when(orderRepository.findByIdAndCardIdAndShopId(testId3, TEST_CARD_ID, TEST_SHOP_ID))
        .thenReturn(order3);

    assertTrue(orderService.makeOrdersUsed(ids, TEST_CARD_ID, TEST_PHONE));
    verify(mongoTemplate).save(expectedOrder1);
    verify(mongoTemplate).save(expectedOrder2);
    verify(mongoTemplate).save(expectedOrder3);
  }
}
