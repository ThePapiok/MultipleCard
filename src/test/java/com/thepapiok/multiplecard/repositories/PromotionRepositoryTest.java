package com.thepapiok.multiplecard.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.thepapiok.multiplecard.collections.Promotion;
import com.thepapiok.multiplecard.configs.DbConfig;
import java.time.LocalDate;
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
public class PromotionRepositoryTest {
  private Promotion promotion;
  @Autowired private PromotionRepository promotionRepository;
  @Autowired private MongoTemplate mongoTemplate;
  @MockBean private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() {
    final int newPrice = 300;
    promotion = new Promotion();
    promotion.setNewPrice(newPrice);
    promotion.setQuantity(1);
    promotion.setStartAt(LocalDate.now());
    promotion.setExpiredAt(LocalDate.now().plusDays(1));
    promotion.setProductId(new ObjectId("123456789012345678901234"));
    promotion = mongoTemplate.save(promotion);
  }

  @AfterEach
  public void cleanUp() {
    promotionRepository.deleteAll();
  }

  @Test
  public void shouldReturnPromotionWithOnlyQuantityFieldAtFindQuantityByIdWhenEverythingOk() {
    Promotion expectedPromotion = new Promotion();
    expectedPromotion.setQuantity(promotion.getQuantity());

    assertEquals(expectedPromotion, promotionRepository.findQuantityById(promotion.getId()));
  }

  @Test
  public void
      shouldReturnPromotionWithOnlyNewPriceFieldAtFindNewPriceByProductIdWhenEverythingOk() {
    Promotion expectedPromotion = new Promotion();
    expectedPromotion.setNewPrice(promotion.getNewPrice());

    assertEquals(
        expectedPromotion, promotionRepository.findNewPriceByProductId(promotion.getProductId()));
  }

  @Test
  public void shouldReturnPromotionWithOnlyIdFieldAtFindIdByProductIdWhenEverythingOk() {
    Promotion expectedPromotion = new Promotion();
    expectedPromotion.setId(promotion.getId());

    assertEquals(
        expectedPromotion, promotionRepository.findIdByProductId(promotion.getProductId()));
  }
}
