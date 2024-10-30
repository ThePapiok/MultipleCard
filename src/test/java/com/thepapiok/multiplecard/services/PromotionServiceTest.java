package com.thepapiok.multiplecard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;
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
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

public class PromotionServiceTest {
  private static final String TEST_ID = "123456789012345678901234";
  private static final LocalDate TEST_DATE = LocalDate.of(2024, 1, 1);
  private static final LocalDate TEST_DATE1 = LocalDate.of(2025, 1, 1);
  private static final LocalDate TEST_DATE2 = LocalDate.of(2023, 1, 1);
  private static final ObjectId TEST_PRODUCT_ID = new ObjectId(TEST_ID);
  private static MockedStatic<LocalDate> localDateMockedStatic;
  @Mock private PromotionRepository promotionRepository;
  @Mock private PromotionConverter promotionConverter;
  private PromotionService promotionService;
  private Promotion expectedPromotion;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    promotionService = new PromotionService(promotionRepository, promotionConverter);
    final int amount = 500;
    final int testYearOfStartAt = 2024;
    final int testMonthOfStartAt = 1;
    final int testDayOfStartAt = 1;
    final int testYearOfExpiredAt = 2025;
    final int testMonthOfExpiredAt = 2;
    final int testDayOfExpiredAt = 2;
    final int testYearOfDate1 = 2015;
    final int testMonthOfDate1 = 1;
    final int testDayOfDate1 = 1;
    final int testYearOfDate2 = 2016;
    final int testMonthOfDate2 = 1;
    final int testDayOfDate2 = 1;
    expectedPromotion = new Promotion();
    expectedPromotion.setId(new ObjectId("029956781012345678403259"));
    expectedPromotion.setProductId(TEST_PRODUCT_ID);
    expectedPromotion.setAmount(amount);
    expectedPromotion.setStartAt(
        LocalDate.of(testYearOfStartAt, testMonthOfStartAt, testDayOfStartAt));
    expectedPromotion.setExpiredAt(
        LocalDate.of(testYearOfExpiredAt, testMonthOfExpiredAt, testDayOfExpiredAt));
    expectedPromotion.setCount(0);
    localDateMockedStatic = mockStatic(LocalDate.class);
    localDateMockedStatic.when(LocalDate::now).thenReturn(TEST_DATE);
    localDateMockedStatic
        .when(() -> LocalDate.of(testYearOfDate1, testMonthOfDate1, testDayOfDate1))
        .thenReturn(TEST_DATE1);
    localDateMockedStatic
        .when(() -> LocalDate.of(testYearOfDate2, testMonthOfDate2, testDayOfDate2))
        .thenReturn(TEST_DATE2);
  }

  @BeforeEach
  public void cleanUp() {
    localDateMockedStatic.close();
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

    assertTrue(promotionService.checkNewStartAtIsPresent(TEST_DATE1, TEST_ID));
  }

  @Test
  public void
      shouldReturnFalseAtCheckNewStartAtIsPresentWhenNoExistsPromotionAlreadyAndStartAtIsFromPast() {
    when(promotionRepository.findByProductId(TEST_PRODUCT_ID)).thenReturn(null);

    assertFalse(promotionService.checkNewStartAtIsPresent(TEST_DATE2, TEST_ID));
  }

  @Test
  public void shouldReturnTrueAtCheckNewStartAtIsPresentWhenExistsPromotionAlreadyAndIsEquals() {
    when(promotionRepository.findByProductId(TEST_PRODUCT_ID)).thenReturn(expectedPromotion);

    assertTrue(promotionService.checkNewStartAtIsPresent(TEST_DATE, TEST_ID));
  }

  @Test
  public void
      shouldReturnTrueAtCheckNewStartAtIsPresentWhenExistsPromotionAlreadyAndStartAtIsNotFromPast() {
    when(promotionRepository.findByProductId(TEST_PRODUCT_ID)).thenReturn(expectedPromotion);

    assertTrue(promotionService.checkNewStartAtIsPresent(TEST_DATE1, TEST_ID));
  }

  @Test
  public void
      shouldReturnFalseAtCheckNewStartAtIsPresentWhenExistsPromotionAlreadyAndStartAtIsFromPast() {
    when(promotionRepository.findByProductId(TEST_PRODUCT_ID)).thenReturn(expectedPromotion);

    assertFalse(promotionService.checkNewStartAtIsPresent(TEST_DATE2, TEST_ID));
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
    final int addedMonths = 9;
    final int addedDays = 29;

    assertTrue(
        promotionService.checkDateIsMaxNextYear(
            TEST_DATE1.plusMonths(addedMonths).plusDays(addedDays)));
  }

  @Test
  public void shouldReturnFalseAtCheckDateIsMaxNextYearWhenDateIsToFar() {
    final int addedMonths = 9;
    final int addedDays = 30;

    assertFalse(
        promotionService.checkDateIsMaxNextYear(
            TEST_DATE1.plusMonths(addedMonths).plusDays(addedDays)));
  }
}
