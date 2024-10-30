package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.thepapiok.multiplecard.collections.Promotion;
import com.thepapiok.multiplecard.repositories.PromotionRepository;
import java.time.LocalDate;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PromotionServiceTest {

  @Mock private PromotionRepository promotionRepository;
  private PromotionService promotionService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    promotionService = new PromotionService(promotionRepository);
  }

  @Test
  public void shouldReturnPromotionAtGetPromotion() {
    final String id = "123456789012345678901234";
    final ObjectId productId = new ObjectId(id);
    final int amount = 500;
    final int testYearOfStartAt = 2024;
    final int testMonthOfStartAt = 1;
    final int testDayOfStartAt = 1;
    final int testYearOfExpiredAt = 2025;
    final int testMonthOfExpiredAt = 2;
    final int testDayOfExpiredAt = 2;
    Promotion expectedPromotion = new Promotion();
    expectedPromotion.setId(new ObjectId("029956781012345678403259"));
    expectedPromotion.setProductId(productId);
    expectedPromotion.setAmount(amount);
    expectedPromotion.setStartAt(
        LocalDate.of(testYearOfStartAt, testMonthOfStartAt, testDayOfStartAt));
    expectedPromotion.setExpiredAt(
        LocalDate.of(testYearOfExpiredAt, testMonthOfExpiredAt, testDayOfExpiredAt));
    expectedPromotion.setCount(0);

    when(promotionRepository.findByProductId(productId)).thenReturn(expectedPromotion);

    assertEquals(expectedPromotion, promotionService.getPromotion(id));
  }
}
