package com.thepapiok.multiplecard.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.thepapiok.multiplecard.collections.Promotion;
import com.thepapiok.multiplecard.dto.PromotionDTO;
import com.thepapiok.multiplecard.repositories.PromotionRepository;
import java.time.LocalDate;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PromotionConverterTest {
  private static final ObjectId TEST_PRODUCT_ID = new ObjectId("123456789012345678901234");
  private Promotion promotion;
  private PromotionDTO promotionDTO;
  @Mock private PromotionRepository promotionRepository;
  private PromotionConverter promotionConverter;

  @BeforeEach
  public void setUp() {
    final int amount = 3422;
    final ObjectId promotionId = new ObjectId("723456229012345678901231");
    final LocalDate startAt = LocalDate.of(2024, 12, 5);
    final LocalDate expiredAt = LocalDate.of(2026, 9, 24);
    MockitoAnnotations.openMocks(this);
    promotionConverter = new PromotionConverter(promotionRepository);
    promotion = new Promotion();
    promotion.setCount(null);
    promotion.setAmount(amount);
    promotion.setProductId(TEST_PRODUCT_ID);
    promotion.setStartAt(startAt);
    promotion.setExpiredAt(expiredAt);
    promotion.setId(promotionId);
    promotionDTO = new PromotionDTO();
    promotionDTO.setCount("");
    promotionDTO.setAmount("34.22");
    promotionDTO.setProductId(TEST_PRODUCT_ID.toString());
    promotionDTO.setStartAt(startAt);
    promotionDTO.setExpiredAt(expiredAt);
  }

  @Test
  public void shouldReturnPromotionDTOAtGetDTOWhenEverythingOk() {
    assertEquals(promotionDTO, promotionConverter.getDTO(promotion));
  }

  @Test
  public void shouldReturnPromotionAtGetEntityWhenPromotionExists() {
    when(promotionRepository.findByProductId(TEST_PRODUCT_ID)).thenReturn(promotion);

    assertEquals(promotion, promotionConverter.getEntity(promotionDTO));
  }

  @Test
  public void shouldReturnPromotionAtGetEntityWhenPromotionNotExists() {
    Promotion expectedPromotion = new Promotion();
    expectedPromotion.setCount(null);
    expectedPromotion.setAmount(promotion.getAmount());
    expectedPromotion.setProductId(promotion.getProductId());
    expectedPromotion.setStartAt(promotion.getStartAt());
    expectedPromotion.setExpiredAt(promotion.getExpiredAt());

    when(promotionRepository.findByProductId(TEST_PRODUCT_ID)).thenReturn(null);

    assertEquals(expectedPromotion, promotionConverter.getEntity(promotionDTO));
  }
}
