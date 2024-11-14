package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mongodb.MongoWriteException;
import com.thepapiok.multiplecard.collections.Promotion;
import com.thepapiok.multiplecard.dto.PromotionDTO;
import com.thepapiok.multiplecard.misc.PromotionConverter;
import com.thepapiok.multiplecard.repositories.PromotionRepository;
import java.time.LocalDate;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PromotionServiceTest {
  private static final String TEST_ID = "123456789012345678901234";
  private static final ObjectId TEST_PRODUCT_ID = new ObjectId(TEST_ID);
  private Promotion expectedPromotion;
  @Mock private PromotionRepository promotionRepository;
  @Mock private PromotionConverter promotionConverter;
  private PromotionService promotionService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    promotionService = new PromotionService(promotionRepository, promotionConverter);
    final int amount = 500;
    final int testYearOfExpiredAt = 2025;
    final int testMonthOfExpiredAt = 2;
    final int testDayOfExpiredAt = 2;
    expectedPromotion = new Promotion();
    expectedPromotion.setId(new ObjectId("029956781012345678403259"));
    expectedPromotion.setProductId(TEST_PRODUCT_ID);
    expectedPromotion.setAmount(amount);
    expectedPromotion.setStartAt(LocalDate.now());
    expectedPromotion.setExpiredAt(
        LocalDate.of(testYearOfExpiredAt, testMonthOfExpiredAt, testDayOfExpiredAt));
    expectedPromotion.setCount(0);
  }

  @Test
  public void shouldReturnPromotionDTOAtGetPromotionDTOWhenEverythingOk() {
    PromotionDTO expectedPromotionDTO = new PromotionDTO();
    expectedPromotionDTO.setProductId(expectedPromotion.getProductId().toString());
    expectedPromotionDTO.setAmount(String.valueOf(expectedPromotion.getAmount()));
    expectedPromotionDTO.setCount(String.valueOf(expectedPromotion.getCount()));
    expectedPromotionDTO.setStartAt(expectedPromotion.getStartAt());
    expectedPromotionDTO.setExpiredAt(expectedPromotion.getExpiredAt());

    when(promotionRepository.findByProductId(TEST_PRODUCT_ID)).thenReturn(expectedPromotion);
    when(promotionConverter.getDTO(expectedPromotion)).thenReturn(expectedPromotionDTO);

    assertEquals(expectedPromotionDTO, promotionService.getPromotionDTO(TEST_ID));
  }

  @Test
  public void shouldReturnNullAtGetPromotionDTOWhenNotFound() {
    when(promotionRepository.findByProductId(TEST_PRODUCT_ID)).thenReturn(null);

    assertNull(promotionService.getPromotionDTO(TEST_ID));
  }

  @Test
  public void shouldReturnPromotionAtGetPromotionWhenEverythingOk() {
    when(promotionRepository.findByProductId(TEST_PRODUCT_ID)).thenReturn(expectedPromotion);

    assertEquals(expectedPromotion, promotionService.getPromotion(TEST_ID));
  }

  @Test
  public void
      shouldReturnTrueAtCheckNewStartAtIsPresentWhenNoExistsPromotionAlreadyAndStartAtIsNotFromPast() {
    when(promotionRepository.findByProductId(TEST_PRODUCT_ID)).thenReturn(null);

    assertTrue(promotionService.checkNewStartAtIsPresent(LocalDate.now(), TEST_ID));
  }

  @Test
  public void
      shouldReturnFalseAtCheckNewStartAtIsPresentWhenNoExistsPromotionAlreadyAndStartAtIsFromPast() {
    when(promotionRepository.findByProductId(TEST_PRODUCT_ID)).thenReturn(null);

    assertFalse(promotionService.checkNewStartAtIsPresent(LocalDate.now().minusDays(1), TEST_ID));
  }

  @Test
  public void shouldReturnTrueAtCheckNewStartAtIsPresentWhenExistsPromotionAlreadyAndIsEquals() {
    when(promotionRepository.findByProductId(TEST_PRODUCT_ID)).thenReturn(expectedPromotion);

    assertTrue(promotionService.checkNewStartAtIsPresent(LocalDate.now(), TEST_ID));
  }

  @Test
  public void
      shouldReturnTrueAtCheckNewStartAtIsPresentWhenExistsPromotionAlreadyAndStartAtIsNotFromPast() {
    when(promotionRepository.findByProductId(TEST_PRODUCT_ID)).thenReturn(expectedPromotion);

    assertTrue(promotionService.checkNewStartAtIsPresent(LocalDate.now().plusYears(1), TEST_ID));
  }

  @Test
  public void
      shouldReturnFalseAtCheckNewStartAtIsPresentWhenExistsPromotionAlreadyAndStartAtIsFromPast() {
    when(promotionRepository.findByProductId(TEST_PRODUCT_ID)).thenReturn(expectedPromotion);

    assertFalse(promotionService.checkNewStartAtIsPresent(LocalDate.now().minusDays(1), TEST_ID));
  }

  @Test
  public void shouldReturnTrueAtUpsertPromotionWhenEverythingOk() {
    PromotionDTO promotionDTO = new PromotionDTO();
    promotionDTO.setProductId(expectedPromotion.getProductId().toString());
    promotionDTO.setAmount(String.valueOf(expectedPromotion.getAmount()));
    promotionDTO.setCount(String.valueOf(expectedPromotion.getCount()));
    promotionDTO.setStartAt(expectedPromotion.getStartAt());
    promotionDTO.setExpiredAt(expectedPromotion.getExpiredAt());

    when(promotionConverter.getEntity(promotionDTO)).thenReturn(expectedPromotion);

    assertTrue(promotionService.upsertPromotion(promotionDTO));
    verify(promotionRepository).save(expectedPromotion);
  }

  @Test
  public void shouldReturnTrueAtUpsertPromotionWhenGetException() {
    PromotionDTO promotionDTO = new PromotionDTO();
    promotionDTO.setProductId(expectedPromotion.getProductId().toString());
    promotionDTO.setAmount(String.valueOf(expectedPromotion.getAmount()));
    promotionDTO.setCount(String.valueOf(expectedPromotion.getCount()));
    promotionDTO.setStartAt(expectedPromotion.getStartAt());
    promotionDTO.setExpiredAt(expectedPromotion.getExpiredAt());

    when(promotionConverter.getEntity(promotionDTO)).thenReturn(expectedPromotion);
    when(promotionRepository.save(expectedPromotion)).thenThrow(MongoWriteException.class);

    assertFalse(promotionService.upsertPromotion(promotionDTO));
    verify(promotionRepository).save(expectedPromotion);
  }

  @Test
  public void shouldReturnTrueAtCheckDateIsMaxNextYearWhenEverythingOk() {
    final LocalDate date = LocalDate.now().plusYears(1);

    assertTrue(promotionService.checkDateIsMaxNextYear(date));
  }

  @Test
  public void shouldReturnFalseAtCheckDateIsMaxNextYearWhenDateIsToFar() {
    final LocalDate date = LocalDate.now().plusYears(1).plusDays(1);

    assertFalse(promotionService.checkDateIsMaxNextYear(date));
  }

  @Test
  public void shouldReturnTrueAtDeletePromotionWhenEverythingOk() {
    assertTrue(promotionService.deletePromotion(TEST_ID));
  }

  @Test
  public void shouldReturnFalseAtDeletePromotionWhenGetException() {
    doThrow(MongoWriteException.class).when(promotionRepository).deleteByProductId(TEST_PRODUCT_ID);

    assertFalse(promotionService.deletePromotion(TEST_ID));
  }
}
