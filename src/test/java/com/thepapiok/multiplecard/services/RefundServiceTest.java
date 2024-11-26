package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.thepapiok.multiplecard.collections.Refund;
import com.thepapiok.multiplecard.repositories.RefundRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class RefundServiceTest {
  private static final String TEST_ORDER_ID = "123456789012345678901234";
  private RefundService refundService;
  @Mock private RefundRepository refundRepository;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    refundService = new RefundService(refundRepository);
  }

  @Test
  public void shouldReturnTrueAtCheckExistsAlreadyRefundWhenRefundExists() {
    when(refundRepository.existsByOrderId(TEST_ORDER_ID)).thenReturn(true);

    assertTrue(refundService.checkExistsAlreadyRefund(TEST_ORDER_ID));
  }

  @Test
  public void shouldReturnTrueAtCheckExistsAlreadyRefundWhenNotRefundExists() {
    when(refundRepository.existsByOrderId(TEST_ORDER_ID)).thenReturn(false);

    assertFalse(refundService.checkExistsAlreadyRefund(TEST_ORDER_ID));
  }

  @Test
  public void shouldCreateRefundAtCreateRefundWhenEverythingOk() {
    Refund expectedRefund = new Refund();
    expectedRefund.setRefunded(false);
    expectedRefund.setOrderId(TEST_ORDER_ID);

    refundService.createRefund(TEST_ORDER_ID);
    verify(refundRepository).save(expectedRefund);
  }

  @Test
  public void shouldUpdateRefundAtUpdateRefundWhenEverythingOk() {
    Refund refund = new Refund();
    refund.setRefunded(false);
    refund.setOrderId(TEST_ORDER_ID);
    Refund expectedRefund = new Refund();
    expectedRefund.setRefunded(true);
    expectedRefund.setOrderId(TEST_ORDER_ID);

    when(refundRepository.findByOrderId(TEST_ORDER_ID)).thenReturn(refund);

    refundService.updateRefund(TEST_ORDER_ID);
    verify(refundRepository).save(expectedRefund);
  }
}
