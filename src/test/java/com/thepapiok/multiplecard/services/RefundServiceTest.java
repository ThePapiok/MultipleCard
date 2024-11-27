package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.thepapiok.multiplecard.collections.Refund;
import com.thepapiok.multiplecard.repositories.RefundRepository;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;

public class RefundServiceTest {
  private static final String TEST_ORDER_ID = "123456789012345678901234";
  private RefundService refundService;
  @Mock private RefundRepository refundRepository;
  @Mock private EmailService emailService;
  @Mock private MessageSource messageSource;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    refundService = new RefundService(refundRepository, emailService, messageSource);
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
    Locale locale = Locale.getDefault();

    when(messageSource.getMessage("buyProducts.refund.message", null, locale))
        .thenReturn(
            "Przepraszamy, wystąpił nieoczekiwany błąd związany z twoją transakcją. "
                + "W najbliższym czasie pieniądze trafią z powrotem na twoje konto.");
    when(messageSource.getMessage("buyProducts.refund.title", null, locale)).thenReturn("Zwrot");

    refundService.createRefund(TEST_ORDER_ID, "pl", "multiplecard@gmail.com");
    verify(refundRepository).save(expectedRefund);
    verify(emailService)
        .sendEmail(
            "Przepraszamy, wystąpił nieoczekiwany błąd związany z twoją transakcją. "
                + "W najbliższym czasie pieniądze trafią z powrotem na twoje konto.",
            "multiplecard@gmail.com",
            "Zwrot - " + TEST_ORDER_ID);
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
