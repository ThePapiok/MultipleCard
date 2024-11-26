package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.thepapiok.multiplecard.repositories.OrderRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class OrderServiceTest {
  private static final String TEST_ORDER_ID = "123456789012345678901234";
  private static final ObjectId TEST_ORDER_OBJECT_ID = new ObjectId(TEST_ORDER_ID);
  private OrderService orderService;
  @Mock private OrderRepository orderRepository;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    orderService = new OrderService(orderRepository);
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
}
