package com.thepapiok.multiplecard.services;

import com.thepapiok.multiplecard.collections.Promotion;
import com.thepapiok.multiplecard.dto.PromotionDTO;
import com.thepapiok.multiplecard.misc.PromotionConverter;
import com.thepapiok.multiplecard.repositories.PromotionRepository;
import java.time.LocalDate;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PromotionService {

  private final PromotionRepository promotionRepository;
  private final PromotionConverter promotionConverter;

  @Autowired
  public PromotionService(
      PromotionRepository promotionRepository, PromotionConverter promotionConverter) {
    this.promotionRepository = promotionRepository;
    this.promotionConverter = promotionConverter;
  }

  public PromotionDTO getPromotionDTO(String id) {
    Promotion promotion = getPromotion(id);
    if (promotion == null) {
      return null;
    }
    return promotionConverter.getDTO(promotion);
  }

  public Promotion getPromotion(String id) {
    return promotionRepository.findByProductId(new ObjectId(id));
  }

  public boolean checkNewStartAtIsPresent(LocalDate startAt, String id) {
    Promotion promotion = getPromotion(id);
    if (promotion != null && startAt.isEqual(promotion.getStartAt())) {
      return true;
    }
    return !startAt.isBefore(LocalDate.now());
  }

  public boolean upsertPromotion(PromotionDTO promotionDTO) {
    try {
      Promotion promotion = promotionConverter.getEntity(promotionDTO);
      promotionRepository.save(promotion);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public boolean checkDateIsMaxNextYear(LocalDate date) {
    return !date.isAfter(LocalDate.now().plusYears(1));
  }

  public boolean deletePromotion(String id) {
    try {
      promotionRepository.deleteByProductId(new ObjectId(id));
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
